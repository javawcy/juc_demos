package locks;

import locks.fifo_threads.FIFOThread;

/**
 * <p>
 *
 * </P>
 *
 * @author Chongyu
 * @since 2020/11/23
 */
public class FIFOTests {
    public static void main(String[] args) throws InterruptedException {
        FIFOMutex mutex = new FIFOMutex();
        FIFOThread thread1 = new FIFOThread("thread1",mutex);
        FIFOThread thread2 = new FIFOThread("thread2",mutex);
        FIFOThread thread3 = new FIFOThread("thread3",mutex);
        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();
        assert FIFOThread.count == 300;
        System.out.println("done");
    }
}
