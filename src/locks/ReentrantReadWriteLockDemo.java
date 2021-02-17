package locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁demo
 */
public class ReentrantReadWriteLockDemo {
    private static final ReentrantReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock(true);
    private static final Lock READ_LOCK = READ_WRITE_LOCK.readLock();
    private static final Lock WRITE_LOCK = READ_WRITE_LOCK.writeLock();
    public static void main(String[] args) {
        new Thread(() -> {
            READ_LOCK.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"\tget read lock");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                READ_LOCK.unlock();
            }
        },"A").start();

        new Thread(() -> {
            READ_LOCK.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"\tget read lock");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                READ_LOCK.unlock();
            }
        },"B").start();

        new Thread(() -> {
            WRITE_LOCK.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"\tget write lock");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                WRITE_LOCK.unlock();
            }
        },"C").start();

        new Thread(() -> {
            READ_LOCK.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"\tget read lock");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                READ_LOCK.unlock();
            }
        },"D").start();

    }

}
