package learn.juc.thread.local;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * CS 游戏演示 ThreadLocal 的使用
 * ThreadLocal 严格翻译来应该是 CopyValueIntoEveryThread
 *
 * 源码来自于《码处高效》，侵删
 *
 * @ClassName: CsGameByThreadLocal
 * @author: Glorze
 * @since: 2020/3/16 22:28
 */
public class CsGameByThreadLocal {
    private static final Integer BULLET_NUMBER = 1500;
    private static final Integer KILLED_ENEMIES = 0;
    private static final Integer LIFE_VALUE = 10;
    private static final Integer TOTAL_PLAYERS = 10;

    // 随机数用来展示每个对象的不同的数据
    private static  final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * ThreadLocal 是线程安全的，每个线程都有独立的变量副本
     * ThreadLocal 对象通常是由 private static 修饰的
     * 但是 ThreadLocal 无法解决共享对象的更新问题
     */
    // 初始化子弹数
    private static final ThreadLocal<Integer> BULLET_NUMBER_THREADLOCAL = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return BULLET_NUMBER;
        }
    };

    // 初始化杀敌数
    private static final ThreadLocal<Integer> KILLED_ENEMIES_THREADLOCAL = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return KILLED_ENEMIES;
        }
    };

    // 初始化自己的命数
    private static final ThreadLocal<Integer> LIFE_VALUE_THREADLOCAL = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return LIFE_VALUE;
        }
    };

    /**
     * 定义每位队员
     */
    private static class Player extends Thread {

        @Override
        public void run() {
            Integer bullets = BULLET_NUMBER_THREADLOCAL.get() - RANDOM.nextInt(BULLET_NUMBER);
            Integer killEnemies = KILLED_ENEMIES_THREADLOCAL.get() - RANDOM.nextInt(TOTAL_PLAYERS / 2);
            Integer lifeValue = LIFE_VALUE_THREADLOCAL.get() - RANDOM.nextInt(LIFE_VALUE);

            System.out.println(getName() + ", BULLET_NUMBER is " + bullets);
            System.out.println(getName() + ", KILLED_ENEMIES is " + killEnemies);
            System.out.println(getName() + ", LIFE_VALUE is " + lifeValue);

            BULLET_NUMBER_THREADLOCAL.remove();
            KILLED_ENEMIES_THREADLOCAL.remove();
            LIFE_VALUE_THREADLOCAL.remove();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < TOTAL_PLAYERS; i++) {
            new Player().start();
        }
    }
}
