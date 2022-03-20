package learn.juc.concurrent.executor;

import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture 串行使用示例
 *
 * 源码来自廖雪峰，侵删
 */
public class TestSerialCompletableFuture {
    public static void main(String[] args) throws InterruptedException {
        // 第一个任务
        CompletableFuture<String> cfQuery = CompletableFuture.supplyAsync(() -> {
            return queryCode("高老四博客");
        });
        // cfQuery 查询成功之后继续执行下一个任务
        CompletableFuture<Double> cfFetch = cfQuery.thenApplyAsync(code -> {
            return fetchPrice(code);
        });
        // cfFetch 成功之后打印结果
        cfFetch.thenAccept(result -> {
            System.out.println("价格为：" + result);
        });
        // 主线程不要立刻结束
        Thread.sleep(2000);

    }

    private static String queryCode(String name) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "5200808";
    }

    private static Double fetchPrice(String code) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 8 + Math.random() * 18;
    }
}
