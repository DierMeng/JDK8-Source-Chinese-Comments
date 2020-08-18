package learn.design.patterns.proxy;

/**
 * 设计模式之代理模式
 *
 * @ClassName: ProxyPerson
 * @author: glorze.com
 * @since: 2020/7/12 16:24
 */
public class ProxyPerson implements Person {

    private TrueShopper trueShopper;

    public ProxyPerson(TrueShopper trueShopper) {
        this.trueShopper = trueShopper;
    }

    @Override
    public void shop() {
        System.out.println("帮助真正的买家去免税店购买商品！");
        this.trueShopper.shop();
        System.out.println("帮助真正的买家去购买萝卜丁口红！");
    }

    public static void main(String[] args) {
        ProxyPerson proxyPerson = new ProxyPerson(new TrueShopper());
        proxyPerson.shop();
    }
}