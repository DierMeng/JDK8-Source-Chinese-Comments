package learn.juc.thread.pool;

import java.util.concurrent.*;

/**
 * 创建带有名字的线程池
 *
 * @ClassName: ThreadPoolWithName
 * @author: Glorze
 * @since: 2020/5/28 21:48
 */
public class ThreadPoolWithName {

    static class NamedThreadFactory implements ThreadFactory {

        private String namePrefix;

        public NamedThreadFactory(String name) {
            namePrefix = "pool-" + name + "-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + "Glorze");
            return thread;
        }
    }

    static ExecutorService executorServiceOne = new ThreadPoolExecutor(5, 5, 100, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("fuxue1"));

    static ExecutorService executorServiceTwo = new ThreadPoolExecutor(5, 5, 100, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("fuxue2"));

    public static void main(String[] args) {
        executorServiceOne.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程池1 执行任务");
                throw new NullPointerException();
            }
        });
        executorServiceTwo.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程池2 执行任务");
            }
        });
    }
}
