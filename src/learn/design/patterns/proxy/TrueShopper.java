package learn.design.patterns.proxy;

/**
 * 设计模式之代理模式
 *
 * @ClassName: TrueShopper
 * @author: 高老四博客
 * @since: 2020/7/12 16:22
 */
public class TrueShopper implements Person {

    @Override
    public void shop() {
        // 因为自己买太贵，需要找代购买免税的商品
        System.out.println("我要买一只免税的萝卜丁口红！");
    }
}
