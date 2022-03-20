package learn.design.patterns.observer;

/**
 * 观察者
 *
 * @author : 高老四
 * @InterfaceName : Observer
 * @since : 2021/7/11 00:43
 */
public interface Observer {

    /**
     * 观察者接到通知后执行的业务逻辑
     * @Title : update
     * @return : void
     */
    void update();

}
