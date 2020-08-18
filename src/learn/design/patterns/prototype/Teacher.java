package learn.design.patterns.prototype;

/**
 * 原型设计模式之教师类
 *
 * Java 中浅拷贝代码示例
 *
 * @ClassName: Teacher
 * @author: Glorze
 * @since: 2020/8/12 22:55
 */
public class Teacher implements Cloneable {

    private String name;

    private Student student;

    public Teacher() {
    }

    public Teacher(String name, Student student) {
        this.name = name;
        this.student = new Student();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Teacher teacher = new Teacher(name, student);
        return teacher;
    }

    public static void main(String[] args) throws CloneNotSupportedException {
        Teacher t1, t2;
        t1 = new Teacher();
        Student student = new Student();
        student.setName("高老四博客");
        student.setAge(27);
        t1.setName("Glorze");
        t1.setStudent(student);
        t2 = (Teacher) t1.clone();
        System.out.println("t1 和 t2 是否相同：" + (t1 == t2));
        System.out.println("原型对象和克隆对象的 Student 实例是否相同：" + (t1.getStudent() == t2.getStudent()));
    }
}
