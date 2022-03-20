package learn.design.patterns.chainofresponsibility;

/**
 * 客户端
 *
 * @author : 高老四
 * @ClassName : Client
 * @since : 2022/3/13 21:43
 */
public class Client {

    public static void main(String[] args) {
        Handler handler = new ConcreteHandlerOne();
        handler.setHandler(new ConcreteHandlerTwo());
        handler.handleReuqest("oom.cool");
    }
}
