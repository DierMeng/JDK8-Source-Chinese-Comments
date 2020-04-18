package learn.leetcode;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1188.实现一个拥有如下方法的线程安全有限阻塞队列
 *  BoundedBlockingQueue(int capacity) 构造方法初始化队列，其中 capacity 代表队列长度上限.
 *  void enqueue(int element) 在队首增加一个 element。如果队列满，调用线程被阻塞直到队列非满
 *  int dequeue() 返回队尾元素并从队列中将其删除。如果队列为空，调用线程被阻塞直到队列非空
 * 你的实现将会被多线程同事访问进行测试，每一个线程要么是一个只调用 enqueue 方法的生产者线程，要么是一个只调用 dequeue 方法的消费者线程。size 方法将会在每一个测试用例之后进行调用。
 * 请不要用内置的有限阻塞队列实现。否则不通过。
 *
 * 解题思路：
 *  双端队列可以使用 LinkedList 来设计
 *  队列元素的实时数量处理要用原子性、可见性来保证
 *  线程安全可以使用 synchronized 关键字或者使用锁来实现
 *
 * @ClassName: BoundedBlockingQueue
 * @author: 高泽
 * @since: 2020/4/14 14:28
 */
public class BoundedBlockingQueue {

    // 用线程安全的集合实现一个阻塞队列
    private LinkedList<Integer> list = new LinkedList<>();
    // 存储当前队列元素个数
    AtomicInteger size = new AtomicInteger(0);
    // 存储队列长度上限
    private volatile int capacity;
    // 可重入锁
    private Lock lock = new ReentrantLock();
    Condition procuder = lock.newCondition();
    Condition consumer = lock.newCondition();

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void enqueue(int element) throws InterruptedException {
        try {
            // synchronized (this)
            lock.lock();
            // 如果队列满，调用线程被阻塞直到队列非满。
            while (size.get() >= capacity) {
                // 线程阻塞时会释放锁
                procuder.await();
                // this.wait();
            }
            // 在队首增加一个element
            list.addFirst(element);
            // 当前队列元素个数加一
            size.incrementAndGet();
            // 通知消费者线程可以继续消费了
            consumer.signal();
            // this.notify();
        } finally {
            lock.unlock();
        }
    }

    public int dequeue() throws InterruptedException {
        try {
            lock.lock();
            // synchronized (this)
            // 如果队列空，调用线程被阻塞直到队列非空。
            while (size.get() == 0) {
                // 线程阻塞时会释放锁
                consumer.await();
                // this.wait();
            }
            // 返回队尾元素并从队列中将其删除
            int value = list.getLast();
            list.removeLast();
            // 当前队列元素个数减一
            size.decrementAndGet();
            // 通知生产者线程可以继续生产了
            procuder.signal();
            // this.notify();
            return value;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        return size.get();
    }

}
