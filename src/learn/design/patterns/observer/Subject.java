package learn.design.patterns.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 主题目标接口
 *
 * 被观察者
 *
 * @author : 高老四
 * @InterfaceName : Subject
 * @since : 2021/7/11 00:42
 */
public interface Subject {

    List<Observer> observerList = new ArrayList<>();

    void attach(Observer observer);

    void detach(Observer observer);

    void notifyObserver();
}
