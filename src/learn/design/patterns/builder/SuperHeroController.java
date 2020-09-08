package learn.design.patterns.builder;

/**
 * 建造者模式之超级英雄指挥者
 *
 * @ClassName: SuperHeroController
 * @author: Glorze
 * @since: 2020/8/20 14:19
 */
public class SuperHeroController {
    public SuperHero construct(SuperHeroBuilder superHeroBuilder) {
        SuperHero superHero;
        superHeroBuilder.buildSex();
        superHeroBuilder.buildType();
        superHeroBuilder.buildSkill();
        superHeroBuilder.buildWeapon();
        superHero = superHeroBuilder.createSuperHero();
        return superHero;
    }

    public static void main(String[] args) {
        SuperHeroBuilder superHeroBuilder = new BlackWidow();
        SuperHeroController superHeroController = new SuperHeroController();
        SuperHero superHero = superHeroController.construct(superHeroBuilder);
        System.out.println("性别：" + superHero.getSex());
        System.out.println("类型：" + superHero.getType());
        System.out.println("武器：" + superHero.getWeapon());
        System.out.println("技能：" + superHero.getSkill());
    }
}
