package learn.design.patterns.chainofresponsibility;

/**
 * 具体处理者
 *
 * @author : 高老四
 * @ClassName : ConcreteHandler
 * @since : 2022/3/13 21:41
 */
public class ConcreteHandlerOne extends Handler {

    @Override
    public void handleReuqest(String request) {
        if (true) {
            System.out.println("https://www.oom.cool/");
        } else {
            this.handler.handleReuqest(request);
        }
    }

}
