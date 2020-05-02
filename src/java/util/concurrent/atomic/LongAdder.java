package java.util.concurrent.atomic;
import java.io.Serializable;

/**
 * LongAdder 中会维护一组（一个或多个）变量，这些变量加起来就是要以原子方式更新的 long 型变量。
 * 当更新方法 add(long) 在线程间竞争时，该组变量可以动态增长以减缓竞争。
 * 方法 sum() 返回当前在维持总和的变量上的总和。
 * 与 AtomicLong 相比，LongAdder 更多地用于收集统计数据，而不是细粒度的同步控制。在低并发环境下，两者性能很相似。
 * 但在高并发环境下，LongAdder 有着明显更高的吞吐量，但是有着更高的空间复杂度。
 *
 * cell 存在且更新无竞争，其余情况都通过 Striped64 的 longAccumulate 方法来完成。
 *
 * @since 1.8
 */
public class LongAdder extends Striped64 implements Serializable {
    private static final long serialVersionUID = 7249069246863182397L;

    /**
     * 构造函数，创建初始和为 0 的新加法器
     */
    public LongAdder() {
    }

    /**
     * 增加 x
     */
    public void add(long x) {
        Cell[] as; long b, v; int m; Cell a;
        if ((as = cells) != null || !casBase(b = base, b + x)) {
            // 如果 cells 不为 null，或者 CAS base 变量失败，说明冲突了，
            // 置 uncontended 为 true
            boolean uncontended = true;
            if (as == null || (m = as.length - 1) < 0 || (a = as[getProbe() & m]) == null || !(uncontended = a.cas(v = a.value, v + x))) {
                // 当 as 为 null，或 as 的长度小于等于 1，或 a 为 null
                // 判断符合调用父类方法进行相加操作
                // 如果所映射的槽不为空，且成功更新则返回，否则进入复杂处理流程。
                longAccumulate(x, null, uncontended);
            }
        }
    }

    /**
     * 自增
     */
    public void increment() {
        add(1L);
    }

    /**
     * 自减
     */
    public void decrement() {
        add(-1L);
    }

    /**
     * 求和,base 值加上每个 cell 的值。
     */
    public long sum() {
        Cell[] as = cells; Cell a;
        long sum = base;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    sum += a.value;
            }
        }
        return sum;
    }

    /**
     * 重置 cell 数组
     */
    public void reset() {
        Cell[] as = cells; Cell a;
        base = 0L;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    a.value = 0L;
            }
        }
    }

    /**
     * 求和并重置
     */
    public long sumThenReset() {
        Cell[] as = cells; Cell a;
        long sum = base;
        base = 0L;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null) {
                    sum += a.value;
                    a.value = 0L;
                }
            }
        }
        return sum;
    }

    /**
     * Returns the String representation of the {@link #sum}.
     * @return the String representation of the {@link #sum}
     */
    public String toString() {
        return Long.toString(sum());
    }

    /**
     * Equivalent to {@link #sum}.
     *
     * @return the sum
     */
    public long longValue() {
        return sum();
    }

    /**
     * Returns the {@link #sum} as an {@code int} after a narrowing
     * primitive conversion.
     */
    public int intValue() {
        return (int)sum();
    }

    /**
     * Returns the {@link #sum} as a {@code float}
     * after a widening primitive conversion.
     */
    public float floatValue() {
        return (float)sum();
    }

    /**
     * Returns the {@link #sum} as a {@code double} after a widening
     * primitive conversion.
     */
    public double doubleValue() {
        return (double)sum();
    }

    /**
     * Serialization proxy, used to avoid reference to the non-public
     * Striped64 superclass in serialized forms.
     * @serial include
     */
    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 7249069246863182397L;

        /**
         * The current value returned by sum().
         * @serial
         */
        private final long value;

        SerializationProxy(LongAdder a) {
            value = a.sum();
        }

        /**
         * Return a {@code LongAdder} object with initial state
         * held by this proxy.
         *
         * @return a {@code LongAdder} object with initial state
         * held by this proxy.
         */
        private Object readResolve() {
            LongAdder a = new LongAdder();
            a.base = value;
            return a;
        }
    }

    /**
     * Returns a
     * <a href="../../../../serialized-form.html#java.util.concurrent.atomic.LongAdder.SerializationProxy">
     * SerializationProxy</a>
     * representing the state of this instance.
     *
     * @return a {@link SerializationProxy}
     * representing the state of this instance
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * @param s the stream
     * @throws java.io.InvalidObjectException always
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.InvalidObjectException {
        throw new java.io.InvalidObjectException("Proxy required");
    }

}
