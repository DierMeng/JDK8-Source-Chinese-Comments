package learn.design.patterns.decorator;

/**
 * 面板抽象类
 * @author: glorze.com
 * @since: 2020/7/5 17:24
 */
public abstract class Panel {

    /**
     * 展示面板
     * @Title: display
     * @return void
     */
    abstract void display();

    /**
     * 关闭面板
     * @Title: display
     * @return void
     */
    abstract void close();

    /**
     * 最小化面板
     * @Title: display
     * @return void
     */
    abstract void minimize();
}
