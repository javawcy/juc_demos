package locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * Lock
 * </P>
 *
 * @author Chongyu
 * @since 2020/11/23
 */
public class LockInterfaceDemo1 {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        if (lock.tryLock()) {
            try {
                System.out.println("get the lock and do some thing");
            } finally {
                //使用finally确保锁被正常释放
                lock.unlock();
            }
        }
    }

}
