package learn.juc.thread.synchronize;

/**
 * JVM 指令重排序测试类
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: InstructionReordering
 * @author: Glorze
 * @since: 2020/3/18 21:55
 */
public class InstructionReordering extends Thread {

    @Override
    public void run() {
        int x = 1;
        int y = 2;
        int z = 3;
        // 此处 x 的重新赋值会与上面的变量初始化一起执行
        // CPU 会对质量重排序，发现可以将 x 最终的值一并处理完毕，在进行下面的加法操作
        x = x + 1;
        int sum = x + y +z;
    }
}
