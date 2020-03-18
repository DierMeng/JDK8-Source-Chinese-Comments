package learn.juc.thread.synchronize;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 基于执行时间的同步类 CountDownLatch
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: CountDownLatchTest
 * @author: Glorze
 * @since: 2020/3/18 22:41
 */
public class CountDownLatchTest {
    // 如果主线程不进行异常的抛出，不会执行 countDown 方法
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch count = new CountDownLatch(3);
        Thread thread1 = new TranslateThread("1st content", count);
        Thread thread2 = new TranslateThread("2nd content", count);
        Thread thread3 = new TranslateThread("3rd content", count);
        thread1.start();
        thread2.start();
        thread3.start();
        count.await(10L, TimeUnit.SECONDS);
        System.out.println("所有线程执行完成");
    }
}
