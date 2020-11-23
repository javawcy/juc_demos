package locks.fifo_threads;

import locks.FIFOMutex;

/**
 * <p>
 *
 * </P>
 *
 * @author Chongyu
 * @since 2020/11/23
 */
public class FIFOThread extends Thread{
    private final String name;
    private final FIFOMutex mutex;
    public static int count;

    public FIFOThread(String name, FIFOMutex mutex) {
        this.name = name;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            mutex.lock();
            count ++;
            System.out.println("name:"+name+" count:"+count);
            mutex.unlock();
        }
    }
}
