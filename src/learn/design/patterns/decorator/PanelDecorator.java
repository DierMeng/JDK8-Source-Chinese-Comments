package learn.design.patterns.decorator;

/**
 * 面板修饰抽象类
 * @author: glorze.com
 * @since: 2020/7/5 17:28
 */
public abstract class PanelDecorator extends Panel {

    private Panel panel;

    public PanelDecorator(Panel panel) {
        this.panel = panel;
    }

    @Override
    public void display() {
        panel.display();
    }

    @Override
    public void close() {
        panel.close();
    }

    @Override
    public void minimize() {
        panel.minimize();
    }
}
