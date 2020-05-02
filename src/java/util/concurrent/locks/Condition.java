package java.util.concurrent.locks;
import java.util.concurrent.TimeUnit;
import java.util.Date;

/**
 * 多线程间协调通信的工具类，使得某个或者某些线程一起等待某个条件（Condition）,
 * 只有当该条件具备( signal 或者 signalAll 方法被调用)时 ，这些等待线程才会被唤醒，从而重新争夺锁。
 */
public interface Condition {

    /**
     * 造成当前线程在接到信号或被中断之前一直处于等待状态。
     */
    void await() throws InterruptedException;

    /**
     * 造成当前线程在接到信号之前一直处于等待状态。
     */
    void awaitUninterruptibly();

    /**
     * 造成当前线程在接到信号、被中断或到达指定等待时间之前一直处于等待状态。
     */
    long awaitNanos(long nanosTimeout) throws InterruptedException;

    /**
     * 造成当前线程在接到信号、被中断或到达指定等待时间之前一直处于等待状态。
     */
    boolean await(long time, TimeUnit unit) throws InterruptedException;

    /**
     * 造成当前线程在接到信号、被中断或到达指定最后期限之前一直处于等待状态。
     */
    boolean awaitUntil(Date deadline) throws InterruptedException;

    /**
     * 唤醒一个等待线程。
     */
    void signal();

    /**
     * 唤醒所有等待线程。
     */
    void signalAll();
}
