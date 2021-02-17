package locks;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchDemo1 {
    private static final CountDownLatch COUNT = new CountDownLatch(1);
    public static void main(String[] args) {
        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName()+"\t开始阻塞");
                COUNT.await();
                System.out.println(Thread.currentThread().getName()+"\t获取锁，执行结束！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"A").start();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName()+"\t开始阻塞");
                COUNT.await();
                System.out.println(Thread.currentThread().getName()+"\t获取锁，执行结束！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"B").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        COUNT.countDown();
        System.out.println("over");
    }
}
