package learn.juc.concurrent.executor;

import java.util.concurrent.*;

/**
 * 使用 Callable、Future 获取执行结果
 *
 */
public class TestCallableFuture {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Task task = new Task();
        Future<Integer> result = executorService.submit(task);
        executorService.shutdown();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("主线程正在执行中");

        try {
            System.out.println("子线程执行结果：" + result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("所有线程执行完毕");
    }
}
