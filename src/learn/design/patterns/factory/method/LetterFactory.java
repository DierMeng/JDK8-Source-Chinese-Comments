package learn.design.patterns.factory.method;

/**
 * 信鸽工厂类
 *
 * @ClassName: EagleFactory
 * @author: 高老四博客
 * @since: 2020/7/23 23:09
 */
public class LetterFactory implements BirdFactory {

    @Override
    public Bird createBird() {
        Bird bird = new Letter();
        return bird;
    }
}
