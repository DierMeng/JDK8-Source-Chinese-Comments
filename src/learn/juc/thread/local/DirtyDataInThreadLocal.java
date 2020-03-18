package learn.juc.thread.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模拟线程池中的线程可能会读取到上一个线程缓存的数据信息从而导致脏数据的问题
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: DirtyDataInThreadLocal
 * @author: Glorze
 * @since: 2020/3/17 22:05
 */
public class DirtyDataInThreadLocal {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

    public static void main(String[] args) {
        // 使用固定大小为 1 的线程池，说明上一个的线程属性会被下一个线程属性复用
        ExecutorService pool = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 2; i++) {
            MyThread myThread = new MyThread();
            pool.execute(myThread);
        }
    }

    private static class MyThread extends Thread {
        private static boolean flag = true;

        @Override
        public void run() {
            if (flag) {
                threadLocal.set(this.getName() + ", session info.");
                flag = false;
            }
            System.out.println(this.getName() + " 线程是 " + threadLocal.get());
        }
    }
}
