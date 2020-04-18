package java.util.concurrent;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 计数器工具类
 *
 * 初始时需要指定一个计数器的大小，然后可被多个线程并发的实现减 1 操作，并在计数器为 0 后调用 await 方法的线程被唤醒，从而实现多线程间的协作。
 *
 * 任务分为 N 个子线程去执行，state 也初始化为 N（注意N要与线程个数一致）。
 * 这 N 个子线程是并行执行的，每个子线程执行完后 countDown() 一次，state 会 CAS 减 1。
 * 等到所有子线程都执行完后(即 state=0)，会 unpark(线程唤醒) 主调用线程，然后主调用线程就会从 await() 函数返回，继续后余动作。
 *
 * 与 AQS 的独占功能一样，共享锁是否可以被获取的判断为空方法，交由子类去实现。
 * 与 AQS 的独占功能不同，当锁被头节点获取后，独占功能是只有头节点获取锁，其余节点的线程继续沉睡，等待锁被释放后才会唤醒下一个节点的线程，
 *  而共享功能是只要头节点获取锁成功，就在唤醒自身节点对应的线程的同时，继续唤醒 AQS 队列中的下一个节点的线程，每个节点在唤醒自身的同时还会唤醒下一个节点对应的线程，
 *  以实现共享状态的「向后传播」，从而实现共享功能。
 */
public class CountDownLatch {

    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        /**
         * 定义了资源总量 state=count
         */
        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        /**
         * 尝试获取共享锁
         *
         * state 变成 0，就返回 1，表示获取共享锁成功
         * 否则返回 -1，表示获取共享锁失败
         */
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

    private final Sync sync;

    public CountDownLatch(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count < 0");
        }
        this.sync = new Sync(count);
    }

    /**
     * 所有线程调用 await() 都不会等待，所以 CountDownLatch 是一次性的，用完后如果再想用就只能重新创建一个。
     * 如果希望循环使用，推荐使用 CyclicBarrier。
     */
    public void await() throws InterruptedException {
        // 这里其实调用了 AQS 的 acquireSharedInterruptibly 方法
        sync.acquireSharedInterruptibly(1);
    }

    public boolean await(long timeout, TimeUnit unit)
        throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    /**
     * countDown() 不断将 state 减 1,当 state=O 时才能获得锁,释放后 state 就一直为 0
     */
    public void countDown() {
        sync.releaseShared(1);
    }

    /**
     * Returns the current count.
     *
     * <p>This method is typically used for debugging and testing purposes.
     *
     * @return the current count
     */
    public long getCount() {
        return sync.getCount();
    }

    /**
     * Returns a string identifying this latch, as well as its state.
     * The state, in brackets, includes the String {@code "Count ="}
     * followed by the current count.
     *
     * @return a string identifying this latch, as well as its state
     */
    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }
}
