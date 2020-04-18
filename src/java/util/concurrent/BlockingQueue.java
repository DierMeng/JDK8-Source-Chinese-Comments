package java.util.concurrent;

import java.util.Collection;
import java.util.Queue;

/**
 * 当获取元素时，如果队列为空，则一直等待直到队列非空。当存储元素时，如果队列中没有空间进行存储，则一直等待直到有空间进行存储。
 */
public interface BlockingQueue<E> extends Queue<E> {

    /**
     * 将指定的元素插入到此队列中，如果不违反容量限制立即执行此操作
     * 成功后返回 true
     * 如果当前没有可用空间，则抛出 IllegalStateException。
     */
    boolean add(E e);

    /**
     * 将指定的元素插入到此队列中，如果不违反容量限制立即执行此操作
     * 成功后返回 true
     * 如果当前没有可用空间，失败会返回 false 而不抛出异常
     */
    boolean offer(E e);

    /**
     * 将指定的元素插入到此队列中，如果空间不够用会等待空间可用（一直阻塞）。
     */
    void put(E e) throws InterruptedException;

    /**
     * offer(E e) 的超时退出
     */
    boolean offer(E e, long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * 检索并删除此队列的头，等待元素可用（一直阻塞）。
     */
    E take() throws InterruptedException;

    /**
     * E take() 的超时设置
     */
    E poll(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * 返回该队列最好可以（在没有存储器或资源约束）接受而不会阻塞，或附加的元素的数量
     * 如果没有固定的限制默认为 Integer.MAX_VALUE
     */
    int remainingCapacity();

    /**
     * 从该队列中删除指定元素
     * 成功返回 true
     * 没有这个元素或者删除失败返回 false
     */
    boolean remove(Object o);

    /**
     * 如果此队列包含指定的元素，则返回 true 。
     */
    public boolean contains(Object o);

    /**
     * 从该队列中删除所有可用的元素，并将它们添加到给定的集合中。
     */
    int drainTo(Collection<? super E> c);

    /**
     * 最多从该队列中删除给定数量的可用元素，并将它们添加到给定的集合中。
     */
    int drainTo(Collection<? super E> c, int maxElements);
}
