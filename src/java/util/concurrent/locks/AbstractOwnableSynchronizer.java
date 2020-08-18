package java.util.concurrent.locks;

/**
 * AQS 的父类
 *
 * 可以通过该类来设置和取独占锁的拥有者线程。
 * 主要提供一个 exclusiveOwnerThread 属性，用于关联当前持有该锁的线程。
 */
public abstract class AbstractOwnableSynchronizer implements java.io.Serializable {

    /** Use serial ID even though all fields transient. */
    private static final long serialVersionUID = 3737899427754241961L;

    /**
     * Empty constructor for use by subclasses.
     */
    protected AbstractOwnableSynchronizer() { }

    /**
     * 互斥模式同步下的当前线程，独占锁
     */
    private transient Thread exclusiveOwnerThread;

    /**
     * 设置当前拥有独占访问的线程。锁的拥有线程，null 参数表示没有线程拥有访问。
     * 此方法不另外施加任何同步或 volatile 字段访问。
     */
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    /**
     * 返回由 setExclusiveOwnerThread 最后设置的线程；
     * 如果从未设置，则返回 null。
     * 此方法不另外施加任何同步或 volatile 字段访问。
     */
    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }
}
