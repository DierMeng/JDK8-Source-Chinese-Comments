package learn.classloader;

/**
 * Class 对象测试
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: One
 * @author: Glorze
 * @since: 2020/3/29 23:13
 */
public class One {

    private String inner = "time flies";

    public void call() {
        System.out.println("hello worid.");
    }

    public String getInner() {
        return inner;
    }
}
