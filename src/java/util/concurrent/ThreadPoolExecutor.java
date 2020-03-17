package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;

/**
 * 线程池管理执行类
 */
public class ThreadPoolExecutor extends AbstractExecutorService {

    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));

    // Integer 共有 32 位，最右边 29 位表示工作线程数，最左边 3 位表示线程池状态
    // 线程池的状态用高 3 位表示，其中包括了符号位
    private static final int COUNT_BITS = Integer.SIZE - 3;

    // 000-111111111111111111111111111111
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // 以下这五种状态依次为从小到大
    // 十进制值：-536870912
    // 此状态表示线程池可以接受新任务
    private static final int RUNNING    = -1 << COUNT_BITS;
    // 十进制值：0
    // 此状态不再接受新任务，但可以继续执行队列中的任务
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    // 十进制值：536870912
    // 此状态全面拒绝，并中断正在处理的任务
    private static final int STOP       =  1 << COUNT_BITS;
    // 十进制值：1073741824
    // 此状态表示所有任务已经被终止
    private static final int TIDYING    =  2 << COUNT_BITS;
    // 十进制值：1610612736
    // 此状态表示已清理完现场
    private static final int TERMINATED =  3 << COUNT_BITS;

    // 表示线程池当前处于 STOP（全面拒绝，并中断正在处理的任务） 状态
    private static int runStateOf(int c)     { return c & ~CAPACITY; }

    // 工作线程数
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    /**
     * 线程加一操作
     * 原子性操作
     * 执行失败的概率非常低。即使失败，再次执行时成功的概率也是极高的，类似于自旋锁原理。
     * 这里的处理逻辑是先加 1，创建失败再减 1，轻量处理并发创建线程的方式
     */
    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }

    private void decrementWorkerCount() {
        do {} while (! compareAndDecrementWorkerCount(ctl.get()));
    }

    private final BlockingQueue<Runnable> workQueue;

    private final ReentrantLock mainLock = new ReentrantLock();

    private final HashSet<Worker> workers = new HashSet<Worker>();

    private final Condition termination = mainLock.newCondition();

    /**
     * 整个线程池在运行期间的最大并发任务个数
     * 可以用作线程池的监控
     */
    private int largestPoolSize;

    /**
     * 线程池在运行过程中已完成的任务数量，小于或等于线程池需要执行的任务数量
     * 可以用作线程池的监控
     */
    private long completedTaskCount;

    private volatile ThreadFactory threadFactory;

    private volatile RejectedExecutionHandler handler;

    private volatile long keepAliveTime;

    /**
     * 是否允许核心线程超时
     * true：核心线程超时也会被回收
     * false：默认不被回收
     */
    private volatile boolean allowCoreThreadTimeOut;

    private volatile int corePoolSize;

    private volatile int maximumPoolSize;

    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");

    private final AccessControlContext acc;

    /**
     * 内部常量类，工作线程的核心类实现
     * 实现 Runnable 接口，并吧本对象作为参数输入给 run() 方法中的 runWorker(this)
     * 所以内部属性线程 thread 在 start 的时候，即会调用 runWorker 方法
     */
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {

        private static final long serialVersionUID = 6138294804551838833L;

        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;

        /**
         * 构造
         */
        Worker(Runnable firstTask) {
            // 它是 AQS(AbstractQueuedSynchronizer) 的方法
            //在 runWorker 方法执行执行禁止线程被中断
            setState(-1);
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        /**
         * 当 thread 被 start() 之后，执行 runWorker 的方法
         */
        public void run() {
            runWorker(this);
        }

        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock()        { acquire(1); }
        public boolean tryLock()  { return tryAcquire(1); }
        public void unlock()      { release(1); }
        public boolean isLocked() { return isHeldExclusively(); }

        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }
    }

    private void advanceRunState(int targetState) {
        for (;;) {
            int c = ctl.get();
            if (runStateAtLeast(c, targetState) ||
                ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))))
                break;
        }
    }

    final void tryTerminate() {
        for (;;) {
            int c = ctl.get();
            if (isRunning(c) ||
                runStateAtLeast(c, TIDYING) ||
                (runStateOf(c) == SHUTDOWN && ! workQueue.isEmpty()))
                return;
            if (workerCountOf(c) != 0) { // Eligible to terminate
                interruptIdleWorkers(ONLY_ONE);
                return;
            }

            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
                    try {
                        terminated();
                    } finally {
                        ctl.set(ctlOf(TERMINATED, 0));
                        termination.signalAll();
                    }
                    return;
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                for (Worker w : workers)
                    security.checkAccess(w.thread);
            } finally {
                mainLock.unlock();
            }
        }
    }

    private void interruptWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers)
                w.interruptIfStarted();
        } finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers(boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    } catch (SecurityException ignore) {
                    } finally {
                        w.unlock();
                    }
                }
                if (onlyOne)
                    break;
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers() {
        interruptIdleWorkers(false);
    }

    private static final boolean ONLY_ONE = true;

    final void reject(Runnable command) {
        handler.rejectedExecution(command, this);
    }

    void onShutdown() {
    }

    final boolean isRunningOrShutdown(boolean shutdownOK) {
        int rs = runStateOf(ctl.get());
        return rs == RUNNING || (rs == SHUTDOWN && shutdownOK);
    }

    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = workQueue;
        ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r))
                    taskList.add(r);
            }
        }
        return taskList;
    }

    /**
     * 根据当前线程池状态，检查是否可以添加新的任务线程。
     * 如果可以就创建并启动任务，一切正常会返回 true。
     * 返回 false 的情况：
     *  1.线程池没有处于 RIUNNING 状态
     *  2.线程工厂创建新的任务线程失败
     *
     * firstTask：外部启动线程池时需要构造的第一个线程，它是线程的母体
     * core：新增工作线程时的判断指标
     *  true：表示新增工作线程时，需要判断当前 RUNNING 状态的线程是否少于 corePoolSize(常驻核心线程数)
     *  false：表示新增工作线程时，需要判断当前 RUNNING 状态的线程是否少于 maximumPoolSize(线程池能够容纳同时执行的最大线程数)
     */
    private boolean addWorker(Runnable firstTask, boolean core) {

        // 不需要任务预定义的语法标签，响应下文的 continue retry，快速退出多层嵌套循环
        // 此中写法类似于 goto 作用，目的是早实现多重循环时能够快速退出到任何一层
        retry:
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // 如果 rs 是 RUNNING 状态，则不执行后面的判断，继续执行下面的代码
            // 如果是 STOP 及以上的状态，或者母线程不为空、或者队列为空，都返回失败
            /*boolean isNotAllowedToCreateTask = rs >= SHUTDOWN && ! (rs == SHUTDOWN && firstTask == null && ! workQueue.isEmpty());
            if(isNotAllowedToCreateTask) {
                return fasle;
            }*/
            if (rs >= SHUTDOWN && ! (rs == SHUTDOWN && firstTask == null && ! workQueue.isEmpty())) {
                return false;
            }

            for (;;) {
                int wc = workerCountOf(c);
                // 如果超过最大允许线程数则不能再添加新的线程
                // 最大线程数不能超过 2^29
                if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize)) {
                    return false;
                }
                // 将当前活动线程数加一(原子性操作)
                if (compareAndIncrementWorkerCount(c)) {
                    // 表示直接跳出与 retry 相邻的这个循环体
                    break retry;
                }
                // 线程池状态和工作线程数是可变化的，需要经常提取这个最新值
                c = ctl.get();
                // 如果已经关闭（不再接受新任务，但可以继续执行队列中的任务），则再次从 retry 标签处进入，重新判断
                if (runStateOf(c) != rs)
                    // 跳转至标签处，继续执行循环
                    // 如果条件为假，则说明线程池还处于运行状态，继续在 for (;;) 循环内执行
                    // 如果线程还是处于 RUNNING 状态，那就说明原子性加一的操作失败
                    continue retry;
            }
        }

        // 开始创建工作线程
        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            // 利用 Worker 构造方法中的线程池工厂创建线程，并封装工作线程 Worker 对象
            w = new Worker(firstTask);
            // 这是 Worker 中的属性对象 thread
            final Thread t = w.thread;
            if (t != null) {
                // 在进行 ThreadPoolExecutor 的敏感操作时
                // 需要持有主锁，避免在添加和启动线程时被干扰
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    int rs = runStateOf(ctl.get());

                    // 当线程池为 RUNNING 或 SHUTDOWN 且 firstTask 出事线程为空时
                    if (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) {
                            throw new IllegalThreadStateException();
                        }
                        workers.add(w);
                        int s = workers.size();
                        // 整个线程池在运行期间的最大并发任务个数
                        if (s > largestPoolSize) {
                            largestPoolSize = s;
                        }
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    // 此处并非线程池的 execute 的 command 参数指向的线程
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted) {
                // 如果线程启动失败，把上面原子性操作加一的工作线程计数再减回去
                addWorkerFailed(w);
            }
        }
        return workerStarted;
    }

    /**
     * 如果线程启动失败，把创建线程时的原子性操作加一的工作线程计数再减回去
     */
    private void addWorkerFailed(Worker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (w != null)
                workers.remove(w);
            decrementWorkerCount();
            tryTerminate();
        } finally {
            mainLock.unlock();
        }
    }

    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) // If abrupt, then workerCount wasn't adjusted
            decrementWorkerCount();

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += w.completedTasks;
            workers.remove(w);
        } finally {
            mainLock.unlock();
        }

        tryTerminate();

        int c = ctl.get();
        if (runStateLessThan(c, STOP)) {
            if (!completedAbruptly) {
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                if (min == 0 && ! workQueue.isEmpty())
                    min = 1;
                if (workerCountOf(c) >= min)
                    return; // replacement not needed
            }
            addWorker(null, false);
        }
    }

    private Runnable getTask() {
        boolean timedOut = false; // Did the last poll() time out?

        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            int wc = workerCountOf(c);

            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            if ((wc > maximumPoolSize || (timed && timedOut))
                && (wc > 1 || workQueue.isEmpty())) {
                if (compareAndDecrementWorkerCount(c))
                    return null;
                continue;
            }

            try {
                Runnable r = timed ?
                    workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                    workQueue.take();
                if (r != null)
                    return r;
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

    /**
     * Worker 对象作为输入参数，Worker 内部属性线程调用 start 的时候就会执行此方法。
     */
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                if ((runStateAtLeast(ctl.get(), STOP) ||
                     (Thread.interrupted() &&
                      runStateAtLeast(ctl.get(), STOP))) &&
                    !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

    public ThreadPoolExecutor(
            int corePoolSize, // 常驻核心线程数
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), defaultHandler);
    }

    public ThreadPoolExecutor(
            int corePoolSize, // 常驻核心线程数
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             threadFactory, defaultHandler);
    }

    public ThreadPoolExecutor(
            int corePoolSize, // 常驻核心线程数
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), handler);
    }

    /**
     * 线程池构造方法
     */
    public ThreadPoolExecutor(
            // 常驻核心线程数
            // 如果等于 0，则任务执行完之后，没有任何请求进入时销毁线程池的线程
            // 如果大于 0，即使本地任务执行完毕，核心线程也不会被销毁
            // 设置过大会浪费资源，设置过小会导致线程频繁的创建或销毁
            int corePoolSize,
            // 线程池能够容纳同时执行的最大线程数
            // 必须大于等于 1
            // 如果待执行的线程数大于次数，需要借助 workQueue 参数，缓存在队列中
            // 如果此数与 corePoolSize 相等，表示线程池的大小是固定的
            int maximumPoolSize,
            // 线程池中的线程空闲时间
            // 当空闲时间达到这个值的时候，线程会被销毁，知道只剩下 corePoolSize 个线程为止，避免浪费内存和句柄资源
            // 默认当线程池的线程数大于 corePoolSize 时，此参数才会起作用
            // 当 变量设置为 true 时，核心线程超时也会被回收
            long keepAliveTime,
            TimeUnit unit, // 时间单位，通常是 TimeUnit.SECONDS
            // 缓存队列
            // 当请求的线程数大于 maximumPoolSize 时，线程进入 BlockingQueue 阻塞队列
            BlockingQueue<Runnable> workQueue,
            // 线程工厂
            // 用来生产一组相同任务的线程
            // 线程池的命名就是通过给这个 factory 增加组名前缀实现
            ThreadFactory threadFactory,
            // 执行拒绝策略的对象，限流保护
            // 当 workQueue 的任务缓存区上限的时候，就通过该策略处理请求
            RejectedExecutionHandler handler) {

        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0) {
            throw new IllegalArgumentException();
        }

        // 要求队列、线程工厂、拒绝处理服务必须有实例对象
        if (workQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException();
        }

        this.acc = System.getSecurityManager() == null ?
                null :
                AccessController.getContext();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    /**
     * 线程池的执行
     * 步骤及思路
     *  1.如果当前运行的线程少于 corePoolSize（常驻核心线程数），则创建新线程来执行任务（注意，执行这一步骤需要获取全局锁）
     *  2.如果运行的线程等于或多于 corePoolSize，则将任务加入 BlockingQueue 队列
     *  3.如果队列已满，则创建新的线程来处理任务（注意，执行这一步骤需要获取全局锁）
     *  4.如果创建新线程将使当前运行的线程超出 maximumPoolSize（线程池能够容纳同时执行的最大线程数），任务将被拒绝，并调用 RejectedExecutionHandler.rejectedExecution() 方法执行拒绝策略
     */
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();

        // 返回包含线程数及线程池状态的 Integer 类型数值
        int c = ctl.get();
        // 如果工作线程数小于核心线程数，则创建线程任务并执行
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true)) {
                return;
            }
            // 如果创建失败，防止外部已经在线程池中加入新任务，重新获取线程数及线程池状态
            c = ctl.get();
        }
        // 只有线程池处于 RUNNING（线程池可以接受新任务） 状态，才执行置入队列的操作
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            // 如果线程池不是 RUNNING 状态，则将刚加入队列的任务移除
            if (! isRunning(recheck) && remove(command)) {
                reject(command);
            }
            // 如果之前的线程已被消费完，新建一个线程
            else if (workerCountOf(recheck) == 0) {
                addWorker(null, false);
            }
        }
        // 核心池和队列都已经满了，尝试创建一个新线程
        else if (!addWorker(command, false)) {
            // 如果 addWorker 返回的是 false，表示创建失败，就会唤醒拒绝策略
            reject(command);
        }
    }

    /**
     *  线程池关闭的思路：
     *      遍历线程池中的工作线程
     *      逐个调用线程的 interrupt 方法来中断线程
     *      无法响应中断的任务可能永远无法终止
     *  只是将线程池的状态设置成 SHUTDOWN（不再接受新任务，但可以继续执行队列中的任务） 状态，然后中断所有没有正在执行任务的线程
     */
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();
            onShutdown();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }

    /**
     * 首先将线程池的状态设置成 STOP（全面拒绝，并中断正在处理的任务），然后尝试停止所有的正在执行或暂停任务的线程，并且返回等待执行任务的列表。
     */
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(STOP);
            interruptWorkers();
            tasks = drainQueue();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
        return tasks;
    }

    public boolean isShutdown() {
        return ! isRunning(ctl.get());
    }

    public boolean isTerminating() {
        int c = ctl.get();
        return ! isRunning(c) && runStateLessThan(c, TERMINATED);
    }

    public boolean isTerminated() {
        return runStateAtLeast(ctl.get(), TERMINATED);
    }

    public boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (;;) {
                if (runStateAtLeast(ctl.get(), TERMINATED))
                    return true;
                if (nanos <= 0)
                    return false;
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * Invokes {@code shutdown} when this executor is no longer
     * referenced and it has no threads.
     */
    protected void finalize() {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null || acc == null) {
            shutdown();
        } else {
            PrivilegedAction<Void> pa = () -> { shutdown(); return null; };
            AccessController.doPrivileged(pa, acc);
        }
    }

    /**
     * Sets the thread factory used to create new threads.
     *
     * @param threadFactory the new thread factory
     * @throws NullPointerException if threadFactory is null
     * @see #getThreadFactory
     */
    public void setThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null)
            throw new NullPointerException();
        this.threadFactory = threadFactory;
    }

    /**
     * Returns the thread factory used to create new threads.
     *
     * @return the current thread factory
     * @see #setThreadFactory(ThreadFactory)
     */
    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    /**
     * Sets a new handler for unexecutable tasks.
     *
     * @param handler the new handler
     * @throws NullPointerException if handler is null
     * @see #getRejectedExecutionHandler
     */
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (handler == null)
            throw new NullPointerException();
        this.handler = handler;
    }

    /**
     * Returns the current handler for unexecutable tasks.
     *
     * @return the current handler
     * @see #setRejectedExecutionHandler(RejectedExecutionHandler)
     */
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return handler;
    }

    /**
     * Sets the core number of threads.  This overrides any value set
     * in the constructor.  If the new value is smaller than the
     * current value, excess existing threads will be terminated when
     * they next become idle.  If larger, new threads will, if needed,
     * be started to execute any queued tasks.
     *
     * @param corePoolSize the new core size
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     * @see #getCorePoolSize
     */
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0)
            throw new IllegalArgumentException();
        int delta = corePoolSize - this.corePoolSize;
        this.corePoolSize = corePoolSize;
        if (workerCountOf(ctl.get()) > corePoolSize)
            interruptIdleWorkers();
        else if (delta > 0) {
            // We don't really know how many new threads are "needed".
            // As a heuristic, prestart enough new workers (up to new
            // core size) to handle the current number of tasks in
            // queue, but stop if queue becomes empty while doing so.
            int k = Math.min(delta, workQueue.size());
            while (k-- > 0 && addWorker(null, true)) {
                if (workQueue.isEmpty())
                    break;
            }
        }
    }

    /**
     * Returns the core number of threads.
     *
     * @return the core number of threads
     * @see #setCorePoolSize
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * Starts a core thread, causing it to idly wait for work. This
     * overrides the default policy of starting core threads only when
     * new tasks are executed. This method will return {@code false}
     * if all core threads have already been started.
     *
     * @return {@code true} if a thread was started
     */
    public boolean prestartCoreThread() {
        return workerCountOf(ctl.get()) < corePoolSize &&
            addWorker(null, true);
    }

    /**
     * Same as prestartCoreThread except arranges that at least one
     * thread is started even if corePoolSize is 0.
     */
    void ensurePrestart() {
        int wc = workerCountOf(ctl.get());
        if (wc < corePoolSize)
            addWorker(null, true);
        else if (wc == 0)
            addWorker(null, false);
    }

    /**
     * 如果调用此方法
     * 线程池会提前创建并启动所有常驻核心线程，也叫基本线程
     */
    public int prestartAllCoreThreads() {
        int n = 0;
        while (addWorker(null, true)) {
            ++n;
        }
        return n;
    }

    public boolean allowsCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public void allowCoreThreadTimeOut(boolean value) {
        if (value && keepAliveTime <= 0)
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        if (value != allowCoreThreadTimeOut) {
            allowCoreThreadTimeOut = value;
            if (value)
                interruptIdleWorkers();
        }
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < corePoolSize)
            throw new IllegalArgumentException();
        this.maximumPoolSize = maximumPoolSize;
        if (workerCountOf(ctl.get()) > maximumPoolSize)
            interruptIdleWorkers();
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setKeepAliveTime(long time, TimeUnit unit) {
        if (time < 0)
            throw new IllegalArgumentException();
        if (time == 0 && allowsCoreThreadTimeOut())
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        long keepAliveTime = unit.toNanos(time);
        long delta = keepAliveTime - this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if (delta < 0)
            interruptIdleWorkers();
    }

    public long getKeepAliveTime(TimeUnit unit) {
        return unit.convert(keepAliveTime, TimeUnit.NANOSECONDS);
    }

    public BlockingQueue<Runnable> getQueue() {
        return workQueue;
    }

    public boolean remove(Runnable task) {
        boolean removed = workQueue.remove(task);
        tryTerminate(); // In case SHUTDOWN and now empty
        return removed;
    }

    public void purge() {
        final BlockingQueue<Runnable> q = workQueue;
        try {
            Iterator<Runnable> it = q.iterator();
            while (it.hasNext()) {
                Runnable r = it.next();
                if (r instanceof Future<?> && ((Future<?>)r).isCancelled())
                    it.remove();
            }
        } catch (ConcurrentModificationException fallThrough) {
            for (Object r : q.toArray())
                if (r instanceof Future<?> && ((Future<?>)r).isCancelled())
                    q.remove(r);
        }

        tryTerminate();
    }

    /**
     * 获取线程池的线程数量。
     * 如果线程池不销毁的话，线程池里的线程不会自动销毁，所以这个线程数量值大小只增不减。
     */
    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return runStateAtLeast(ctl.get(), TIDYING) ? 0
                : workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 获取活动的线程数
     */
    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (Worker w : workers)
                if (w.isLocked())
                    ++n;
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return largestPoolSize;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 获取线程池需要执行的任务数量
     * 可以用作线程池的监控
     */
    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
                if (w.isLocked())
                    ++n;
            }
            return n + workQueue.size();
        } finally {
            mainLock.unlock();
        }
    }

    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = completedTaskCount;
            for (Worker w : workers)
                n += w.completedTasks;
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public String toString() {
        long ncompleted;
        int nworkers, nactive;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            ncompleted = completedTaskCount;
            nactive = 0;
            nworkers = workers.size();
            for (Worker w : workers) {
                ncompleted += w.completedTasks;
                if (w.isLocked())
                    ++nactive;
            }
        } finally {
            mainLock.unlock();
        }
        int c = ctl.get();
        String rs = (runStateLessThan(c, SHUTDOWN) ? "Running" :
                     (runStateAtLeast(c, TERMINATED) ? "Terminated" :
                      "Shutting down"));
        return super.toString() +
            "[" + rs +
            ", pool size = " + nworkers +
            ", active threads = " + nactive +
            ", queued tasks = " + workQueue.size() +
            ", completed tasks = " + ncompleted +
            "]";
    }

    protected void beforeExecute(Thread t, Runnable r) { }

    protected void afterExecute(Runnable r, Throwable t) { }

    protected void terminated() { }

    /**
     * 公开的静态内部类
     * 调用任务的 run() 方法绕过线程池直接执行
     */
    public static class CallerRunsPolicy implements RejectedExecutionHandler {

        public CallerRunsPolicy() { }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }

    /**
     * 公开的内部静态类（默认）
     * 丢弃任务并抛出 RejectedExecutionException 异常
     */
    public static class AbortPolicy implements RejectedExecutionHandler {

        public AbortPolicy() { }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + e.toString());
        }
    }

    /**
     * 公开的静态内部类
     * 丢弃任务，但是不抛出异常，不推荐
     */
    public static class DiscardPolicy implements RejectedExecutionHandler {

        public DiscardPolicy() { }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        }
    }

    /**
     * 公开的静态内部类
     * 抛弃队列中等待最久的任务，然后把当前任务加入队列中
     */
    public static class DiscardOldestPolicy implements RejectedExecutionHandler {

        public DiscardOldestPolicy() { }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }
}
