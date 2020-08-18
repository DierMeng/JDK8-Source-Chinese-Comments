package learn.design.patterns.decorator;

/**
 * 具体的面板装饰实现类
 *
 * @ClassName: SkinPanelDecorator
 * @author: Glorze
 * @since: 2020/7/5 17:04
 */
public class SkinPanelDecorator extends PanelDecorator {

    public SkinPanelDecorator(Panel panel) {
        super(panel);
    }

    @Override
    public void display() {
        super.display();
        this.changeSkin();
    }

    /**
     * 更换 QQ 皮肤
     * @Title: changeSkin
     * @return void
     */
    public void changeSkin() {
        System.out.println("为当前 QQ 面板更换「凉爽夏日」皮肤！");
    }

    public static void main(String[] args) {
        Panel QQPanel = new QQPanel();
        Panel decoratorPanel = new SkinPanelDecorator(QQPanel);
        decoratorPanel.display();
  }
}
