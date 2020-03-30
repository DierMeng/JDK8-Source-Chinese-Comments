package learn.classloader;

import java.lang.reflect.Field;

/**
 * 类加载器
 * Class 测试类
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: ClassTest
 * @author: Glorze
 * @since: 2020/3/29 00:13
 */
public class ClassTest {

    // 数组类型有一个魔法属性: length 来获取数组长度
    private static int[] array = new int[3];
    private static int length = array.length;

    // 任何小写 class 定义的类，也有一个魔法属性: class，来获取此类的大写 Class 类对象
    private static Class<One> one = One.class;
    private static Class<Another> another = Another.class;

    public static void main(String[] args) throws Exception {

        // 通过 newInstance 方法创建 One 和 Another 的类对象
        One oneObject = one.newInstance();
        oneObject.call();

        Another anotherObject = another.newInstance();
        anotherObject.speak();

        // 通过 one 这个大写的 Class 对象，获取私有成员属性对象 Field
        Field privateFieldInOne = one.getDeclaredField("inner");

        // 设置私有对象可以访问和修改
        privateFieldInOne.setAccessible(true);

        privateFieldInOne.set(oneObject, "world changed.");
        // 成功修改类的私有属性 inner 变量值为 world changed.
        System.out.println(oneObject.getInner());

    }

}
