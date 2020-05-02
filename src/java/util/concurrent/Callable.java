package java.util.concurrent;

/**
 * 可以在 Callable 实现中声明强类型的返回值，甚至是抛出异常。
 *
 * Callable 是个泛型接口，泛型 V 就是要 call() 方法返回的类型。
 * Callable 接口和 Runnable 接口很像，都可以被另外一个线程执行，但是 Runnable 不会返回数据也不能抛出异常。
 */
@FunctionalInterface
public interface Callable<V> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    V call() throws Exception;
}
