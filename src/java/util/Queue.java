package java.util;

/**
 * 具有基本的集合操作功能，同时还是一个队列。
 */
public interface Queue<E> extends Collection<E> {
    /**
     * 将指定的元素添加入队列，如果队列是有界的且没有空闲空间则抛出异常。
     */
    boolean add(E e);

    /**
     * 将指定的元素添加入队列，如果队列是有界的且没有空闲空间则返回 false。
     * 在使用有界队列时推荐使用该方法来替代 add 方法。
     */
    boolean offer(E e);

    /**
     * 删除并返回队首的元素。
     * 如果队列为空则会抛异常。
     */
    E remove();

    /**
     * 删除并返回队首的元素。
     * 如果队列为空返回 null。
     */
    E poll();

    /**
     * 返回队首元素但是不删除。
     * 如果队列为空会抛出异常。
     */
    E element();

    /**
     * 返回队首元素但是不删除。
     * 如果队列为空则返回 null。
     */
    E peek();
}
