package learn.juc.concurrent.executor;

import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture 的基本使用
 */
public class TestCompletableFuture {
    public static void main(String[] args) throws InterruptedException {
        // 创建异步执行任务
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(TestCompletableFuture::fetchPrice);
        // 如果执行成功
        cf.thenAccept(result -> {
            System.out.println("价格为：" + result);
        });
        // 如果执行异常
        cf.exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
        // 主线程不要立刻结束，否则 CompletableFuture 默认使用的线程池会立刻关闭
        Thread.sleep(200);
    }

    private static Double fetchPrice() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Math.random() < 0.8) {
            throw new RuntimeException("获取价格失败");
        }
        return 5 + Math.random() * 20;
    }
}
