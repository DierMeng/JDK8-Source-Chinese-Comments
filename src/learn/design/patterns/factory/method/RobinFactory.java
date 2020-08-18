package learn.design.patterns.factory.method;

/**
 * 知更鸟工厂类
 *
 * @ClassName: EagleFactory
 * @author: 高老四博客
 * @since: 2020/7/23 23:09
 */
public class RobinFactory implements BirdFactory {

    @Override
    public Bird createBird() {
        Bird bird = new Robin();
        return bird;
    }
}
