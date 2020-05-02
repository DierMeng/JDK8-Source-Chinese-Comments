package learn.basic.data.type;

/**
 * 一些基本数据类型小常识
 *
 * 源码来自于《阿里技术》公众号，侵删
 */
public class DoubleTest {

    public static void main(String[] args) {

        // 如果表示的小数存在精度丢失，则为 false
        // 如果表示的小数不存在精度丢失，则为 true
        float a = 0.126f;
        double b = 0.126d;
        System.out.println((a -b) == 0.0);
        // 输出 Infinity
        // 在整型运算中，除数是不能为0的，否则直接运行异常。但是在浮点数运算中，引入了无限这个概念。
        System.out.println(1.0 / 0);
        // 输出 NaN
        // Double 包装类下，public static final double NaN = 0.0d / 0.0；NAN 表示非数字，它与任何值都不相等，甚至不等于它自己。
        System.out.println(0.0 / 0.0);

    }

}
