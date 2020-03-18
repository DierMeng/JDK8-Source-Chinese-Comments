package learn.design.patterns.singleton;

/**
 * 基本的单例示例，也叫懒汉式单例
 * 特点就是在但是被需要使用的时候才创建实例，所以在并发编程的环境下要考虑线程安全问题
 *
 * @ClassName: Singleton
 * @author: Glorze
 * @since: 2020/3/18 22:02
 */
public class BasicSingleton {

    /**
     * 使用一个类变量来缓存曾经创建的实例
     */
    private static BasicSingleton instance;

    /**
     * 对构造器使用 private 修饰，隐藏该构造器
     * @descript 高老四博客
     */
    private BasicSingleton() {

    }

    /**
     * 提供一个静态方法，用于返回 Singleton 实例
     * 该方法可以加入自定义控制，保证只产生一个 Singleton 对象就好
     * @Title: getInstance
     * @return Singleton
     * @author: 高老四博客
     * @since: 2018年3月18日 下午4:58:46
     */
    public static BasicSingleton getInstance() {
        if (null == instance) {
            instance = new BasicSingleton();
        }
        return instance;
    }

    public static void main(String[] args) {
        BasicSingleton singleton1 = BasicSingleton.getInstance();
        BasicSingleton singleton2 = BasicSingleton.getInstance();
        System.out.println(singleton1 == singleton2);
    }
}
