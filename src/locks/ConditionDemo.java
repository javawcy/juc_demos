package locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition
 */
public class ConditionDemo {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    public static void main(String[] args) {
        //A线程先获取锁
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"\tget the lock");
                condition.await();
                System.out.println(Thread.currentThread().getName()+"\tweak up");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println(Thread.currentThread().getName()+"\trelease the lock");
            }
        },"A").start();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"\tget the lock");
                condition.signal();
                System.out.println(Thread.currentThread().getName()+"\tsignal condition");
            } finally {
                lock.unlock();
                System.out.println(Thread.currentThread().getName()+"\trelease the lock");
            }
        },"B").start();

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println("over");
    }
}
