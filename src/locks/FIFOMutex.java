package locks;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>
 * lockSupport demo first-in-first-out
 * </P>
 *
 * @author Chongyu
 * @since 2020/11/23
 */
public class FIFOMutex {

    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final Queue<Thread> waiters = new ConcurrentLinkedQueue<>();

    public void lock() {
        Thread thread = Thread.currentThread();
        waiters.add(thread);

        //当前线程不在队首或锁已经被占用，则当前线程阻塞
        while (waiters.peek() != thread || !locked.compareAndSet(false, true)) {
            LockSupport.park(this);
        }

        waiters.remove();
    }

    public void unlock() {
        locked.set(false);
        LockSupport.unpark(waiters.peek());
    }
}
