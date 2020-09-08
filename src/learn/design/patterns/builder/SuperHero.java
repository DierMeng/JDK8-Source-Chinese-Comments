package learn.design.patterns.builder;

/**
 * 建造者模式之复杂产品-漫威超级英雄
 *
 * @ClassName: SuperHero
 * @author: Glorze
 * @since: 2020/8/19 17:35
 */
public class SuperHero {

    /**
     * 超级英雄的类型
     *  地球人
     *  外星人
     *  天神
     */
    private String type;

    /**
     * 性别
     *  男
     *  女
     */
    private String sex;

    /**
     * 武器
     */
    private String weapon;

    /**
     * 技能
     */
    private String skill;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }
}
