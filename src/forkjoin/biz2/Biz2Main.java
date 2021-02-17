package forkjoin.biz2;

import forkjoin.biz.*;

import java.util.concurrent.ForkJoinPool;

/**
 * <p>
 * Description
 * </P>
 *
 * @author Chongyu
 * @since 2021/2/17
 */
public class Biz2Main {
    public static void main(String[] args) {
        ExtendForkJoinPool pool = new ForkJoinPoolFactory().getObject();
        SomeDTO context = new SomeDTO(new ADTO(null, null));
        DefaultForkJoinDataLoader<SomeDTO> loader = new DefaultForkJoinDataLoader<>(context);

        AService1 aService1 = new AService1();
        AService2 aService2 = new AService2();
        BService bService = new BService();
        CService cService = new CService();

        loader.addTask(ctx -> {
            ctx.setResultB(bService.result());
        });

        loader.addTask(ctx -> {
            ctx.setResultC(cService.result());
        });

        DefaultForkJoinDataLoader<SomeDTO> subTask = new DefaultForkJoinDataLoader<>(context);
        subTask.addTask(ctx -> {
            ctx.getResultA().setAResult1(aService1.result());
        });
        subTask.addTask(ctx -> {
            ctx.getResultA().setAResult2(aService2.result());
        });

        loader.addTask(subTask);
        pool.invoke(loader);
        System.out.println(context);
    }

}
