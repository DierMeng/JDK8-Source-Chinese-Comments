package learn.reference.type;

/**
 * 软引用实体测试类
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: House
 * @author: Glorze
 * @since: 2020/3/15 23:01
 */
public class House {
    private static final Integer DOOR_NUMBER = 2000;
    public Door[] doors = new Door[DOOR_NUMBER];

    private String Homeowner;

    public String getHomeowner() {
        return Homeowner;
    }

    public void setHomeowner(String homeowner) {
        Homeowner = homeowner;
    }

    public House() {
    }

    public House(String homeowner) {
        Homeowner = homeowner;
    }

    class Door {

    }
}
