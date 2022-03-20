package learn.juc.concurrent.executor;

import java.util.concurrent.Callable;

public class Task implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        System.out.println("子线程进行业务处理中");
        Thread.sleep(3000);
        int sum = 0;
        for (int i = 0; i < 100; i++) {
            sum += i;
        }
        return sum;
    }
}
