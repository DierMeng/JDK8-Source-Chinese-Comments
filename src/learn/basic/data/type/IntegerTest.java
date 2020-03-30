package learn.basic.data.type;

/**
 * 两个 Integer 类型的变量相等的判断
 *
 * @ClassName: IntegerTest
 * @author: Glorze
 * @since: 2020/3/28 16:41
 */
public class IntegerTest {
    public static void main(String[] args) {
        Integer ia = 88;
        Integer ib = 88;
        Integer ic = 200;
        Integer id = 200;
        System.out.println("两个88自动装箱后是否相等: " + (ia == ib));
        System.out.println("两个200自动装箱后是否相等: " + (ic == id));
    }
}
