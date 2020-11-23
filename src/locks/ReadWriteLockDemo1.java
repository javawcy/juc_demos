package locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * ReadWriteLock
 * </P>
 *
 * @author Chongyu
 * @since 2020/11/23
 */
public class ReadWriteLockDemo1 {

    public static void main(String[] args) {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        final Lock lock1 = lock.readLock();
        final Lock lock2 = lock.writeLock();
    }
}
