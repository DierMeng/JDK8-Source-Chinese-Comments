package java.util.concurrent.atomic;
import java.util.function.LongBinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * DoubleAccumulator、DoubleAdder、LongAccumulator、LongAdder 的父类
 *
 * 就像一个 AtomicLong，里面维持一个 volatile 的 base，还有一个 cell 数组，cell 数组主要是存储线程需要增加或减少的值，
 * 它能够将竞争的线程分散到自己内部的私有 cell 数组里面，所以当并发量很大的时候，线程会被部分分发去访问内部的 cell 数组。
 */
@SuppressWarnings("serial")
abstract class Striped64 extends Number {

    /**
     * 伪共享：让当前线程执行操作变量处于一个独立的 cache line（CPU 指令缓冲行）里面。
     *
     * 用 @sun.misc.Contended 来杜绝为共享。用来保存冲突时需要增加的格子。cell CAS 方式。
     */
    @sun.misc.Contended static final class Cell {
        volatile long value;
        Cell(long x) { value = x; }
        // CAS 操作
        final boolean cas(long cmp, long val) {
            return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
        }

        // Unsafe mechanics
        private static final sun.misc.Unsafe UNSAFE;
        private static final long valueOffset;
        static {
            try {
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class<?> ak = Cell.class;
                valueOffset = UNSAFE.objectFieldOffset
                    (ak.getDeclaredField("value"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /**
     * cpu 的个数，绑定的 table
     */
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    /**
     * 用于存储冲突的线程
     * cells 数组，大小为 2 的倍数
     *
     * 存放 Cell 的表。当不为空时大小是 2 的幂。
     */
    transient volatile Cell[] cells;

    /**
     * 基础的值。当没有冲突或冲突很少时，就会在 base 上操作，而不用加入 cell，也就是 AtomicLong 的原理
     * 不冲突下直接在 base 上增加,通过 CAS 更改。
     *
     * base 值，在没有竞争时使用，也作为表初始化竞争时的一个后备。
     */
    transient volatile long base;

    /**
     * 判断 cells 是否有线程在使用的变量，通过 CAS 去锁定。
     * 判断 cells 是否被一个线程使用了，如果一个线程使用，就不自旋一次换个 PROBE 来进行。
     *
     * 自旋锁，在 resizing 和/或创建 Cell 时使用。
     */
    transient volatile int cellsBusy;

    /**
     * Package-private default constructor
     */
    Striped64() {
    }

    /**
     * CASes the base field.
     */
    final boolean casBase(long cmp, long val) {
        return UNSAFE.compareAndSwapLong(this, BASE, cmp, val);
    }

    /**
     * CASes the cellsBusy field from 0 to 1 to acquire lock.
     */
    final boolean casCellsBusy() {
        return UNSAFE.compareAndSwapInt(this, CELLSBUSY, 0, 1);
    }

    /**
     * Returns the probe value for the current thread.
     * Duplicated from ThreadLocalRandom because of packaging restrictions.
     */
    static final int getProbe() {
        return UNSAFE.getInt(Thread.currentThread(), PROBE);
    }

    /**
     * Pseudo-randomly advances and records the given probe value for the
     * given thread.
     * Duplicated from ThreadLocalRandom because of packaging restrictions.
     */
    static final int advanceProbe(int probe) {
        probe ^= probe << 13;   // xorshift
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        UNSAFE.putInt(Thread.currentThread(), PROBE, probe);
        return probe;
    }

    /**
     * 里面可以重新改变 table 大小，或者创建新的 cells
     *
     * @param x 增加的 long 值
     * @param fn 函数式编程，代表一个一个待执行操作的函数
     * @param wasUncontended false if CAS failed before call
     */
    final void longAccumulate(long x, LongBinaryOperator fn, boolean wasUncontended) {
        int h;
        if ((h = getProbe()) == 0) {
            // 如果当前线程没有初始化，就初始化当前线程
            ThreadLocalRandom.current(); // force initialization
            h = getProbe();
            wasUncontended = true;
        }
        // // 最后的槽不为空则 true，也用于控制扩容，false重试。
        boolean collide = false;                // True if last slot nonempty
        for (;;) {
            Cell[] as; Cell a; int n; long v;
            if ((as = cells) != null && (n = as.length) > 0) {
                // cell 有值，表已经初始化
                if ((a = as[(n - 1) & h]) == null) {
                    // 线程所映射到的槽是空的，尝试关联新的 cell
                    // 进入这个方法，就说明这个位置没线程，所以你可以进来。进来后再看能不能获取到 cell 锁。
                    if (cellsBusy == 0) {       // Try to attach new Cell
                        // 新建一个 cell，并且尝试加进去
                        // 锁未被使用，乐观的创建并初始化 cell
                        Cell r = new Cell(x);   // Optimistically create
                        if (cellsBusy == 0 && casCellsBusy()) {
                            // 锁仍然是空闲的、且成功获取到锁
                            boolean created = false;
                            try {               // Recheck under lock
                                // 在持有锁时再次检查槽是否空闲
                                Cell[] rs; int m, j;
                                if ((rs = cells) != null && (m = rs.length) > 0 && rs[j = (m - 1) & h] == null) {
                                    // 所映射的槽仍为空
                                    // 关联 cell 到槽
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                // 释放锁
                                cellsBusy = 0;
                            }
                            if (created) {
                                // 成功创建 cell 并关联到槽，退出
                                break;
                            }
                            // 槽现在不为空了
                            continue;           // Slot is now non-empty
                        }
                    }
                    // 锁被占用了，重试
                    collide = false;
                }
                // 已经知道 CAS 失败
                else if (!wasUncontended) {       // CAS already known to fail
                    // 在重散列后继续
                    wasUncontended = true;      // Continue after rehash
                }
                // 在当前槽的 cell 上尝试更新
                else if (a.cas(v = a.value, ((fn == null) ? v + x : fn.applyAsLong(v, x)))) {
                    break;
                }
                // 表大小达到上限或者扩容了
                // 表达到上限后就不会再尝试下面 if 的扩容了，只会重散列，尝试其他槽
                else if (n >= NCPU || cells != as) {
                    collide = false;            // At max size or stale
                }
                // 如果不存在冲突，则设置为存在冲突
                else if (!collide) {
                    collide = true;
                }
                // 有竞争力，需要扩容
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        // 扩容操作，锁空闲且成功获取到锁
                        // 距离上一次检查后表没有改变，扩容：加倍
                        if (cells == as) {      // Expand table unless stale
                            Cell[] rs = new Cell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            cells = rs;
                        }
                    } finally {
                        // 释放锁
                        cellsBusy = 0;
                    }
                    collide = false;
                    // 在扩容后的表上重试
                    continue;                   // Retry with expanded table
                }
                // 没办法获取锁，冲散列，尝试其他槽
                h = advanceProbe(h);
            }
            // 加锁的情况初始化表
            else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
                // 这是 cell 初始化的过程
                // 直接修改 base 不成功，所以来修改 cells 做文章。
                // cell 为 null，但是 cellsBusy=0，但是有，加入一个 cell 中。
                boolean init = false;
                try {                           // Initialize table
                    if (cells == as) {
                        // 最开始 cells 的大小为 2
                        Cell[] rs = new Cell[2];
                        // 给要增加的 x，做一个 cell 的坑。
                        rs[h & 1] = new Cell(x);
                        cells = rs;
                        init = true;
                    }
                } finally {
                    // 释放锁
                    cellsBusy = 0;
                }
                if (init) {
                    // 成功初始化，已更新，跳出循环
                    break;
                }
            }
            else if (casBase(v = base, ((fn == null) ? v + x : fn.applyAsLong(v, x)))) {
                // cell 为 null 并且 cellsBusy 为 1，也就是说，现在有人用 cells，我就去尝试更新 base 吧，借用 CAS 这个 base 来实现
                // 表违背初始化，可能正在初始化，回退使用 base
                break;                          // Fall back on using base
            }
        }
    }

    /**
     * Same as longAccumulate, but injecting long/double conversions
     * in too many places to sensibly merge with long version, given
     * the low-overhead requirements of this class. So must instead be
     * maintained by copy/paste/adapt.
     */
    final void doubleAccumulate(double x, DoubleBinaryOperator fn,
                                boolean wasUncontended) {
        int h;
        if ((h = getProbe()) == 0) {
            ThreadLocalRandom.current(); // force initialization
            h = getProbe();
            wasUncontended = true;
        }
        boolean collide = false;                // True if last slot nonempty
        for (;;) {
            Cell[] as; Cell a; int n; long v;
            if ((as = cells) != null && (n = as.length) > 0) {
                if ((a = as[(n - 1) & h]) == null) {
                    if (cellsBusy == 0) {       // Try to attach new Cell
                        Cell r = new Cell(Double.doubleToRawLongBits(x));
                        if (cellsBusy == 0 && casCellsBusy()) {
                            boolean created = false;
                            try {               // Recheck under lock
                                Cell[] rs; int m, j;
                                if ((rs = cells) != null &&
                                    (m = rs.length) > 0 &&
                                    rs[j = (m - 1) & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                cellsBusy = 0;
                            }
                            if (created)
                                break;
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;
                }
                else if (!wasUncontended)       // CAS already known to fail
                    wasUncontended = true;      // Continue after rehash
                else if (a.cas(v = a.value,
                               ((fn == null) ?
                                Double.doubleToRawLongBits
                                (Double.longBitsToDouble(v) + x) :
                                Double.doubleToRawLongBits
                                (fn.applyAsDouble
                                 (Double.longBitsToDouble(v), x)))))
                    break;
                else if (n >= NCPU || cells != as)
                    collide = false;            // At max size or stale
                else if (!collide)
                    collide = true;
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        if (cells == as) {      // Expand table unless stale
                            Cell[] rs = new Cell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            cells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                h = advanceProbe(h);
            }
            else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
                boolean init = false;
                try {                           // Initialize table
                    if (cells == as) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(Double.doubleToRawLongBits(x));
                        cells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            }
            else if (casBase(v = base,
                             ((fn == null) ?
                              Double.doubleToRawLongBits
                              (Double.longBitsToDouble(v) + x) :
                              Double.doubleToRawLongBits
                              (fn.applyAsDouble
                               (Double.longBitsToDouble(v), x)))))
                break;                          // Fall back on using base
        }
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long BASE;
    private static final long CELLSBUSY;
    private static final long PROBE;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> sk = Striped64.class;
            BASE = UNSAFE.objectFieldOffset
                (sk.getDeclaredField("base"));
            CELLSBUSY = UNSAFE.objectFieldOffset
                (sk.getDeclaredField("cellsBusy"));
            Class<?> tk = Thread.class;
            PROBE = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomProbe"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
