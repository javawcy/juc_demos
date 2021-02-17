package forkjoin.biz;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * <p>
 * Description
 * </P>
 *
 * @author Chongyu
 * @since 2021/2/17
 */
public class BizMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(4);
        BizForkTask task = new BizForkTask(new SomeDTO(),new AService1(),new AService2(), new BService(), new CService(), 0);
        pool.submit(task);
        System.out.println(task.get());
    }
}
