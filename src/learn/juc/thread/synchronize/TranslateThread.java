package learn.juc.thread.synchronize;

import java.util.concurrent.CountDownLatch;

/**
 * 翻译类，模拟模拟多个线程对翻译引擎发起翻译请求
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: TranslateThread
 * @author: Glorze
 * @since: 2020/3/18 22:46
 */
public class TranslateThread extends Thread {

    private String content;

    private final CountDownLatch count;

    public TranslateThread(String content, CountDownLatch count) {
        this.content = content;
        this.count = count;
    }

    @Override
    public void run() {
        if (Math.random() > 0.5) {
            throw new RuntimeException("原文存在非法字符");
        }

        System.out.println(content + "的翻译已经完成，译文是。。。");
        count.countDown();
    }
}
