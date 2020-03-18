package learn.juc.thread.synchronize;

import java.util.concurrent.Semaphore;

/**
 * 基于信号的同步类 Semaphore 测试
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: CustomCheckWindow
 * @author: Glorze
 * @since: 2020/3/18 23:06
 */
public class CustomCheckWindow {

    public static void main(String[] args) {
        // 设定三个信号量，即 3 个服务窗口
        // 如果信号量设置为，就是典型的互斥锁
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i <= 5; i++) {
            new SecurityCheckThread(i, semaphore).start();
        }
    }

    private static class SecurityCheckThread extends Thread {
        private int seq;
        private Semaphore semaphore;
        public SecurityCheckThread(int seq, Semaphore semaphore) {
            this.seq = seq;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                System.out.println("No." + seq + "乘客，正在检查中");
                if (seq % 2 == 0) {
                    Thread.sleep(1000L);
                    System.out.println("No." + seq + "乘客，身份可疑，不能出国！");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
                System.out.println("No." + seq + "乘客已完成服务。");
            }
        }
    }
}
