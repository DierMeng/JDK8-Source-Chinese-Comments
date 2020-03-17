package learn.reference.type;

import java.lang.ref.SoftReference;

/**
 * 软引用测试类，如果内存没有达到 OOM，软引用持有的对象不会被回收。
 * 相当于租房子，只要一直续租，房东一直租，就一直能住。
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: SoftReferenceHouse
 * @author: Glorze
 * @since: 2020/3/15 23:03
 */
public class SoftReferenceWhenIdle {
    public static void main(String[] args) {
        House seller = new House();
        SoftReference<House> buyer2 = new SoftReference<House>(seller);
        seller = null;
        while (true) {
            // 建议 JVM 进行垃圾回收
            System.gc();
            System.runFinalization();
            if (buyer2.get() == null) {
                System.out.println("house is null.");
                break;
            } else {
                System.out.println("still there.");
            }
        }
    }
}
