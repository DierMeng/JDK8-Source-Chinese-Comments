package learn.design.patterns.prototype;

/**
 * 原型设计模式之学生类
 *
 * @ClassName: Student
 * @author: Glorze
 * @since: 2020/8/12 22:51
 */
public class Student {

    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
