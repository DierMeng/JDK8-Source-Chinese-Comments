package learn.reference.type;

import java.lang.ref.WeakReference;

/**
 * 弱引用测试类，如果内存没有达到 OOM，软引用持有的对象会被回收。
 * 即当对象置为 null 时自动感知，并且主动断开引用指向的对象
 * 相当于买房子，没几天发现房产证是假的 被骗
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: SoftReferenceHouse
 * @author: Glorze
 * @since: 2020/3/15 23:03
 */
public class WeakReferenceWhenIdle {
    public static void main(String[] args) {
        House seller = new House();
        WeakReference<House> buyer3 = new WeakReference<House>(seller);
        seller = null;

        long start = System.nanoTime();
        int count = 0;
        while (true) {
            if (buyer3.get() == null) {
                long duration = (System.nanoTime() - start) / (1000 * 1000);
                System.out.println("house is null and exited time = " + duration + "ms");
                break;
            } else {
                System.out.println("still there. count = " + (count++));
            }
        }
    }
}
