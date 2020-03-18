package learn.design.patterns.singleton;

/**
 * 按需初始化实现的单例
 * Iodh 全称 Initialization on Demand Holder
 * 主要解决饿汉式单例不能实现延迟加载，解决懒汉式单例因为线程安全控制带来的性能和代码繁琐的诟病
 *
 * @ClassName: IodhSingleton
 * @author: Glorze
 * @since: 2020/3/18 22:28
 */
public class IodhSingleton {

    /**
     * 对构造器使用 private 修饰，隐藏该构造器
     * @descript 高老四博客
     */
    private IodhSingleton() {

    }

    /**
     * 声明静态内部类在第一次调用的时候初始化 instance
     * @ClassName HolderClass
     * @author: glorze.com
     * @since: 2018年3月18日 下午5:56:44
     */
    private static class HolderClass {
        private final static IodhSingleton INSTANCE = new IodhSingleton();
    }

    /**
     * 提供一个静态方法，用于返回 Singleton 实例
     * 该方法可以加入自定义控制，保证只产生一个 Singleton 对象就好
     * 利用内部类返回实例
     * @Title: getInstance
     * @return IodhSingleton
     * @author: 高老四博客
     * @since: 2018年3月18日 下午5:58:10
     */
    public static IodhSingleton getInstance() {
        return HolderClass.INSTANCE;
    }

    public static void main(String[] args) {
        IodhSingleton iodhSingleton1 = IodhSingleton.getInstance();
        IodhSingleton iodhSingleton2 = IodhSingleton.getInstance();
        System.out.println(iodhSingleton1 == iodhSingleton2);
    }
}
