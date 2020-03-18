package learn.design.patterns.singleton;

/**
 * 饿汉式单例
 *
 * @ClassName: HungrySingle
 * @author: Glorze
 * @since: 2020/3/18 22:05
 */
public class HungrySingle {
    /**
     * 使用一个类变量来缓存曾经创建的实例
     * 类加载的时候就创建单例对象
     */
    private static final HungrySingle instance = new HungrySingle();

    /**
     * 对构造器使用 private 修饰，隐藏该构造器
     * @descript 高老四博客
     */
    private HungrySingle() {

    }

    /**
     * 提供一个静态方法，用于返回 Singleton 实例
     * 该方法可以加入自定义控制，保证只产生一个 Singleton 对象就好
     * @Title: getInstance
     * @return HungrySingle
     * @author: 高老四博客
     * @since: 2018年3月18日 下午5:17:30
     */
    public static HungrySingle getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        HungrySingle hungrySingle1 = HungrySingle.getInstance();
        HungrySingle hungrySingle2 = HungrySingle.getInstance();
        System.out.println(hungrySingle1 == hungrySingle2);
    }
}
