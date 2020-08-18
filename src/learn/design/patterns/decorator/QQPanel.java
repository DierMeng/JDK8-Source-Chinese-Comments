package learn.design.patterns.decorator;

/**
 * QQ 面板实现类
 *
 * @ClassName: QQPanel
 * @author: 高老四博客
 * @since: 2020/7/5 16:57
 */
public class QQPanel extends Panel {

    @Override
    public void display() {
        System.out.println("展示 QQ 面板！");
    }

    @Override
    public void close() {
        System.out.println("关闭 QQ 面板！");
    }

    @Override
    public void minimize() {
        System.out.println("最小化 QQ 面板！");
    }
}
