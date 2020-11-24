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
public class AQSLockDemo {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock(true);
        AQSLockThread A = new AQSLockThread("A", lock);
        AQSLockThread B = new AQSLockThread("B", lock);
        AQSLockThread C = new AQSLockThread("C", lock);

        A.start();
        B.start();
        C.start();

        A.join();
        B.join();
        C.join();
    }
}
