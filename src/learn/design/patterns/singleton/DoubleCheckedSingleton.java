package learn.design.patterns.singleton;

/**
 * 双重检查锁模式实现的单例，懒汉式，针对线程安全
 * 但是不能完全保证线程同步
 * 对象引用在没有同步的情况下进行读操作，导致用户可能会获取为构造完成的对象，所以对象的实例使用 volatile 修饰
 * volatile 可以限制编译器对其进行相关的读写操作，限制指令重排序，确定对象实例化之后才返回引用
 * 注意：volatile 所有操作都需要同步给内存变量，所以性能慢，执行速度变低。
 *
 * @ClassName: DoubleCheckedSingleton
 * @author: Glorze
 * @since: 2020/3/18 22:22
 */
public class DoubleCheckedSingleton {

    /**
     * 使用一个类变量来缓存曾经创建的实例
     */
    private volatile static DoubleCheckedSingleton instance;

    /**
     * 对构造器使用 private 修饰，隐藏该构造器
     * @descript 高老四博客
     */
    private DoubleCheckedSingleton() {

    }

    /**
     * 提供一个静态方法,用于返回 Singleton 实例
     * 该方法可以加入自定义控制，保证只产生一个 Singleton 对象就好
     * 双重检查锁定来实现懒汉式单例类
     * @Title: getInstance
     * @return BestSingleton
     * @author: 高老四博客
     * @since: 2018年3月18日 下午5:39:03
     */
    public static DoubleCheckedSingleton getInstance() {
        // 第一重判断
        if(null == instance) {
            // 锁定代码块
            synchronized (DoubleCheckedSingleton.class) {
                // 第二重判断
                if (null == instance) {
                    instance = new DoubleCheckedSingleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        DoubleCheckedSingleton bestSingleton1 = DoubleCheckedSingleton.getInstance();
        DoubleCheckedSingleton bestSingleton2 = DoubleCheckedSingleton.getInstance();
        System.out.println(bestSingleton1 == bestSingleton2);
    }
}
