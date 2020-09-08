package learn.design.patterns.builder;

/**
 * 具体建造者
 * 创建一个黑寡妇英雄角色
 *
 * @ClassName: BlackWidow
 * @author: Glorze
 * @since: 2020/8/19 17:45
 */
public class BlackWidow extends SuperHeroBuilder {

    @Override
    void buildType() {
        superHero.setType("地球人");
    }

    @Override
    void buildSex() {
        superHero.setSex("女");
    }

    @Override
    void buildWeapon() {
        superHero.setWeapon("制服诱惑");
    }

    @Override
    void buildSkill() {
        superHero.setSkill("寿命长");
    }
}
