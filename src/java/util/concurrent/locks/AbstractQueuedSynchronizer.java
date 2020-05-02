package java.util.concurrent.locks;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import sun.misc.Unsafe;

/**
 * 内置自旋锁实现的同步队列（FIFO），封装入队和出队的操作，提供独占、共享、中断等特性的方法。
 * AQS 的功能可以分为两类：独占功能和共享功能
 * 它的所有子类中，要么实现并使用了它独占功能的 API，要么使用了共享锁的功能，而不会同时使用两套 API，即便是它最有名的子类 ReentrantReadWriteLock，也是通过两个内部类：读锁和写锁，分别实现的两套 API 来实现的
 *
 * AQS 并不关心「是什么锁」，对于 AQS 来说它只是实现了一系列的用于判断「资源」是否可以访问的 API，并且封装了在「访问资源」受限时将请求访问的线程的加入队列、挂起、唤醒等操作
 * AQS 只关心
 *  - 资源不可以访问时，怎么处理？
 *  - 资源是可以被同时访问，还是在同一时间只能被一个线程访问？
 *  - 如果有线程等不及资源了，怎么从 AQS 的队列中退出？
 * 等一系列围绕资源访问的问题，至于「资源是否可以被访问？」这个问题则交给 AQS 的子类去实现。
 *      当 AQS 的子类是实现独占功能时，例如 ReentrantLock，「资源是否可以被访问」被定义为只要 AQS 的 state 变量不为 0，并且持有锁的线程不是当前线程，则代表资源不能访问。
 *      当 AQS 的子类是实现共享功能时，例如 CountDownLatch，「资源是否可以被访问」被定义为只要 AQS 的 state 变量不为0，说明资源不能访问。
 */
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {

    private static final long serialVersionUID = 7373984972572414691L;

    protected AbstractQueuedSynchronizer() { }

    static final class Node {

        /**
         * 共享
         */
        static final Node SHARED = new Node();

        /**
         * 独占
         */
        static final Node EXCLUSIVE = null;

        /**
         * 节点取消，当前的线程被取消
         * 在同步队列中等待的线程等待超时或被中断，需要从同步队列中取消该 Node 的结点，
         *  其结点的 waitStatus 为 CANCELLED，即结束状态，进入该状态后的结点将不会再变化。
         */
        static final int CANCELLED =  1;

        /**
         * 节点等待触发，当前节点的后继节点包含的线程需要运行，也就是 unpark
         * 被标识为该等待唤醒状态的后继结点，当其前继结点的线程释放了同步锁或被取消，将会通知该后继结点的线程执行。
         * 说白了，就是处于唤醒状态，只要前继结点释放锁，就会通知标识为 SIGNAL 状态的后继结点的线程执行。
         */
        static final int SIGNAL    = -1;

        /**
         * 节点等待条件，表示当前节点在等待 condition，也就是在 condition 队列中
         * 与 Condition 相关，该标识的结点处于等待队列中，结点的线程等待在 Condition 上，当其他线程调用了 Condition的signal() 方法后，
         * CONDITION 状态的结点将从等待队列转移到同步队列中，等待获取同步锁。
         */
        static final int CONDITION = -2;

        /**
         * 节点状态需要向后传播，表示当前场景下后续的 acquireShared 能够得以执行
         * 与共享模式相关，在共享模式中，该状态标识结点的线程处于可运行状态。
         */
        static final int PROPAGATE = -3;

        /**
         * 描述节点的状态，上边四个 int 静态常量就是它的四种状态。
         * 此外这个 waitStatus 的值还可以为 0，表示当前节点在 sync 队列中，等待着获取锁，初始化状态
         *  入队的线程需要挂起时，需要满足它前一个节点的状态是 SIGNAL 的，也就是前一个节点是要处于节点等待触发的状态
         *
         *  AQS 在判断状态时，通过用 waitStatus>0 表示取消状态，而 waitStatus<0 表示有效状态。
         */
        volatile int waitStatus;

        /**
         * 当前节点的前一个节点
         * 前去节点，比如当前节点被取消，就需要前驱节点和后继节点来完成连接
         */
        volatile Node prev;

        /**
         * 当前节点的后一个节点，后继节点
         */
        volatile Node next;

        /**
         * 入队列时的当前线程
         */
        volatile Thread thread;

        /**
         * 存储 condition 队列中的后继节点
         */
        Node nextWaiter;

        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {
        }

        Node(Thread thread, Node mode) {
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) {
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

    /**
     * 队列的头节点
     */
    private transient volatile Node head;

    /**
     * 队列的尾节点
     */
    private transient volatile Node tail;

    /**
     * 资源共享变量
     * 如果线程获取资源失败,则进入同步 FIFO 队列中等待；
     * 如果线程成功获取资源就执行临界区代码；
     * 执行完释放资源时，会通知同步队列中的等待线程来获取资源后出队并执行。
     *
     * 在独占锁中用来表示有没有线程拿走了锁，或者说，锁还存不存在，比如在 ReentrantLock 中，就表示加锁的次数，释放锁的时候不断进行减 1 操作
     * 在共享锁中用来表示初始大小，比如说再 CountDownLatch 中，state 就表示计数器初始的大小
     */
    private volatile int state;

    protected final int getState() {
        return state;
    }

    protected final void setState(int newState) {
        state = newState;
    }

    protected final boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    static final long spinForTimeoutThreshold = 1000L;

    /**
     * 节点入队，将 node 加入队尾
     */
    private Node enq(final Node node) {
        // CAS「自旋」，直到成功加入队尾
        for (;;) {
            Node t = tail;
            // 如果尾节点为空，那么原子化的分配一个头结点，并将尾节点指向头结点，这一步是初始化
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                // 正常流程，放入队尾
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }

    /**
     * 创建一个 waiter（当前线程）后放到队列中
     *  1.使用当前线程构造 Node
     *  2.先行尝试在队尾添加，如果尾节点已经有了，
     *      -- 分配引用 T 指向尾节点
     *      -- 将当前节点的前驱节点更新为尾节点
     *      -- 如果尾节点是 T，那么将当前节点设置为尾节点（tail = current），原子更新
     *      -- T 的后继节点指向当前节点（T.next = current）
     *  3.如果队尾添加失败或者是第一个入队的节点
     *
     * 将该线程加入等待队列的尾部，并标记为独占模式；
     *
     * @param mode 表示节点类型的字段，仅仅表示这个节点是独占的，还是共享
     */
    private Node addWaiter(Node mode) {
        // 用当前线程构造一个 Node 对象
        Node node = new Node(Thread.currentThread(), mode);
        // 快速尝试在尾部添加
        Node pred = tail;
        if (pred != null) {
            // 创建好节点后，将节点加入到队列尾部，此处，在队列不为空的时候，先尝试通过 cas 方式修改尾节点为最新的节点
            // 如果修改失败，意味着有并发，这个时候才会进入 enq 中死循环，「自旋」方式修改。
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        // 如果是第 1 个节点，也就是 sync 队列没有初始化，那么会进入到 enq这 个方法，进入的线程可能有多个，
        // 或者说在 addWaiter 中没有成功入队的线程都将进入 enq 这个方法。
        // 可以看到 enq 的逻辑是确保进入的 Node 都会有机会顺序的添加到 sync 队列中
        // 如果修改失败，意味着有并发，这个时候才会进入 enq 中死循环，「自旋」方式修改。
        // 入队
        enq(node);
        return node;
    }

    private void setHead(Node node) {
        head = node;
        node.thread = null;
        node.prev = null;
    }

    /**
     * 释放锁成功后，找到 AQS 的头节点并唤醒它，唤醒等待队列中下一个线程
     * 用 unpark() 唤醒等待队列中最前边的那个未放弃线程
     * 寻找的顺序是从队列尾部开始往前去找的最前面的一个 waitStatus 小于 0 的节点
     */
    private void unparkSuccessor(Node node) {
        // 将状态设置为同步状态，node 一般为当前线程所在的节点
        int ws = node.waitStatus;
        // 获取当前节点的后继节点，如果满足状态，那么进行唤醒操作
        if (ws < 0) {
            // 置零当前线程所在的节点状态，允许失败
            compareAndSetWaitStatus(node, ws, 0);
        }
        // 如果没有满足状态，从尾部开始找寻符合要求的节点并将其唤醒
        // 找到下一个需要唤醒的节点 s
        Node s = node.next;
        // 如果为空或已取消
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev) {
                // <= 0 的节点，都是还有效的节点
                if (t.waitStatus <= 0) {
                    s = t;
                }
            }
        }
        if (s != null) {
            // 唤醒
            LockSupport.unpark(s.thread);
        }
    }

    /**
     * 唤醒后继节点
     */
    private void doReleaseShared() {
        for (;;) {
            Node h = head;
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                if (ws == Node.SIGNAL) {
                    // 如果当前节点是 SIGNAL，即节点等待触发，意味着它正在等待一个信号
                    // 或者说，它在等待被唤醒，因此做两件事，1 是重置 waitStatus 标志位，2 是重置成功后, 唤醒下一个节点。
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0)) {
                        continue;
                    }
                    // 唤醒后继
                    unparkSuccessor(h);
                }
                // 如果本身头结点的 waitStatus 是处于重置状态（waitStatus==0）的，将其设置为「传播 PROPAGATE」状态。
                // 意味着需要将状态向后一个节点传播。
                else if (ws == 0 && !compareAndSetWaitStatus(h, 0, Node.PROPAGATE)) {
                    continue;
                }
            }
            // head 发生变化
            if (h == head) {
                break;
            }
        }
    }

    /**
     * 设置当前线程在队列中为头结点并且节点状态为 propagate，即节点状态需要向后传播
     * 使用 CAS 更换了头节点，然后将当前节点的下一个节点取出来，如果同样是共享类型的，再做一个释放共享锁操作。
     */
    private void setHeadAndPropagate(Node node, int propagate) {
        Node h = head; // Record old head for check below
        // head 指向自己
        setHead(node);
        // 如果还有剩余量，继续唤醒下一个邻居线程
        if (propagate > 0 || h == null || h.waitStatus < 0 || (h = head) == null || h.waitStatus < 0) {
            Node s = node.next;
            if (s == null || s.isShared()) {
                doReleaseShared();
            }
        }
    }

    private void cancelAcquire(Node node) {
        if (node == null)
            return;

        node.thread = null;

        Node pred = node.prev;
        while (pred.waitStatus > 0)
            node.prev = pred = pred.prev;

        Node predNext = pred.next;

        node.waitStatus = Node.CANCELLED;

        if (node == tail && compareAndSetTail(node, pred)) {
            compareAndSetNext(pred, predNext, null);
        } else {
            int ws;
            if (pred != head &&
                ((ws = pred.waitStatus) == Node.SIGNAL ||
                 (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
                pred.thread != null) {
                Node next = node.next;
                if (next != null && next.waitStatus <= 0)
                    compareAndSetNext(pred, predNext, next);
            } else {
                unparkSuccessor(node);
            }

            node.next = node; // help GC
        }
    }

    /**
     * 用于检查状态，看看自己是否真的可以去休息了（
     * 即进入 waiting 状态
     */
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        // 拿到前驱的状态
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL)
             // 如果已经告诉前驱拿完号后通知自己一下，就可以安心等待了
            return true;
        if (ws > 0) {
            /*
             * 如果前驱放弃了，那就一直往前找，直到找到最近一个正常等待的状态，并排在它的后边。
             * 注意：那些放弃的结点，由于被自己「加塞」到它们前边，它们相当于形成一个无引用链，稍后就会被 GC 回收
             */
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            /*
             * 如果前驱正常，那就把前驱的状态设置成 SIGNAL，告诉它拿完号后通知自己一下。
             * 有可能失败，前驱说不定刚刚释放完
             */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

    /**
     * Convenience method to interrupt current thread.
     */
    static void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    /**
     * 让线程去休息，真正进入等待状态。
     */
    private final boolean parkAndCheckInterrupt() {
        // 调用 park() 使线程进入 waiting 状态
        LockSupport.park(this);
        // 如果被唤醒，查看自己是不是被中断的。
        // 有两种途径可以唤醒该线程 被 unpark() 被 interrupt()
        // Thread.interrupted() 会清除当前线程的中断标记位。
        return Thread.interrupted();
    }

    /*
     * Various flavors of acquire, varying in exclusive/shared and
     * control modes.  Each is mostly the same, but annoyingly
     * different.  Only a little bit of factoring is possible due to
     * interactions of exception mechanics (including ensuring that we
     * cancel if tryAcquire throws exception) and other control, at
     * least not without hurting performance too much.
     */

    /**
     * 将线程的节点接入到队列中后，将当前线程挂起，在等待队列中排队拿号（中间没其它事干可以休息），直到拿到号后再返回
     * 进入 sync 队列之后，接下来就是要进行锁的获取，或者说是访问控制了，只有一个线程能够在同一时刻继续的运行，而其他的进入等待状态。
     * 而每个线程都是一个独立的个体，它们自省的观察，当条件满足的时候（自己的前驱是头结点并且原子性的获取了状态），那么这个线程能够继续运行。
     *  1.获取当前节点的前驱节点
     *  2.当前驱节点是头结点并且能够获取状态，代表当前节点占有锁
     *  3.否则进入等待状态
     *
     * 使线程在等待队列中获取资源，一直获取到资源后才返回。如果在整个等待过程中被中断过，则返回 true，否则返回 false。
     */
    final boolean acquireQueued(final Node node, int arg) {
        // 标记是否成功拿到资源
        boolean failed = true;
        try {
            // 标记等待过程中是否被中断过
            boolean interrupted = false;
            // CAS 自旋
            for (;;) {
                // 拿到前驱
                final Node p = node.predecessor();
                // 如果当前的节点是 head 说明他是队列中第一个「有效的」节点，因此尝试获取，tryAcquire 是交给子类去扩展的。
                if (p == head && tryAcquire(arg)) {
                    // 第一次节点插入时，第一个节点是一个空节点，代表有一个线程已经获取锁，事实上，队列的第一个节点就是代表持有锁的节点
                    // 队列默认的头节点，如果每次有线程竞争失败，进入队列后其实都是插入到队列的尾节点（tail）后面
                    // 成功后，默认节点移除，后续节点变成头节点
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    // 返回等待过程中是否被中断过
                    return interrupted;
                }
                // 如果不是第一次插入，就检查前一个节点的状态位，以此判断当前获取锁失败的线程是否需要挂起
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
                    // 如果需要挂起，借助 JUC 包下的 LockSupport 类静态方法 park 挂起当前线程，直到被唤醒
                    // 如果等待过程中被中断过，哪怕只有那么一次，就将 interrupted 标记为 true
                    interrupted = true;
                }
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in exclusive interruptible mode.
     * @param arg the acquire argument
     */
    private void doAcquireInterruptibly(int arg) throws InterruptedException {
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
                // 检测中断标志位
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
                    throw new InterruptedException();
                }
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * 提供具备有超时功能的获取状态的调用，如果在指定的 nanosTimeout 内没有获取到状态，那么返回 false，反之返回 true。
     * 可以将该方法看做 acquireInterruptibly 的升级版，也就是在判断是否被中断的基础上增加了超时控制。
     *  1.加入 sync 队列
     *  2.条件满足直接返回
     *  3.获取状态失败休眠一段时间
     *  4.计算再次休眠的时间
     *  5.休眠时间的判定
     */
    private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (nanosTimeout <= 0L) {
            return false;
        }
        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return true;
                }
                nanosTimeout = deadline - System.nanoTime();
                if (nanosTimeout <= 0L) {
                    return false;
                }
                // 计算时间，单签时间减去睡眠之前的时间得到睡眠的时间。然后被原有超时时间减去，得到了还应该睡眠的时间
                if (shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold) {
                    LockSupport.parkNanos(this, nanosTimeout);
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * 将当前线程加入等待队列尾部休息，直到其他线程释放资源唤醒自己，自己成功拿到相应量的资源后才返回
     */
    private void doAcquireShared(int arg) {
        // 加入队列尾部
        final Node node = addWaiter(Node.SHARED);
        // 是否成功的标志
        boolean failed = true;
        try {
            // 等待过程中是否被中断过的标志
            boolean interrupted = false;
            // CAS 自旋
            for (;;) {
                // 前驱
                final Node p = node.predecessor();
                // 如果到 head 的下一个，因为 head 是拿到资源的线程，此时 node 被唤醒，很可能是 head 用完资源来唤醒自己的
                if (p == head) {
                    // 尝试获取资源
                    int r = tryAcquireShared(arg);
                    // 成功
                    if (r >= 0) {
                        // 将 head 指向自己，还有剩余资源可以再唤醒之后的线程
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        // 如果等待过程中被打断过，此时将中断补上。
                        if (interrupted) {
                            selfInterrupt();
                        }
                        failed = false;
                        return;
                    }
                }
                // 判断状态，寻找安全点，进入 waiting 状态，等着被 unpark() 或 interrupt()
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * 将获取共享锁失败的线程放入到队列
     */
    private void doAcquireSharedInterruptibly(int arg) throws InterruptedException {

        // 将当前线程包装为类型为 Node.SHARED 的节点，标示这是一个共享节点。
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                // 如果新建节点的前一个节点就是 Head，说明当前节点是 AQS 队列中等待获取锁的第一个节点，按照 FIFO 的原则，可以直接尝试获取锁。
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        // 获取成功，需要将当前节点设置为 AQS 队列中的第一个节点，这是 AQS 的规则，队列的头节点表示正在获取锁的节点
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                // 检查下是否需要将当前节点挂起
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
                    throw new InterruptedException();
                }
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared timed mode.
     *
     * @param arg the acquire argument
     * @param nanosTimeout max wait time
     * @return {@code true} if acquired
     */
    private boolean doAcquireSharedNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (nanosTimeout <= 0L)
            return false;
        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return true;
                    }
                }
                nanosTimeout = deadline - System.nanoTime();
                if (nanosTimeout <= 0L)
                    return false;
                if (shouldParkAfterFailedAcquire(p, node) &&
                    nanosTimeout > spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if (Thread.interrupted())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    // Main exported methods

    /**
     * 独占，排它获取这个状态
     * 需要查询当前状态是否允许获取，然后在进行获取状态（使用 CAS）
     *
     * 自定义同步器需要实现的方法，也就是自定义同步器非阻塞原子化的获取状态，如果是锁，该方法一般用于 Lock 的 tryLock 实现中，
     * 这个特性是 synchronized 无法提供的。
     *
     * 尝试直接去获取资源，如果成功则直接返回返回 true，失败则返回 false
     */
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * 释放状态，尝试去释放指定量的资源
     * 保证原子化的将状态设置回去，需要使用 compareAndSet 来保证。
     * 如果释放状态成功过之后，将会进入后继节点的唤醒过程。
     *
     * 成功返回 true，失败则返回 false
     */
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * 共享的模式获取状态
     * 在调用 tryAcquireShared 之前总能成功地调用 tryReleaseShared
     *
     * 负数表示失败；0表示成功，但没有剩余可用资源；正数表示成功，且有剩余资源。
     */
    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * 共享的模式下释放状态
     *
     * 如果释放后允许唤醒后续等待结点返回 true，否则返回 false。
     */
    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * 在独占，排它模式下，状态是否被占用
     * 该线程是否正在独占资源。只有用到 condition 才需要去实现它
     */
    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }

    /**
     * 尝试获取锁，获取不到则创建一个 waiter（当前线程）后放到队列中
     *  1.尝试获取（调用 tryAcquire 更改状态，需要保证原子性）
     *  2.如果获取不到，将当前线程构造成节点 Node 并加入 sync 队列
     *  3.再次尝试获取，如果没有或获取到那么将当前线程从线程调度器上摘下，进入等待状态
     *
     * acquire 负责
     *  -- 状态的维护
     *  -- 状态的获取
     *  -- sync 队列的维护
     */
    public final void acquire(int arg) {
        if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) {
            // 如果线程在等待过程中被中断过，它是不响应的。
            // 只是获取资源后才再进行自我中断 selfInterrupt()，将中断补上。
            selfInterrupt();
        }
    }

    /**
     * 提供获取状态的能力
     * 无法获取状态的情况下会进入 sync 队列进行排队，类似acquire，
     * 和 acquire 不同的地方在于它能够在外界对当前线程进行中断的时候提前结束获取状态的操作
     *
     * 比如在获取一个通过网络交互实现的锁时，这个锁资源突然进行了销毁，那么使用 acquireInterruptibly 的获取方式就能够让该时刻尝试获取锁的线程提前返回。
     *
     *  1.检测当前线程是否被中断
     *  2.尝试获取状态
     *  3.构造节点并加入 sync 队列
     *  4.中断检测
     */
    public final void acquireInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        if (!tryAcquire(arg)) {
            doAcquireInterruptibly(arg);
        }
    }

    /**
     * Attempts to acquire in exclusive mode, aborting if interrupted,
     * and failing if the given timeout elapses.  Implemented by first
     * checking interrupt status, then invoking at least once {@link
     * #tryAcquire}, returning on success.  Otherwise, the thread is
     * queued, possibly repeatedly blocking and unblocking, invoking
     * {@link #tryAcquire} until success or the thread is interrupted
     * or the timeout elapses.  This method can be used to implement
     * method {@link Lock#tryLock(long, TimeUnit)}.
     *
     * @param arg the acquire argument.  This value is conveyed to
     *        {@link #tryAcquire} but is otherwise uninterpreted and
     *        can represent anything you like.
     * @param nanosTimeout the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException if the current thread is interrupted
     */
    public final boolean tryAcquireNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquire(arg) ||
            doAcquireNanos(arg, nanosTimeout);
    }

    /**
     * 资源释放，锁释放
     *  1.尝试释放状态
     *  2.唤醒当前节点的后继节点所包含的线程
     *
     * 独占模式下线程释放共享资源的顶层入口。它会释放指定量的资源，如果彻底释放了（即 state=0）,它会唤醒等待队列里的其他线程来获取资源。
     * 根据 tryRelease() 的返回值来判断该线程是否已经完成释放掉资源了
     */
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            // 找到头结点
            Node h = head;
            if (h != null && h.waitStatus != 0) {
                // 唤醒等待队列里的下一个线程
                unparkSuccessor(h);
            }
            return true;
        }
        return false;
    }

    /**
     * 该方法能够以共享模式获取状态，共享模式下线程获取共享资源的顶层入口。
     * 它会获取指定量的资源，获取成功则直接返回，获取失败则进入等待队列，直到获取到资源为止，整个过程忽略中断。
     *  1.尝试获取共享状态 调用 tryAcquireShared 来获取共享状态，该方法是非阻塞的，如果获取成功则立刻返回，也就表示获取共享锁成功。
     *  2.获取失败进入 sync 队列
     *  3.循环内判断退出队列条件
     *  4.获取共享状态成功
     *      和独占锁之间的主要区别在于获取共享状态成功之后的行为，而如果共享状态获取成功之后会判断后继节点是否是共享模式，如果是共享模式，那么就直接对其进行唤醒操作，也就是同时激发多个线程并发的运行。
     *  5.获取共享状态失败
     *
     */
    public final void acquireShared(int arg) {
        if (tryAcquireShared(arg) < 0) {
            doAcquireShared(arg);
        }
    }

    /**
     * 响应线程的打断
     */
    public final void acquireSharedInterruptibly(int arg) throws InterruptedException {
        // 检查线程是否被打断
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        // 尝试获取锁，如果小于 0，表示获取失败，也就是 计数器 state 不为 0
        if (tryAcquireShared(arg) < 0) {
            // 将当前的线程放入到队列中区
            doAcquireSharedInterruptibly(arg);
        }
    }

    /**
     * Attempts to acquire in shared mode, aborting if interrupted, and
     * failing if the given timeout elapses.  Implemented by first
     * checking interrupt status, then invoking at least once {@link
     * #tryAcquireShared}, returning on success.  Otherwise, the
     * thread is queued, possibly repeatedly blocking and unblocking,
     * invoking {@link #tryAcquireShared} until success or the thread
     * is interrupted or the timeout elapses.
     *
     * @param arg the acquire argument.  This value is conveyed to
     *        {@link #tryAcquireShared} but is otherwise uninterpreted
     *        and can represent anything you like.
     * @param nanosTimeout the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException if the current thread is interrupted
     */
    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquireShared(arg) >= 0 ||
            doAcquireSharedNanos(arg, nanosTimeout);
    }

    /**
     * 释放共享状态，每次获取共享状态 acquireShared 都会操作状态，同样在共享锁释放的时候，也需要将状态释放。
     * 调用同步器的 tryReleaseShared 方法来释放状态，并同时在 doReleaseShared 方法中唤醒其后继节点。
     */
    public final boolean releaseShared(int arg) {
        // 尝试释放资源
        if (tryReleaseShared(arg)) {
            // 唤醒后继节点
            doReleaseShared();
            return true;
        }
        return false;
    }

    // Queue inspection methods

    /**
     * Queries whether any threads are waiting to acquire. Note that
     * because cancellations due to interrupts and timeouts may occur
     * at any time, a {@code true} return does not guarantee that any
     * other thread will ever acquire.
     *
     * <p>In this implementation, this operation returns in
     * constant time.
     *
     * @return {@code true} if there may be other threads waiting to acquire
     */
    public final boolean hasQueuedThreads() {
        return head != tail;
    }

    /**
     * Queries whether any threads have ever contended to acquire this
     * synchronizer; that is if an acquire method has ever blocked.
     *
     * <p>In this implementation, this operation returns in
     * constant time.
     *
     * @return {@code true} if there has ever been contention
     */
    public final boolean hasContended() {
        return head != null;
    }

    /**
     * Returns the first (longest-waiting) thread in the queue, or
     * {@code null} if no threads are currently queued.
     *
     * <p>In this implementation, this operation normally returns in
     * constant time, but may iterate upon contention if other threads are
     * concurrently modifying the queue.
     *
     * @return the first (longest-waiting) thread in the queue, or
     *         {@code null} if no threads are currently queued
     */
    public final Thread getFirstQueuedThread() {
        // handle only fast path, else relay
        return (head == tail) ? null : fullGetFirstQueuedThread();
    }

    /**
     * Version of getFirstQueuedThread called when fastpath fails
     */
    private Thread fullGetFirstQueuedThread() {
        /*
         * The first node is normally head.next. Try to get its
         * thread field, ensuring consistent reads: If thread
         * field is nulled out or s.prev is no longer head, then
         * some other thread(s) concurrently performed setHead in
         * between some of our reads. We try this twice before
         * resorting to traversal.
         */
        Node h, s;
        Thread st;
        if (((h = head) != null && (s = h.next) != null &&
             s.prev == head && (st = s.thread) != null) ||
            ((h = head) != null && (s = h.next) != null &&
             s.prev == head && (st = s.thread) != null))
            return st;

        /*
         * Head's next field might not have been set yet, or may have
         * been unset after setHead. So we must check to see if tail
         * is actually first node. If not, we continue on, safely
         * traversing from tail back to head to find first,
         * guaranteeing termination.
         */

        Node t = tail;
        Thread firstThread = null;
        while (t != null && t != head) {
            Thread tt = t.thread;
            if (tt != null)
                firstThread = tt;
            t = t.prev;
        }
        return firstThread;
    }

    /**
     * Returns true if the given thread is currently queued.
     *
     * <p>This implementation traverses the queue to determine
     * presence of the given thread.
     *
     * @param thread the thread
     * @return {@code true} if the given thread is on the queue
     * @throws NullPointerException if the thread is null
     */
    public final boolean isQueued(Thread thread) {
        if (thread == null)
            throw new NullPointerException();
        for (Node p = tail; p != null; p = p.prev)
            if (p.thread == thread)
                return true;
        return false;
    }

    /**
     * Returns {@code true} if the apparent first queued thread, if one
     * exists, is waiting in exclusive mode.  If this method returns
     * {@code true}, and the current thread is attempting to acquire in
     * shared mode (that is, this method is invoked from {@link
     * #tryAcquireShared}) then it is guaranteed that the current thread
     * is not the first queued thread.  Used only as a heuristic in
     * ReentrantReadWriteLock.
     */
    final boolean apparentlyFirstQueuedIsExclusive() {
        Node h, s;
        return (h = head) != null &&
            (s = h.next)  != null &&
            !s.isShared()         &&
            s.thread != null;
    }

    /**
     * Queries whether any threads have been waiting to acquire longer
     * than the current thread.
     *
     * <p>An invocation of this method is equivalent to (but may be
     * more efficient than):
     *  <pre> {@code
     * getFirstQueuedThread() != Thread.currentThread() &&
     * hasQueuedThreads()}</pre>
     *
     * <p>Note that because cancellations due to interrupts and
     * timeouts may occur at any time, a {@code true} return does not
     * guarantee that some other thread will acquire before the current
     * thread.  Likewise, it is possible for another thread to win a
     * race to enqueue after this method has returned {@code false},
     * due to the queue being empty.
     *
     * <p>This method is designed to be used by a fair synchronizer to
     * avoid <a href="AbstractQueuedSynchronizer#barging">barging</a>.
     * Such a synchronizer's {@link #tryAcquire} method should return
     * {@code false}, and its {@link #tryAcquireShared} method should
     * return a negative value, if this method returns {@code true}
     * (unless this is a reentrant acquire).  For example, the {@code
     * tryAcquire} method for a fair, reentrant, exclusive mode
     * synchronizer might look like this:
     *
     *  <pre> {@code
     * protected boolean tryAcquire(int arg) {
     *   if (isHeldExclusively()) {
     *     // A reentrant acquire; increment hold count
     *     return true;
     *   } else if (hasQueuedPredecessors()) {
     *     return false;
     *   } else {
     *     // try to acquire normally
     *   }
     * }}</pre>
     *
     * @return {@code true} if there is a queued thread preceding the
     *         current thread, and {@code false} if the current thread
     *         is at the head of the queue or the queue is empty
     * @since 1.7
     */
    public final boolean hasQueuedPredecessors() {
        // The correctness of this depends on head being initialized
        // before tail and on head.next being accurate if the current
        // thread is first in queue.
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t &&
            ((s = h.next) == null || s.thread != Thread.currentThread());
    }


    // Instrumentation and monitoring methods

    /**
     * Returns an estimate of the number of threads waiting to
     * acquire.  The value is only an estimate because the number of
     * threads may change dynamically while this method traverses
     * internal data structures.  This method is designed for use in
     * monitoring system state, not for synchronization
     * control.
     *
     * @return the estimated number of threads waiting to acquire
     */
    public final int getQueueLength() {
        int n = 0;
        for (Node p = tail; p != null; p = p.prev) {
            if (p.thread != null)
                ++n;
        }
        return n;
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire.  Because the actual set of threads may change
     * dynamically while constructing this result, the returned
     * collection is only a best-effort estimate.  The elements of the
     * returned collection are in no particular order.  This method is
     * designed to facilitate construction of subclasses that provide
     * more extensive monitoring facilities.
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            Thread t = p.thread;
            if (t != null)
                list.add(t);
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire in exclusive mode. This has the same properties
     * as {@link #getQueuedThreads} except that it only returns
     * those threads waiting due to an exclusive acquire.
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getExclusiveQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (!p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire in shared mode. This has the same properties
     * as {@link #getQueuedThreads} except that it only returns
     * those threads waiting due to a shared acquire.
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getSharedQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    /**
     * Returns a string identifying this synchronizer, as well as its state.
     * The state, in brackets, includes the String {@code "State ="}
     * followed by the current value of {@link #getState}, and either
     * {@code "nonempty"} or {@code "empty"} depending on whether the
     * queue is empty.
     *
     * @return a string identifying this synchronizer, as well as its state
     */
    public String toString() {
        int s = getState();
        String q  = hasQueuedThreads() ? "non" : "";
        return super.toString() +
            "[State = " + s + ", " + q + "empty queue]";
    }


    // Internal support methods for Conditions

    /**
     * Returns true if a node, always one that was initially placed on
     * a condition queue, is now waiting to reacquire on sync queue.
     * @param node the node
     * @return true if is reacquiring
     */
    final boolean isOnSyncQueue(Node node) {
        if (node.waitStatus == Node.CONDITION || node.prev == null)
            return false;
        if (node.next != null) // If has successor, it must be on queue
            return true;
        /*
         * node.prev can be non-null, but not yet on queue because
         * the CAS to place it on queue can fail. So we have to
         * traverse from tail to make sure it actually made it.  It
         * will always be near the tail in calls to this method, and
         * unless the CAS failed (which is unlikely), it will be
         * there, so we hardly ever traverse much.
         */
        return findNodeFromTail(node);
    }

    /**
     * Returns true if node is on sync queue by searching backwards from tail.
     * Called only when needed by isOnSyncQueue.
     * @return true if present
     */
    private boolean findNodeFromTail(Node node) {
        Node t = tail;
        for (;;) {
            if (t == node)
                return true;
            if (t == null)
                return false;
            t = t.prev;
        }
    }

    /**
     *
     */
    final boolean transferForSignal(Node node) {
        // 状态为 CONDITION 的，然后把状态变为 0
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            return false;
        }

        // 把条件队列的上面状态为 0 的节点放入 AQS 阻塞队列
        Node p = enq(node);
        int ws = p.waitStatus;
        // 如果该节点的状态为 cancel 或者修改 waitStatus 失败，则直接唤醒
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL)) {
            // 调用 unpark 激活挂起的线程
            LockSupport.unpark(node.thread);
        }
        return true;
    }

    /**
     * Transfers node, if necessary, to sync queue after a cancelled wait.
     * Returns true if thread was cancelled before being signalled.
     *
     * @param node the node
     * @return true if cancelled before the node was signalled
     */
    final boolean transferAfterCancelledWait(Node node) {
        if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            enq(node);
            return true;
        }
        /*
         * If we lost out to a signal(), then we can't proceed
         * until it finishes its enq().  Cancelling during an
         * incomplete transfer is both rare and transient, so just
         * spin.
         */
        while (!isOnSyncQueue(node))
            Thread.yield();
        return false;
    }

    /**
     * Invokes release with current state value; returns saved state.
     * Cancels node and throws exception on failure.
     * @param node the condition node for this wait
     * @return previous sync state
     */
    final int fullyRelease(Node node) {
        boolean failed = true;
        try {
            int savedState = getState();
            if (release(savedState)) {
                failed = false;
                return savedState;
            } else {
                throw new IllegalMonitorStateException();
            }
        } finally {
            if (failed)
                node.waitStatus = Node.CANCELLED;
        }
    }

    // Instrumentation methods for conditions

    /**
     * Queries whether the given ConditionObject
     * uses this synchronizer as its lock.
     *
     * @param condition the condition
     * @return {@code true} if owned
     * @throws NullPointerException if the condition is null
     */
    public final boolean owns(ConditionObject condition) {
        return condition.isOwnedBy(this);
    }

    /**
     * Queries whether any threads are waiting on the given condition
     * associated with this synchronizer. Note that because timeouts
     * and interrupts may occur at any time, a {@code true} return
     * does not guarantee that a future {@code signal} will awaken
     * any threads.  This method is designed primarily for use in
     * monitoring of the system state.
     *
     * @param condition the condition
     * @return {@code true} if there are any waiting threads
     * @throws IllegalMonitorStateException if exclusive synchronization
     *         is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this synchronizer
     * @throws NullPointerException if the condition is null
     */
    public final boolean hasWaiters(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.hasWaiters();
    }

    /**
     * Returns an estimate of the number of threads waiting on the
     * given condition associated with this synchronizer. Note that
     * because timeouts and interrupts may occur at any time, the
     * estimate serves only as an upper bound on the actual number of
     * waiters.  This method is designed for use in monitoring of the
     * system state, not for synchronization control.
     *
     * @param condition the condition
     * @return the estimated number of waiting threads
     * @throws IllegalMonitorStateException if exclusive synchronization
     *         is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this synchronizer
     * @throws NullPointerException if the condition is null
     */
    public final int getWaitQueueLength(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitQueueLength();
    }

    /**
     * Returns a collection containing those threads that may be
     * waiting on the given condition associated with this
     * synchronizer.  Because the actual set of threads may change
     * dynamically while constructing this result, the returned
     * collection is only a best-effort estimate. The elements of the
     * returned collection are in no particular order.
     *
     * @param condition the condition
     * @return the collection of threads
     * @throws IllegalMonitorStateException if exclusive synchronization
     *         is not held
     * @throws IllegalArgumentException if the given condition is
     *         not associated with this synchronizer
     * @throws NullPointerException if the condition is null
     */
    public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitingThreads();
    }

    /**
     * Condition implementation for a {@link
     * AbstractQueuedSynchronizer} serving as the basis of a {@link
     * Lock} implementation.
     *
     * <p>Method documentation for this class describes mechanics,
     * not behavioral specifications from the point of view of Lock
     * and Condition users. Exported versions of this class will in
     * general need to be accompanied by documentation describing
     * condition semantics that rely on those of the associated
     * {@code AbstractQueuedSynchronizer}.
     *
     * <p>This class is Serializable, but all fields are transient,
     * so deserialized conditions have no waiters.
     */
    public class ConditionObject implements Condition, java.io.Serializable {
        private static final long serialVersionUID = 1173984872572414699L;

        /**
         * Condition 内部维护了 AQS 等待队列的头结点和尾节点，
         * 该队列的作用是存放等待 signal 信号的线程，该线程被封装为 Node 节点后存放于此。
         *
         * 每个线程也仅仅会同时存在以上两个队列中的一个
         *
         */
        private transient Node firstWaiter;
        /** Last node of condition queue. */
        private transient Node lastWaiter;

        /**
         * Creates a new {@code ConditionObject} instance.
         */
        public ConditionObject() { }

        // Internal methods

        /**
         * Adds a new waiter to wait queue.
         * @return its new wait node
         */
        private Node addConditionWaiter() {
            Node t = lastWaiter;
            // If lastWaiter is cancelled, clean out.
            if (t != null && t.waitStatus != Node.CONDITION) {
                unlinkCancelledWaiters();
                t = lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), Node.CONDITION);
            if (t == null)
                firstWaiter = node;
            else
                t.nextWaiter = node;
            lastWaiter = node;
            return node;
        }

        /**
         * @param first 头结点
         */
        private void doSignal(Node first) {
            do {
                // 修改头结点，完成旧头结点的移出工作
                if ( (firstWaiter = first.nextWaiter) == null) {
                    lastWaiter = null;
                }
                first.nextWaiter = null;
            // 将老的头结点加入到 AQS 的等待队列中
            } while (!transferForSignal(first) && (first = firstWaiter) != null);
        }

        /**
         * Removes and transfers all nodes.
         * @param first (non-null) the first node on condition queue
         */
        private void doSignalAll(Node first) {
            lastWaiter = firstWaiter = null;
            do {
                Node next = first.nextWaiter;
                first.nextWaiter = null;
                transferForSignal(first);
                first = next;
            } while (first != null);
        }

        /**
         * Unlinks cancelled waiter nodes from condition queue.
         * Called only while holding lock. This is called when
         * cancellation occurred during condition wait, and upon
         * insertion of a new waiter when lastWaiter is seen to have
         * been cancelled. This method is needed to avoid garbage
         * retention in the absence of signals. So even though it may
         * require a full traversal, it comes into play only when
         * timeouts or cancellations occur in the absence of
         * signals. It traverses all nodes rather than stopping at a
         * particular target to unlink all pointers to garbage nodes
         * without requiring many re-traversals during cancellation
         * storms.
         */
        private void unlinkCancelledWaiters() {
            Node t = firstWaiter;
            Node trail = null;
            while (t != null) {
                Node next = t.nextWaiter;
                if (t.waitStatus != Node.CONDITION) {
                    t.nextWaiter = null;
                    if (trail == null)
                        firstWaiter = next;
                    else
                        trail.nextWaiter = next;
                    if (next == null)
                        lastWaiter = trail;
                }
                else
                    trail = t;
                t = next;
            }
        }

        /**
         *
         */
        public final void signal() {
            // 如果当前线程没有持有锁，抛异常
            if (!isHeldExclusively()) {
                throw new IllegalMonitorStateException();
            }
            // firstWaiter 为 condition 自己维护的一个链表的头结点，取出第一个节点后开始唤醒操作
            // 从条件队列找第一个状态为 CONDITION 的，然后把状态变为 0
            Node first = firstWaiter;
            if (first != null) {
                doSignal(first);
            }
        }

        /**
         * Moves all threads from the wait queue for this condition to
         * the wait queue for the owning lock.
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        public final void signalAll() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;
            if (first != null)
                doSignalAll(first);
        }

        /**
         * Implements uninterruptible condition wait.
         * <ol>
         * <li> Save lock state returned by {@link #getState}.
         * <li> Invoke {@link #release} with saved state as argument,
         *      throwing IllegalMonitorStateException if it fails.
         * <li> Block until signalled.
         * <li> Reacquire by invoking specialized version of
         *      {@link #acquire} with saved state as argument.
         * </ol>
         */
        public final void awaitUninterruptibly() {
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean interrupted = false;
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if (Thread.interrupted())
                    interrupted = true;
            }
            if (acquireQueued(node, savedState) || interrupted)
                selfInterrupt();
        }

        /*
         * For interruptible waits, we need to track whether to throw
         * InterruptedException, if interrupted while blocked on
         * condition, versus reinterrupt current thread, if
         * interrupted while blocked waiting to re-acquire.
         */

        /** Mode meaning to reinterrupt on exit from wait */
        private static final int REINTERRUPT =  1;
        /** Mode meaning to throw InterruptedException on exit from wait */
        private static final int THROW_IE    = -1;

        /**
         * Checks for interrupt, returning THROW_IE if interrupted
         * before signalled, REINTERRUPT if after signalled, or
         * 0 if not interrupted.
         */
        private int checkInterruptWhileWaiting(Node node) {
            return Thread.interrupted() ?
                (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) :
                0;
        }

        /**
         * Throws InterruptedException, reinterrupts current thread, or
         * does nothing, depending on mode.
         */
        private void reportInterruptAfterWait(int interruptMode)
            throws InterruptedException {
            if (interruptMode == THROW_IE)
                throw new InterruptedException();
            else if (interruptMode == REINTERRUPT)
                selfInterrupt();
        }

        /**
         *
         */
        public final void await() throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 将当前线程包装下添加到 Condition 自己维护的一个链表中。
            Node node = addConditionWaiter();
            // 释放当前线程占有的锁
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            // 释放完毕后，遍历 AQS 的队列，看当前节点是否在队列中
            // 不再，说明它还没有竞争锁的资格，所以继续将自己沉睡，直到它被加入到队列中
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            // 被唤醒后，重新开始正式竞争锁，同样，如果竞争不到还是会将自己沉睡，等待唤醒重新开始竞争
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null) // clean up if cancelled
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
        }

        /**
         * Implements timed condition wait.
         * <ol>
         * <li> If current thread is interrupted, throw InterruptedException.
         * <li> Save lock state returned by {@link #getState}.
         * <li> Invoke {@link #release} with saved state as argument,
         *      throwing IllegalMonitorStateException if it fails.
         * <li> Block until signalled, interrupted, or timed out.
         * <li> Reacquire by invoking specialized version of
         *      {@link #acquire} with saved state as argument.
         * <li> If interrupted while blocked in step 4, throw InterruptedException.
         * </ol>
         */
        public final long awaitNanos(long nanosTimeout) throws InterruptedException {
            // 如果中断标志被设置了，则抛异常
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // 添加当前线程节点到条件队列
            Node node = addConditionWaiter();
            // 当前线程释放独占锁
            int savedState = fullyRelease(node);
            final long deadline = System.nanoTime() + nanosTimeout;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (nanosTimeout <= 0L) {
                    transferAfterCancelledWait(node);
                    break;
                }
                if (nanosTimeout >= spinForTimeoutThreshold) {
                    // 挂起当前线程直到超时
                    LockSupport.parkNanos(this, nanosTimeout);
                }
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) {
                    break;
                }
                nanosTimeout = deadline - System.nanoTime();
            }
            // unpark 后，当前线程重新获取锁，有可能获取不到被放到 AQS 的队列
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE) {
                interruptMode = REINTERRUPT;
            }
            if (node.nextWaiter != null) {
                unlinkCancelledWaiters();
            }
            if (interruptMode != 0) {
                reportInterruptAfterWait(interruptMode);
            }
            return deadline - System.nanoTime();
        }

        /**
         * Implements absolute timed condition wait.
         * <ol>
         * <li> If current thread is interrupted, throw InterruptedException.
         * <li> Save lock state returned by {@link #getState}.
         * <li> Invoke {@link #release} with saved state as argument,
         *      throwing IllegalMonitorStateException if it fails.
         * <li> Block until signalled, interrupted, or timed out.
         * <li> Reacquire by invoking specialized version of
         *      {@link #acquire} with saved state as argument.
         * <li> If interrupted while blocked in step 4, throw InterruptedException.
         * <li> If timed out while blocked in step 4, return false, else true.
         * </ol>
         */
        public final boolean awaitUntil(Date deadline)
                throws InterruptedException {
            long abstime = deadline.getTime();
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean timedout = false;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (System.currentTimeMillis() > abstime) {
                    timedout = transferAfterCancelledWait(node);
                    break;
                }
                LockSupport.parkUntil(this, abstime);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return !timedout;
        }

        /**
         * Implements timed condition wait.
         * <ol>
         * <li> If current thread is interrupted, throw InterruptedException.
         * <li> Save lock state returned by {@link #getState}.
         * <li> Invoke {@link #release} with saved state as argument,
         *      throwing IllegalMonitorStateException if it fails.
         * <li> Block until signalled, interrupted, or timed out.
         * <li> Reacquire by invoking specialized version of
         *      {@link #acquire} with saved state as argument.
         * <li> If interrupted while blocked in step 4, throw InterruptedException.
         * <li> If timed out while blocked in step 4, return false, else true.
         * </ol>
         */
        public final boolean await(long time, TimeUnit unit)
                throws InterruptedException {
            long nanosTimeout = unit.toNanos(time);
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            final long deadline = System.nanoTime() + nanosTimeout;
            boolean timedout = false;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (nanosTimeout <= 0L) {
                    timedout = transferAfterCancelledWait(node);
                    break;
                }
                if (nanosTimeout >= spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
                nanosTimeout = deadline - System.nanoTime();
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return !timedout;
        }

        //  support for instrumentation

        /**
         * Returns true if this condition was created by the given
         * synchronization object.
         *
         * @return {@code true} if owned
         */
        final boolean isOwnedBy(AbstractQueuedSynchronizer sync) {
            return sync == AbstractQueuedSynchronizer.this;
        }

        /**
         * Queries whether any threads are waiting on this condition.
         * Implements {@link AbstractQueuedSynchronizer#hasWaiters(ConditionObject)}.
         *
         * @return {@code true} if there are any waiting threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        protected final boolean hasWaiters() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION)
                    return true;
            }
            return false;
        }

        /**
         * Returns an estimate of the number of threads waiting on
         * this condition.
         * Implements {@link AbstractQueuedSynchronizer#getWaitQueueLength(ConditionObject)}.
         *
         * @return the estimated number of waiting threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        protected final int getWaitQueueLength() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            int n = 0;
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION)
                    ++n;
            }
            return n;
        }

        /**
         * Returns a collection containing those threads that may be
         * waiting on this Condition.
         * Implements {@link AbstractQueuedSynchronizer#getWaitingThreads(ConditionObject)}.
         *
         * @return the collection of threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        protected final Collection<Thread> getWaitingThreads() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            ArrayList<Thread> list = new ArrayList<Thread>();
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION) {
                    Thread t = w.thread;
                    if (t != null)
                        list.add(t);
                }
            }
            return list;
        }
    }

    /**
     * Setup to support compareAndSet. We need to natively implement
     * this here: For the sake of permitting future enhancements, we
     * cannot explicitly subclass AtomicInteger, which would be
     * efficient and useful otherwise. So, as the lesser of evils, we
     * natively implement using hotspot intrinsics API. And while we
     * are at it, we do the same for other CASable fields (which could
     * otherwise be done with atomic field updaters).
     */
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        try {
            stateOffset = unsafe.objectFieldOffset
                (AbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset
                (AbstractQueuedSynchronizer.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset
                (AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset
                (Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset
                (Node.class.getDeclaredField("next"));

        } catch (Exception ex) { throw new Error(ex); }
    }

    /**
     * CAS head field. Used only by enq.
     */
    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    /**
     * CAS tail field. Used only by enq.
     */
    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    /**
     * CAS waitStatus field of a node.
     */
    private static final boolean compareAndSetWaitStatus(Node node,
                                                         int expect,
                                                         int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset,
                                        expect, update);
    }

    /**
     * CAS next field of a node.
     */
    private static final boolean compareAndSetNext(Node node,
                                                   Node expect,
                                                   Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }
}
