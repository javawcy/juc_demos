package locks;

import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * Description
 * </P>
 *
 * @author Chongyu
 * @since 2020/11/24
 */
public class AQSLockThread extends Thread {

    private final String name;
    private final ReentrantLock lock;

    public AQSLockThread(String name, ReentrantLock lock) {
        this.name = name;
        this.lock = lock;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            System.out.println(this.name + " get the lock");
        } finally {
            lock.unlock();
        }
    }
}
