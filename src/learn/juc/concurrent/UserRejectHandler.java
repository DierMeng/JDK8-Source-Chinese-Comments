package learn.juc.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 简单的拒绝策略
 */
public class UserRejectHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
        System.out.println("task rejected. " + executor.toString());
    }
}
