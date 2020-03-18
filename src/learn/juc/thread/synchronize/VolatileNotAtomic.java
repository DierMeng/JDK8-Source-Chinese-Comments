package learn.juc.thread.synchronize;

/**
 * 验证 volatile 不能保证原子性的测试类
 * volatile 解决的是多线程共享变量的可见性问题
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: VolatileNotAtomic
 * @author: Glorze
 * @since: 2020/3/18 22:31
 */
public class VolatileNotAtomic {

    private static volatile long count = 0L;
    private static final int NUMBER = 10000;

    public static class SubtractTread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < NUMBER; i++) {
                // 加锁才能保证线程同步，也就是说 volatile 不能保证原子性
                synchronized (VolatileNotAtomic.class) {
                    count--;
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread subtractThread = new SubtractTread();
        subtractThread.start();

        for (int i = 0; i < NUMBER; i++) {
            // 加锁才能保证线程同步，也就是说 volatile 不能保证原子性
            synchronized (VolatileNotAtomic.class) {
                count++;
            }
        }

        // 等待减法线程结束
        while (subtractThread.isAlive()) {

        }

        System.out.println("count 最后的值为：" + count);
    }
}
