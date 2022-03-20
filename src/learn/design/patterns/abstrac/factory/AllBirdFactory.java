package learn.design.patterns.abstrac.factory;

import learn.design.patterns.factory.method.Bird;
import learn.design.patterns.factory.method.Eagle;
import learn.design.patterns.factory.method.Letter;
import learn.design.patterns.factory.method.Robin;

/**
 * 有生命的鸟具体工厂
 *
 * @author : 高老四
 * @ClassName : RealLifeBirdFactory
 * @since : 2021/8/22 22:33
 */
public class AllBirdFactory implements BirdFactory {


    @Override
    public Eagle createEagleBird() {
        return new Eagle();
    }

    @Override
    public Letter createLetterBird() {
        return new Letter();
    }

    @Override
    public Robin createRobinBird() {
        return new Robin();
    }

}
