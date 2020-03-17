package learn.reference.type;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 软引用测试类，在即将 OOM 之前垃圾回收器会把这些软引用指向的对象加入回收范围
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: SoftReferenceHouse
 * @author: Glorze
 * @since: 2020/3/15 23:03
 */
public class SoftReferenceHouse {
    public static void main(String[] args) {
        // List<House> houses = new ArrayList<>();
        List<SoftReference> houses = new ArrayList<>();
        int i = 0;
        while (true) {
            // houses.add(new House());
            SoftReference<House> buyer2 = new SoftReference<House>(new House());
            houses.add(buyer2);
            System.out.println("i = " + (++i));
        }
    }
}
