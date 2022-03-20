package java.util;

/**
 * 被观察者
 *
 * @author  Chris Warth
 * @see     java.util.Observable#notifyObservers()
 * @see     java.util.Observable#notifyObservers(java.lang.Object)
 * @see     java.util.Observer
 * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
 * @since   JDK1.0
 */
public class Observable {

    /**
     * 用来标志是否变化，默认无变化
     */
    private boolean changed = false;

    /**
     * 存储观察者对象
     */
    private Vector<Observer> obs;

    /**
     * 空构造方法
     */
    public Observable() {
        obs = new Vector<>();
    }

    /**
     * 增加观察者
     *
     * @param   o   增加的对象
     * @throws NullPointerException   if the parameter o is null.
     */
    public synchronized void addObserver(Observer o) {
        if (o == null)
            throw new NullPointerException();
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    /**
     * 删除观察者
     * @param   o   被删除的对象
     */
    public synchronized void deleteObserver(Observer o) {
        obs.removeElement(o);
    }

    /**
     * 通知所有观察者更新
     *
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void notifyObservers() {
        notifyObservers(null);
    }

    /**
     * 知所有观察者更新
     *
     * @param   arg   通知内容
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void notifyObservers(Object arg) {
        /*
         * a temporary array buffer, used as a snapshot of the state of
         * current Observers.
         */
        Object[] arrLocal;

        synchronized (this) {

            /* 对 change 加同步锁，防止多线程下各线程对 changed 变量的读写操作不安全
             * 可能出现脏读因此产生重复 update 或者不能 update 的情况
             */
            if (!changed) {
                // 如果没有变化，直接返回
                return;
            }
            arrLocal = obs.toArray();
            // 重置变化
            clearChanged();
        }

        // 循环通知更新
        for (int i = arrLocal.length-1; i>=0; i--)
            ((Observer)arrLocal[i]).update(this, arg);
    }

    /**
     * 删除所有观察者
     */
    public synchronized void deleteObservers() {
        obs.removeAllElements();
    }

    /**
     * 设置变化，调用后 changed 为 true
     */
    protected synchronized void setChanged() {
        changed = true;
    }

    /**
     * 清除变化，调用后 changed 为 false
     *
     * @see     java.util.Observable#notifyObservers()
     * @see     java.util.Observable#notifyObservers(java.lang.Object)
     */
    protected synchronized void clearChanged() {
        changed = false;
    }

    /**
     * 是否变化
     *
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#setChanged()
     */
    public synchronized boolean hasChanged() {
        return changed;
    }

    /**
     * 统计观察者数量然后返回
     */
    public synchronized int countObservers() {
        return obs.size();
    }
}
