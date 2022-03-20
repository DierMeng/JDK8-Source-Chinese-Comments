package learn.design.patterns.observer;

/**
 * 具体的目标主题，被观察者，维护观察者列表
 *
 * @author : 高老四
 * @ClassName : ConcreteSubject
 * @since : 2021/7/11 00:47
 */
public class ConcreteSubject implements Subject {

    @Override
    public void attach(Observer observer) {
        observerList.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObserver() {
        observerList.forEach(Observer::update);
    }
}
