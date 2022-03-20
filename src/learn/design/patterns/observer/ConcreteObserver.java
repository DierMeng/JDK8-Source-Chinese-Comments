package learn.design.patterns.observer;

/**
 * 具体的观察者
 *
 * @author : 高老四
 * @ClassName : ConcreteObserver
 * @since : 2021/7/11 00:51
 */
public class ConcreteObserver implements Observer {

    @Override
    public void update() {
        System.out.println("接收到变化后，我要执行自己的业务逻辑 glorze.com");
    }
}
