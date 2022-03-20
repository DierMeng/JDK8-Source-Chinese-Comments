package learn.design.patterns.abstrac.factory;

import learn.design.patterns.factory.method.Bird;
import learn.design.patterns.factory.method.Eagle;
import learn.design.patterns.factory.method.Letter;
import learn.design.patterns.factory.method.Robin;

/**
 * 飞行行为抽象工厂
 *
 * @InterfaceName: FlyFactory
 * @author: glorze.com
 * @since: 2020/7/23 23:05
 */
public interface BirdFactory {

    Eagle createEagleBird();

    Letter createLetterBird();

    Robin createRobinBird();
}
