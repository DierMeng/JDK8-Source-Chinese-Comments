package learn.juc.thread.local;

import java.util.concurrent.TimeUnit;

/**
 * 测试 ThreadLocal 无法解决共享对象的更新问题
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: InitValueInThreadLocal
 * @author: Glorze
 * @since: 2020/3/16 23:00
 */
public class InitValueInThreadLocal {

    private static final StringBuilder INIT_VALUE = new StringBuilder("init");

    // 覆写 ThreadLocal 的 initialValue，返回 StringBuilder 静态引用
    private static final ThreadLocal<StringBuilder> builder = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return INIT_VALUE;
        }
    };

    private static class AppendStringThread extends Thread {
        @Override
        public void run() {
            StringBuilder inThread = builder.get();
            for (int i =0; i < 10; i++) {
                inThread.append("-" + i);
            }
            System.out.println(inThread.toString());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new AppendStringThread().start();
        }
        TimeUnit.SECONDS.sleep(10);
    }
}
