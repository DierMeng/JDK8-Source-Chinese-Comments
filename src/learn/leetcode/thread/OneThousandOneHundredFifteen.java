package learn.leetcode.thread;

import java.util.concurrent.Semaphore;

/**
 * 我们提供一个类： 
 * 
 *  
 * class FooBar {
 *   public void foo() {
 *   for (int i = 0; i < n; i++) {
 *    print("foo");
 *    }
 *   }
 * 
 *   public void bar() {
 *   for (int i = 0; i < n; i++) {
 *    print("bar");
 *   }
 *   }
 * }
 *  
 * 
 *  两个不同的线程将会共用一个 FooBar 实例。其中一个线程将会调用 foo() 方法，另一个线程将会调用 bar() 方法。 
 * 
 *  请设计修改程序，以确保 "foobar" 被输出 n 次。 
 * 
 *  
 * 
 *  示例 1: 
 * 
 *  
 * 输入: n = 1
 * 输出: "foobar"
 * 解释: 这里有两个线程被异步启动。其中一个调用 foo() 方法, 另一个调用 bar() 方法，"foobar" 将被输出一次。
 *  
 * 
 *  示例 2: 
 * 
 *  
 * 输入: n = 2
 * 输出: "foobarfoobar"
 * 解释: "foobar" 将被输出两次。
 */
public class OneThousandOneHundredFifteen {
    private int n;
    // 当信号量 Semaphore = 1 时，它可以当作互斥锁使用。其中0、1就相当于它的状态，当=1时表示其他线程可以获取，当=0时，排他，即其他线程必须要等待。
    Semaphore foo = new Semaphore(1);
    Semaphore bar = new Semaphore(0);
    public OneThousandOneHundredFifteen(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {

        for (int i = 0; i < n; i++) {
            foo.acquire();
            printFoo.run();
            bar.release();
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            bar.acquire();
            printBar.run();
            foo.release();
        }
    }
    
}
