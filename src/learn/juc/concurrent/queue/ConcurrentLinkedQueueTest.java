package learn.juc.concurrent.queue;

import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 无界非阻塞队列使用测试类
 *
 * ConcurrentLinkedQueue CAS 并发控制元素的存储，从头部取元素，从尾部插入，遍历效率低下，size 方法因为 CAS 没加锁，并发环境下会不准确。
 *
 * @ClassName: ConcurrentLinkedQueueTest
 * @author: Glorze
 * @since: 2020/5/28 22:25
 */
public class ConcurrentLinkedQueueTest {
    public static void main(String[] args) {
        ConcurrentLinkedQueue<Integer> concurrentLinkedQueue = new ConcurrentLinkedQueue<Integer>();
        concurrentLinkedQueue.add(1);
        concurrentLinkedQueue.add(2);
        concurrentLinkedQueue.add(3);
        concurrentLinkedQueue.add(4);
        concurrentLinkedQueue.add(5);
        concurrentLinkedQueue.add(6);
        concurrentLinkedQueue.add(7);
        concurrentLinkedQueue.add(8);

        concurrentLinkedQueue.poll();
        concurrentLinkedQueue.remove(2);
        concurrentLinkedQueue.offer(9);
        for (Integer i : concurrentLinkedQueue) {
            System.out.println(i);
        }
    }
}
