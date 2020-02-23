package java.util.concurrent;

public interface Executor {

    /**
     * 通过参数传入待执行线程的对象
     */
    void execute(Runnable command);
}
