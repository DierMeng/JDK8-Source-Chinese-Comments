package learn.design.patterns.factory.method;

/**
 * 具体鸟类: 信鸽类
 *
 * @ClassName: Letter
 * @author: 高老四博客
 * @since: 2020/4/23 17:45
 */
public class Letter implements Bird {

    public Letter() {
        System.out.println("创建一只信鸽!");
    }

    @Override
    public void fly() {
        System.out.println("我要送一封信，起飞！");
    }
}
