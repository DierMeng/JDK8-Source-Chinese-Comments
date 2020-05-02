package learn.juc.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务执行体
 */
public class UserTask implements Runnable {

    private final AtomicLong count = new AtomicLong(0L);

    @Override
    public void run() {
        System.out.println("running_" + count.getAndIncrement());
    }

}
