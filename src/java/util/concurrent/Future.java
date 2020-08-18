package java.util.concurrent;

/**
 * 代表异步计算的结果，通过 Future 接口提供的方法可以查看异步计算是否执行完成，或者等待执行结果并获取执行结果，同时还可以取消执行。
 * 1. 线程执行结果带有返回值
 * 2. 提供了一个线程超时的功能，超过超时时间抛出异常后返回。
 *
 * 虽然可以实时获取异步执行的结果，但是没有通知机制，无法得知任务什么时候完成，为了获取结果，要么使用阻塞的 get() 方法，要么就是使用 isDone() 轮询判断任务是否完成
 */
public interface Future<V> {

    /**
     * 用来取消异步任务的执行。
     * 如果异步任务已经完成或者已经被取消，或者由于某些原因不能取消，则会返回 false。
     * 如果任务还没有被执行，则会返回 true 并且异步任务不会被执行。
     * 如果任务已经开始执行了但是还没有执行完成，若 mayInterruptIfRunning 为 true，则会立即中断执行任务的线程并返回 true，
     * 若 mayInterruptIfRunning 为 false，则会返回 true 且不会中断任务执行线程。
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * 判断任务是否被取消，如果任务在结束（正常执行结束或者执行异常结束）前被取消则返回 true，否则返回 false。
     */
    boolean isCancelled();

    /**
     * 任务执行过程中发生异常、任务被取消也属于任务已完成，也会返回 true。
     * 返回计算是否完成 , 若任务完成则返回 true (任务完成 state = narmal, exception, interrupted)
     */
    boolean isDone();

    /**
     * 获取计算的结果, 若计算没完成, 直接 await, 直到计算结束或线程中断
     *
     * 获取任务执行结果，如果任务还没完成则会阻塞等待直到任务执行完成。
     * 如果任务被取消则会抛出 CancellationException 异常，如果任务执行过程发生异常则会抛出 ExecutionException 异常，
     * 如果阻塞等待过程中被中断则会抛出 InterruptedException 异常。
     */
    V get() throws InterruptedException, ExecutionException;

    /**
     * 获取计算的结果, 若计算没完成, 直接 await, 直到计算结束或线程中断或time时间超时
     */
    V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
}
