package learn.juc.concurrent.executor;

import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture 并行使用示例
 *
 * 源码来自廖雪峰，侵删
 */
public class TestParalleCompletableFuture {
    public static void main(String[] args) throws InterruptedException {
        // 两个 Completable 执行异步查询
        CompletableFuture<String> cfQueryFromSina = CompletableFuture.supplyAsync(() -> {
            return queryCode("高老四博客");
        });
        CompletableFuture<String> cfQueryFromNetease = CompletableFuture.supplyAsync(() -> {
            return queryCode("高老四博客");
        });
        // 用 anyof 合并为一个新的 CompletableFuture
        CompletableFuture<Object> cfQuery = CompletableFuture.anyOf(cfQueryFromSina, cfQueryFromNetease);

        // 两个 Completable 执行异步查询
        CompletableFuture<Double> cfFetchFromSina = cfQuery.thenApplyAsync(code -> {
            return fetchPrice((String) code);
        });
        CompletableFuture<Double> cfFetchFromNetease = cfQuery.thenApplyAsync(code -> {
            return fetchPrice((String) code);
        });
        // 用 anyof 合并为一个新的 CompletableFuture
        CompletableFuture<Object> cfFetch = CompletableFuture.anyOf(cfFetchFromSina, cfFetchFromNetease);
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
