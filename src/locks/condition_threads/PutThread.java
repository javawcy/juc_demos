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
public class PutThread implements Runnable{

    private final ConditionDemo1 demo1;

    public PutThread(ConditionDemo1 demo1) {
        this.demo1 = demo1;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                demo1.put(i);
                System.out.println("put in "+i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
