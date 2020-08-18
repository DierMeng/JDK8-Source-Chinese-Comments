package learn.design.patterns.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代购微商产业
 *
 * @ClassName: MicroBusiness
 * @author: glorze.com
 * @since: 2020/7/12 16:45
 */
public class MicroBusiness implements InvocationHandler {

    /**
     * 目标代理对象
     */
    private Person target;

    public MicroBusiness(Person target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        Object person = method.invoke(this.target, args);
        after();
        return person;
    }

    private void before() {
        System.out.println("帮助真正的买家去免税店购买商品！");
    }
    private void after() {
        System.out.println("帮助真正的买家去购买萝卜丁口红！");
    }

    public static void main(String[] args) {
        Person person = new TrueShopper();
        MicroBusiness microBusiness = new MicroBusiness(person);
        Person p = (Person) Proxy.newProxyInstance(Person.class.getClassLoader(), new Class[] {Person.class}, microBusiness);
        // 动态代理对象调用目标对象方法实现invoke方法的调用
        p.shop();
    }
}
