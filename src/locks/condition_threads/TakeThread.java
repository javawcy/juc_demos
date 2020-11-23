package locks.condition_threads;

import locks.ConditionDemo1;

/**
 * <p>
 *
 * </P>
 *
 * @author Chongyu
 * @since 2020/11/23
 */
public class TakeThread implements Runnable{

    private final ConditionDemo1 queueDemo;

    public TakeThread(ConditionDemo1 queueDemo) {
        this.queueDemo = queueDemo;
    }

    @Override
    public void run() {
        for (int i = 0; i < queueDemo.items.length; i++) {
            try {
                final Object take = queueDemo.take();
                System.out.println(take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
