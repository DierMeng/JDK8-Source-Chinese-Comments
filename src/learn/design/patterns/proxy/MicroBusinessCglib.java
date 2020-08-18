package learn.design.patterns.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 代购微商产业
 *
 * @ClassName: MicroBusinessCglib
 * @author: glorze.com
 * @since: 2020/7/12 16:45
 */
public class MicroBusinessCglib implements MethodInterceptor {


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        before();
        Object object = methodProxy.invoke(o, objects);
        after();
        return object;
    }

    private void before() {
        System.out.println("帮助真正的买家去免税店购买商品！");
    }
    private void after() {
        System.out.println("帮助真正的买家去购买萝卜丁口红！");
    }

    public static void main(String[] args) {
        TrueShopper trueShopper = new TrueShopper();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(trueShopper.getClass());
        TrueShopper ts = (TrueShopper) enhancer.create();
        ts.shop();

    }
}
