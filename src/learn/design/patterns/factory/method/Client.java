package learn.design.patterns.factory.method;

/**
 * 工厂方法模式客户端
 *
 * @ClassName: Client
 * @author: glorze.com
 * @since: 2020/7/23 23:14
 */
public class Client {
    public static void main(String[] args) {
        BirdFactory birdFactory = new EagleFactory();
        Bird bird = birdFactory.createBird();
        bird.fly();
    }
}
