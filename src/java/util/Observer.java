package java.util;

/**
 * A class can implement the <code>Observer</code> interface when it
 * wants to be informed of changes in observable objects.
 *∑
 * @author  Chris Warth
 * @see     java.util.Observable
 * @since   JDK1.0
 */
public interface Observer {
    /**
     * 提供一个 update 方法用于接收通知者的通知做出相应改变，
     * 其中第一个 Observable 类型的参数是被观察者的引用，当需要与被观察者进行交互的时候，就需要这个引用
     * 另一个 Object 类型的参数是被观察者传递过来的信息
     *
     * @param   o     被观察者的引用
     * @param   arg   被观察者传来的信息
     */
    void update(Observable o, Object arg);
}
