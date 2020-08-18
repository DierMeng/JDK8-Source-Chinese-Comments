package learn.design.patterns.simple.factory.self;

/**
 * 鸟类工厂类,负责各个具体鸟类的初始化、实例化操作
 *
 * @ClassName: BirdFactory
 * @author: 高老四博客
 * @since: 2020/4/23 17:57
 */
public class BirdFactory {

    public static Bird getBird(String birdType) {
        Bird bird = null;
        if (birdType.equals("letter")) {
            bird = new Letter();
            System.out.println("信鸽初始化完毕，等待起飞！");
        } else if (birdType.equals("eagle")) {
            bird = new Eagle();
            System.out.println("老鹰初始化完毕，等待起飞！");
        } else if (birdType.equals("letter")) {
            bird = new Letter();
            System.out.println("信鸽初始化完毕，等待起飞！");
        }
        return bird;
    }

    public static void main(String[] args) {
        Bird bird = BirdFactory.getBird("eagle");
        bird.fly();
    }
}
