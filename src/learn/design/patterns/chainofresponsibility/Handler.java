package learn.design.patterns.chainofresponsibility;

/**
 * 抽象处理类
 *
 * @author : 高老四
 * @ClassName : Handler
 * @since : 2022/3/13 21:38
 */
public abstract class Handler {

    public Handler handler;

    public abstract void handleReuqest(String request);

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
