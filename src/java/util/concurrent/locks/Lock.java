package java.util.concurrent.locks;
import java.util.concurrent.TimeUnit;

public interface Lock {

    /**
     * 获取锁。如果锁不可用，出于线程调度目的，将禁用当前线程，并且在获得锁之前，该线程将一直处于休眠状态。
     */
    void lock();

    /**
     * 如果当前线程未被中断，则获取锁。
     * 如果锁可用，则获取锁，并立即返回。
     * 如果锁不可用，出于线程调度目的，将禁用当前线程
     *  1.该线程将一直处于休眠状态：锁由当前线程获得；
     *  2.其他某个线程中断当前线程，并且支持对锁获取的中断。
     *
     * 如果当前线程：在进入此方法时已经设置了该线程的中断状态；
     * 在获取锁时被中断，并且支持对锁获取的中断，则将抛出  InterruptedException，并清除当前线程的已中断状态。
     */
    void lockInterruptibly() throws InterruptedException;

    /**
     * 仅在调用时锁为空闲状态才获取该锁。
     * 如果锁可用，则获取锁，并立即返回值  true 。
     * 如果锁不可用，则此方法将立即返回值  false 。
     * 通常对于那些不是必须获取锁的操作可能有用。
     */
    boolean tryLock();

    /**
     * 如果锁在给定的等待时间内空闲，并且当前线程未被中断，则获取锁。
     * 如果锁可用，则此方法将立即返回值  true 。
     * 如果锁不可用，出于线程调度目的，将禁用当前线程
     */
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    /**
     * 释放锁。
     * 对应于 lock()、tryLock()、lockInterruptibly() 等操作，如果成功的话应该对应着一个 unlock()，这样可以避免死锁或者资源浪费。
     */
    void unlock();

    /**
     * 返回用来与此 Lock 实例一起使用的 Condition 实例。
     */
    Condition newCondition();
}
