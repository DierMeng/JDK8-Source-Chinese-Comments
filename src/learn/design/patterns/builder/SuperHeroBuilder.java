package learn.design.patterns.builder;

/**
 * 建造者模式之漫威英雄建造者接口
 *
 * @InterfaceName: SuperHeroBuilder
 * @author: Glorze
 * @since: 2020/8/19 17:40
 */
public abstract class SuperHeroBuilder {

    protected SuperHero superHero = new SuperHero();

    abstract void buildType();
    abstract void buildSex();
    abstract void buildWeapon();
    abstract void buildSkill();

    public SuperHero createSuperHero() {
        return superHero;
    }
}
