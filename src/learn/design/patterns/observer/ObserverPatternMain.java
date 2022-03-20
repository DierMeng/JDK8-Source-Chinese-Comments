package learn.design.patterns.observer;

/**
 * 运行
 *
 * @author : 高老四
 * @ClassName : ObserverPatternMain
 * @since : 2021/7/11 00:53
 */
public class ObserverPatternMain {

    public static void main(String[] args) {

        Observer observer1 = new ConcreteObserver();
        Observer observer2 = new ConcreteObserver();
        Observer observer3 = new ConcreteObserver();
        Observer observer4 = new ConcreteObserver();

        Subject subject = new ConcreteSubject();
        subject.attach(observer1);
        subject.attach(observer2);
        subject.attach(observer3);
        subject.attach(observer4);

        subject.notifyObserver();


    }
}
