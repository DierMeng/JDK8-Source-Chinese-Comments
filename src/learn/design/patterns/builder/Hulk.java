package learn.design.patterns.builder;

/**
 * TODO
 *
 * @ClassName: Hulk
 * @author: Glorze
 * @since: 2020/8/19 17:45
 */
public class Hulk extends SuperHeroBuilder {

    @Override
    void buildType() {
        superHero.setType("地球人");
    }

    @Override
    void buildSex() {
        superHero.setSex("男");
    }

    @Override
    void buildWeapon() {
        superHero.setWeapon("电缆蔓");
    }

    @Override
    void buildSkill() {
        superHero.setSkill("徒手搏斗");
    }
}
