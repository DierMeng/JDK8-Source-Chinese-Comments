package learn.design.patterns.factory.method;

/**
 * 具体鸟类: 老鹰类
 *
 * @ClassName: Eagle
 * @author: 高老四博客
 * @since: 2020/4/23 17:45
 */
public class Eagle implements Bird {

    public Eagle() {
        System.out.println("创建一只老鹰!");
    }

    @Override
    public void fly() {
        System.out.println("我要去运动达人罗志祥家啄米，起飞！");
    }
}
