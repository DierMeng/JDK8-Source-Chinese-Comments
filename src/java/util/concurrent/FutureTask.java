package java.util.concurrent;
import java.util.concurrent.locks.LockSupport;

/**
 * 封装一个存放线程执行结果的变量，使用 AQS 的独占 API 实现线程对变量的独占访问，
 * 判断规则：
 *  线程没有执行完毕：call() 方法没有返回前，不能访问变量，
 *  或者是超时时间没到前不能访问变量(这就是 FutureTask 的 get 方法可以实现获取线程执行结果时，设置超时时间的原因)。
 *
 *  1.将一个 Callable 置为 FutureTask 的内置成员
 *  2.执行 Callable 中的 call 方法
 *  3.调用 futureTask.get(timeout, TimeUnit) 方法, 获取 call 的执行结果, 超时的话就报 TimeoutException
 *
 * FutureTask 既能当做一个 Runnable 直接被 Thread 执行，也能作为 Future 用来得到 Callable 的计算结果。
 * 一般配合 ExecutorService 来使用，也可以直接通过 Thread 来使用。
 */
public class FutureTask<V> implements RunnableFuture<V> {

    /**
     * 用来保存 FutureTask 内部的任务执行状态
     *
     * NEW -> COMPLETING -> NORMAL
     * NEW -> COMPLETING -> EXCEPTIONAL
     * NEW -> CANCELLED
     * NEW -> INTERRUPTING -> INTERRUPTED
     */
    private volatile int state;

    /**
     * 表示是个新的任务或者还没被执行完的任务。
     * 这是初始状态。
     */
    private static final int NEW          = 0;

    /**
     * 大于 1 即是完成状态。
     * 所有值大于 COMPLETING 的状态都表示任务已经执行完成(任务正常执行完成，任务执行异常或者任务被取消)。
     *
     * 任务已经执行完成或者执行任务的时候发生异常，但是任务执行结果或者异常原因还没有保存到 outcome 字段的时候，状
     * 态会从 NEW 变更到 COMPLETING。
     * 但是这个状态会时间会比较短，属于中间状态。
     */
    private static final int COMPLETING   = 1;

    /**
     * 任务已经执行完成并且任务执行结果已经保存到 outcome 字段，状态会从 COMPLETING 转换到 NORMAL。
     * 这是一个最终态。
     */
    private static final int NORMAL       = 2;

    /**
     * 任务执行发生异常并且异常原因已经保存到 outcome 字段中后，状态会从 COMPLETING 转换到 EXCEPTIONAL。
     * 这是一个最终态。
     */
    private static final int EXCEPTIONAL  = 3;

    /**
     * 任务还没开始执行或者已经开始执行但是还没有执行完成的时候，用户调用了 cancel(false) 方法取消任务且不中断任务执行线程，
     * 这个时候状态会从 NEW 转化为 CANCELLED 状态。
     * 这是一个最终态。
     */
    private static final int CANCELLED    = 4;

    /**
     * 任务还没开始执行或者已经执行但是还没有执行完成的时候，用户调用了 cancel(true) 方法取消任务并且要中断任务执行线程
     * 但是还没有中断任务执行线程之前，状态会从 NEW 转化为 INTERRUPTING。
     *
     * 这是一个中间状态。
     */
    private static final int INTERRUPTING = 5;

    /**
     * 调用 interrupt() 中断任务执行线程之后状态会从 INTERRUPTING 转换到 INTERRUPTED。
     * 这是一个最终态。
     */
    private static final int INTERRUPTED  = 6;

    /**
     * 用来保存底层的调用，在被执行完成以后会指向 null,接着会初始化 state 字段为 NEW。
     */
    private Callable<V> callable;
    /**
     * 用来保存任务执行结果，如果发生异常，则用来保存异常原因
     * */
    private Object outcome; // non-volatile, protected by state reads/writes
    /** The thread running the callable; CASed during run() */
    private volatile Thread runner;
    /** Treiber stack of waiting threads */
    private volatile WaitNode waiters;

    /**
     * Returns result or throws exception for completed task.
     *
     * @param s completed state value
     */
    @SuppressWarnings("unchecked")
    private V report(int s) throws ExecutionException {
        Object x = outcome;
        // 任务正常执行完成，返回任务执行结果
        if (s == NORMAL) {
            return (V) x;
        }
        // 任务被取消，抛出 CancellationException 异常
        if (s >= CANCELLED) {
            throw new CancellationException();
        }
        // 其他状态，抛出执行异常 ExecutionException
        throw new ExecutionException((Throwable)x);
    }

    /**
     * Creates a {@code FutureTask} that will, upon running, execute the
     * given {@code Callable}.
     *
     * @param  callable the callable task
     * @throws NullPointerException if the callable is null
     */
    public FutureTask(Callable<V> callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        this.callable = callable;
        this.state = NEW;       // ensure visibility of callable
    }

    /**
     * 把传入的 Runnable 封装成一个 Callable 对象保存在 callable 字段中，同时如果任务执行成功的话就会返回传入的 result。
     * 这种情况下如果不需要返回值的话可以传入一个 null。
     */
    public FutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);
        this.state = NEW;       // ensure visibility of callable
    }

    /**
     * 任务是否被取消
     */
    public boolean isCancelled() {
        return state >= CANCELLED;
    }

    /**
     * 任务是否执行完成
     */
    public boolean isDone() {
        return state != NEW;
    }

    /**
     * 1.判断任务当前执行状态，如果任务状态不为 NEW，则说明任务或者已经执行完成，或者执行异常，不能被取消，直接返回 false 表示执行失败。
     * 2.判断需要中断任务执行线程
     *  -- 把任务状态从 NEW 转化到 INTERRUPTING。这是个中间状态。
     *  -- 中断任务执行线程。
     *  -- 修改任务状态为INTERRUPTED。
     * 3.如果不需要中断任务执行线程，直接把任务状态从 NEW 转化为 CANCELLED。如果转化失败则返回 false 表示取消失败。
     * 4.调用 finishCompletion()。
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        // 如果任务已经结束，则直接返回 false
        if (!(state == NEW && UNSAFE.compareAndSwapInt(this, stateOffset, NEW, mayInterruptIfRunning ? INTERRUPTING : CANCELLED))) {
            return false;
        }
        try {
            // // 如果需要中断任务执行线程
            if (mayInterruptIfRunning) {
                try {
                    Thread t = runner;
                    // 中断任务执行线程
                    if (t != null) {
                        t.interrupt();
                    }
                } finally {
                    // 修改状态为 INTERRUPTED
                    UNSAFE.putOrderedInt(this, stateOffset, INTERRUPTED);
                }
            }
        } finally {
            finishCompletion();
        }
        return true;
    }

    /**
     * 任务发起线程可以调用 get() 方法来获取任务执行结果，如果此时任务已经执行完毕则会直接返回任务结果，
     * 如果任务还没执行完毕，则调用方会阻塞直到任务执行结束返回结果为止。
     *  1.判断任务当前的 state <= COMPLETING 是否成立。COMPLETING 状态是任务是否执行完成的临界状态。
     *  2.如果成立，表明任务还没有结束（这里的结束包括任务正常执行完毕，任务执行异常，任务被取消），则会调用 awaitDone() 进行阻塞等待。
     *  3.如果不成立表明任务已经结束，调用 report() 返回结果。
     */
    public V get() throws InterruptedException, ExecutionException {
        int s = state;
        if (s <= COMPLETING) {
            s = awaitDone(false, 0L);
        }
        return report(s);
    }

    /**
     * 带超时等待的获取任务结果
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (unit == null) {
            throw new NullPointerException();
        }
        int s = state;
        // 如果 awaitDone() 超时返回之后任务还没结束，则抛出异常
        if (s <= COMPLETING && (s = awaitDone(true, unit.toNanos(timeout))) <= COMPLETING) {
            throw new TimeoutException();
        }
        return report(s);
    }

    /**
     * Protected method invoked when this task transitions to state
     * {@code isDone} (whether normally or via cancellation). The
     * default implementation does nothing.  Subclasses may override
     * this method to invoke completion callbacks or perform
     * bookkeeping. Note that you can query status inside the
     * implementation of this method to determine whether this task
     * has been cancelled.
     */
    protected void done() { }

    /**
     * 1.首先会 CAS 的把当前的状态从 NEW 变更为 COMPLETING 状态。
     * 2.把任务执行结果保存在 outcome 字段中。
     * 3.CAS 的把当前任务状态从 COMPLETING 变更为 NORMAL。
     * 4.调用 finishCompletion()。
     */
    protected void set(V v) {
        if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
            outcome = v;
            UNSAFE.putOrderedInt(this, stateOffset, NORMAL); // final state
            finishCompletion();
        }
    }

    /**
     * 1.首先会 CAS 的把当前的状态从 NEW 变更为 COMPLETING（中间状态）状态。
     * 2.把异常原因保存在 outcome 字段中，outcome 字段用来保存任务执行结果或者异常原因。
     * 3.CAS 的把当前任务状态从 COMPLETING 变更为 EXCEPTIONAL。
     * 4.调用 finishCompletion()。
     */
    protected void setException(Throwable t) {
        if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
            outcome = t;
            UNSAFE.putOrderedInt(this, stateOffset, EXCEPTIONAL); // final state
            finishCompletion();
        }
    }

    /**
     * 1.判断当前任务的 state 是否等于 NEW，如果不为 NEW 则说明任务或者已经执行过，或者已经被取消，直接返回。
     * 2.如果状态为 NEW 则接着会通过 unsafe 类把任务执行线程引用 CAS 的保存在 runner 字段中，如果保存失败，则直接返回。
     * 3.执行任务
     * 4.如果任务执行发生异常，则调用 setException() 方法保存异常信息。否则执行 set() 设置结果和状态值。
     * 5.任务如果是被中断的，执行 handlePossibleCancellationInterrupt() 处理状态和中断响应。
     */
    public void run() {
        // 状态如果不是 NEW，说明任务或者已经执行过，或者已经被取消，直接返回
        // 状态如果是 NEW，则尝试把当前执行线程保存在 runner 字段（runnerOffset）中，如果赋值失败则直接返回
        if (state != NEW || !UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread())) {
            return;
        }
        try {
            Callable<V> c = callable;
            if (c != null && state == NEW) {
                V result;
                boolean ran;
                try {
                    // 执行任务，计算逻辑
                    result = c.call();
                    ran = true;
                } catch (Throwable ex) {
                    result = null;
                    ran = false;
                    // 任务异常
                    setException(ex);
                }
                if (ran) {
                    // 任务正常执行完毕
                    set(result);
                }
            }
        } finally {
            // runner must be non-null until state is settled to
            // prevent concurrent calls to run()
            runner = null;
            // state must be re-read after nulling runner to prevent
            // leaked interrupts
            int s = state;
            // 如果任务被中断，执行中断处理
            if (s >= INTERRUPTING) {
                handlePossibleCancellationInterrupt(s);
            }
        }
    }

    /**
     * Executes the computation without setting its result, and then
     * resets this future to initial state, failing to do so if the
     * computation encounters an exception or is cancelled.  This is
     * designed for use with tasks that intrinsically execute more
     * than once.
     *
     * @return {@code true} if successfully run and reset
     */
    protected boolean runAndReset() {
        if (state != NEW ||
            !UNSAFE.compareAndSwapObject(this, runnerOffset,
                                         null, Thread.currentThread()))
            return false;
        boolean ran = false;
        int s = state;
        try {
            Callable<V> c = callable;
            if (c != null && s == NEW) {
                try {
                    c.call(); // don't set result
                    ran = true;
                } catch (Throwable ex) {
                    setException(ex);
                }
            }
        } finally {
            // runner must be non-null until state is settled to
            // prevent concurrent calls to run()
            runner = null;
            // state must be re-read after nulling runner to prevent
            // leaked interrupts
            s = state;
            if (s >= INTERRUPTING)
                handlePossibleCancellationInterrupt(s);
        }
        return ran && s == NEW;
    }

    /**
     * Ensures that any interrupt from a possible cancel(true) is only
     * delivered to a task while in run or runAndReset.
     */
    private void handlePossibleCancellationInterrupt(int s) {
        // It is possible for our interrupter to stall before getting a
        // chance to interrupt us.  Let's spin-wait patiently.
        if (s == INTERRUPTING)
            while (state == INTERRUPTING)
                Thread.yield(); // wait out pending interrupt

        // assert state == INTERRUPTED;

        // We want to clear any interrupt we may have received from
        // cancel(true).  However, it is permissible to use interrupts
        // as an independent mechanism for a task to communicate with
        // its caller, and there is no way to clear only the
        // cancellation interrupt.
        //
        // Thread.interrupted();
    }

    /**
     * Simple linked list nodes to record waiting threads in a Treiber
     * stack.  See other classes such as Phaser and SynchronousQueue
     * for more detailed explanation.
     */
    static final class WaitNode {
        volatile Thread thread;
        volatile WaitNode next;
        WaitNode() { thread = Thread.currentThread(); }
    }

    /**
     * 不管是任务执行异常还是任务正常执行完毕，或者取消任务，最后都会调用 finishCompletion() 方法
     *
     * 依次遍历 waiters 链表，唤醒节点中的线程，然后把 callable 置空。
     *
     * 被唤醒的线程会各自从 awaitDone() 方法中的 LockSupport.park() 阻塞中返回，
     *  然后会进行新一轮的循环。在新一轮的循环中会返回执行结果（或者更确切的说是返回任务的状态）。
     */
    private void finishCompletion() {
        // assert state > COMPLETING;
        for (WaitNode q; (q = waiters) != null;) {
            if (UNSAFE.compareAndSwapObject(this, waitersOffset, q, null)) {
                for (;;) {
                    Thread t = q.thread;
                    if (t != null) {
                        q.thread = null;
                        LockSupport.unpark(t);
                    }
                    WaitNode next = q.next;
                    if (next == null) {
                        break;
                    }
                    q.next = null; // unlink to help gc
                    q = next;
                }
                break;
            }
        }

        done();

        callable = null;        // to reduce footprint
    }

    /**
     * 当调用 get() 获取任务结果但是任务还没执行完成的时候，调用线程会调用 awaitDone() 方法进行阻塞等待
     *
     *  1.判断调用 get() 的线程是否被其他线程中断，如果是的话则在等待队列中删除对应节点然后抛出 InterruptedException 异常。
     *  2.取任务当前状态，如果当前任务状态大于 COMPLETING 则表示任务执行完成，则把 thread 字段置 null 并返回结果。
     *  3.如果任务处于 COMPLETING 状态，则表示任务已经处理完成（正常执行完成或者执行出现异常），
     *      但是执行结果或者异常原因还没有保存到 outcome 字段中。这个时候调用线程让出执行权让其他线程优先执行。
     *  4.如果等待节点为空，则构造一个等待节点 WaitNode。
     *  5.如果第四步中新建的节点还没入队列，则 CAS 的把该节点加入 waiters 队列的首节点。
     *  6.阻塞等待。
     */
    private int awaitDone(boolean timed, long nanos) throws InterruptedException {
        // 计算等待截止时间
        final long deadline = timed ? System.nanoTime() + nanos : 0L;
        WaitNode q = null;
        boolean queued = false;
        for (;;) {
            // 判断阻塞线程是否被中断，如果被中断则在等待队列中删除该节点并抛出 InterruptedException 异常
            if (Thread.interrupted()) {
                removeWaiter(q);
                throw new InterruptedException();
            }
            // 获取当前状态，如果状态大于 COMPLETING
            // 说明任务已经结束（要么正常结束，要么异常结束，要么被取消）
            // 则把 thread 显示置空，并返回结果
            int s = state;
            if (s > COMPLETING) {
                if (q != null)
                    q.thread = null;
                return s;
            }
            // 如果状态处于中间状态 COMPLETING
            // 表示任务已经结束但是任务执行线程还没来得及给 outcome 赋值
            // 这个时候让出执行权让其他线程优先执行
            else if (s == COMPLETING) { // cannot time out yet
                Thread.yield();
            }
            // 如果等待节点为空，则构造一个等待节点
            else if (q == null) {
                q = new WaitNode();
            }
            // 如果还没有入队列，则把当前节点加入 waiters 首节点并替换原来 waiters
            else if (!queued) {
                queued = UNSAFE.compareAndSwapObject(this, waitersOffset, q.next = waiters, q);
            }
            else if (timed) {
                // 如果需要等待特定时间，则先计算要等待的时间
                // 如果已经超时，则删除对应节点并返回对应的状态
                nanos = deadline - System.nanoTime();
                if (nanos <= 0L) {
                    removeWaiter(q);
                    return state;
                }
                // 阻塞等待特定时间
                LockSupport.parkNanos(this, nanos);
            }
            else {
                // 阻塞等待直到被其他线程唤醒
                LockSupport.park(this);
            }
        }
    }

    /**
     * Tries to unlink a timed-out or interrupted wait node to avoid
     * accumulating garbage.  Internal nodes are simply unspliced
     * without CAS since it is harmless if they are traversed anyway
     * by releasers.  To avoid effects of unsplicing from already
     * removed nodes, the list is retraversed in case of an apparent
     * race.  This is slow when there are a lot of nodes, but we don't
     * expect lists to be long enough to outweigh higher-overhead
     * schemes.
     */
    private void removeWaiter(WaitNode node) {
        if (node != null) {
            node.thread = null;
            retry:
            for (;;) {          // restart on removeWaiter race
                for (WaitNode pred = null, q = waiters, s; q != null; q = s) {
                    s = q.next;
                    if (q.thread != null)
                        pred = q;
                    else if (pred != null) {
                        pred.next = s;
                        if (pred.thread == null) // check for race
                            continue retry;
                    }
                    else if (!UNSAFE.compareAndSwapObject(this, waitersOffset,
                                                          q, s))
                        continue retry;
                }
                break;
            }
        }
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long stateOffset;
    private static final long runnerOffset;
    private static final long waitersOffset;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> k = FutureTask.class;
            stateOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("state"));
            runnerOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("runner"));
            waitersOffset = UNSAFE.objectFieldOffset
                (k.getDeclaredField("waiters"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
