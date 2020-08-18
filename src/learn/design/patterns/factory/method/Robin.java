package learn.design.patterns.factory.method;

/**
 * 具体鸟类: 知更鸟类
 *
 * @ClassName: Robin
 * @author: 高老四博客
 * @since: 2020/4/23 17:45
 */
public class Robin implements Bird {

    public Robin() {
        System.out.println("创建一只知更鸟!");
    }

    @Override
    public void fly() {
        System.out.println("有人要杀死一只知更鸟，起飞！");
    }
}
