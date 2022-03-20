package learn.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK 动态代理示例
 *
 * @author : 徐郡明
 * @ClassName : TestInvokerHandler
 * @since : 2021/2/2 23:05
 */
public class TestInvokerHandler implements InvocationHandler {

    /**
     * 真正的业务对象，也就是 ReslSubject 对象
     */
    private Object target;

    public TestInvokerHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(target, args);
        return result;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), target.getClass().getInterfaces(), this);
    }

    public static void main(String[] args) {

    }
}
