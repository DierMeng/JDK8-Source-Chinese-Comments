package learn.reference.type;

import java.util.WeakHashMap;

/**
 * WeakHashMap 适用于缓存不敏感的临时信息的场景
 * 比如用户登录系统后的浏览路径在关闭浏览器后可以自动清空
 * 这种特性也用在了 ThreadLocal 上
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: WeakHashMapTest
 * @author: Glorze
 * @since: 2020/3/16 21:35
 */
public class WeakHashMapTest {
    public static void main(String[] args) {
        House seller1 = new House("1 号卖家房源");
        SellerInfo sellerInfo1 = new SellerInfo();

        House seller2 = new House("2 号卖家房源");
        SellerInfo sellerInfo2 = new SellerInfo();

        // 如果换成 HashMap，则 key 是对 House 对象的强引用
        WeakHashMap<House, SellerInfo> weakHashMap = new WeakHashMap<House, SellerInfo>();
        weakHashMap.put(seller1, sellerInfo1);
        weakHashMap.put(seller2, sellerInfo2);
        System.out.println("weakHashMap before null, size = " + weakHashMap.size());

        seller1 = null;

        System.gc();
        System.runFinalization();

        System.out.println("WeakHashMap after null, size = " + weakHashMap);
        System.out.println(weakHashMap);
    }
}
