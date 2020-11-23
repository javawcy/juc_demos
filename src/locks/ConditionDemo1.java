package locks;

import locks.condition_threads.PutThread;
import locks.condition_threads.TakeThread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 缓冲队列实现
 * 假定有一个缓冲队列，支持 put 和 take 方法。
 * 如果试图在空队列中执行 take 操作，则线程将一直阻塞，直到队列中有可用元素；
 * 如果试图在满队列上执行 put 操作，则线程也将一直阻塞，直到队列不满
 * <p>
 * 为了防止发生“虚假唤醒”， Condition 一般都是在一个循环中被等待
 * </P>
 *
 * @author Chongyu
 * @since 2020/11/23
 */
public class ConditionDemo1 {

    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public final Object[] items = new Object[100];
    private int putPtr, takePtr, count;

    public void put(Object o) throws InterruptedException {
        lock.lock();
        try {
            //队列装满，则不满条件线程立即wait让线程不再添加元素
            while (count == items.length)
                notFull.await();
            //否则开始装入
            items[putPtr] = o;
            //加满后起始位置设置为0
            if (++putPtr == items.length)
                putPtr = 0;
            //容量+1
            ++count;
            //通知take
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Object take() throws InterruptedException {
        lock.lock();
        try {
            //队列为空则立即wait让线程不再取元素
            while (count == 0)
                notEmpty.wait();
            //取队列中第一个
            Object item = items[takePtr];
            //索引加1，如果取完了重新设置为0
            if (++takePtr == items.length)
                takePtr = 0;
            //总数-1
            --count;
            //通知put
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        ConditionDemo1 queueDemo = new ConditionDemo1();
        PutThread putThread = new PutThread(queueDemo);
        TakeThread takeThread = new TakeThread(queueDemo);
        putThread.run();
        takeThread.run();
        Thread.sleep(3000);
    }


}
