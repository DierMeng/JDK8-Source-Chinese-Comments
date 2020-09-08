package learn.design.patterns.builder;

/**
 * 具体建造者
 * 创建一个钢铁侠英雄角色
 *
 * @ClassName: IronMan
 * @author: Glorze
 * @since: 2020/8/19 17:44
 */
public class IronMan extends SuperHeroBuilder {

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
        superHero.setWeapon("弑杀者装甲");
    }

    @Override
    void buildSkill() {
        superHero.setSkill("电气工程、机械工程");
    }
}
