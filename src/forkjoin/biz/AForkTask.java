package forkjoin.biz;

import java.util.concurrent.RecursiveTask;

/**
 * <p>
 * Description
 * </P>
 *
 * @author Chongyu
 * @since 2021/2/17
 */
public class AForkTask extends RecursiveTask<ADTO> {

    private static final long serialVersionUID = -4697875732969906551L;
    private final ADTO adto;
    private final AService1 aService1;
    private final AService2 aService2;
    private final int flag;

    public AForkTask(ADTO adto, AService1 aService1, AService2 aService2, int flag) {
        this.aService1 = aService1;
        this.aService2 = aService2;
        this.flag = flag;
        this.adto = adto;
    }

    @Override
    protected ADTO compute() {
        switch (this.flag) {
            case 1:
                String r1 = aService1.result();
                this.adto.setAResult1(r1);
                break;
            case 2:
                String r2 = aService2.result();
                this.adto.setAResult2(r2);
                break;
            default:
                AForkTask A1 = new AForkTask(this.adto, aService1, aService2, 1);
                A1.fork();
                AForkTask A2 = new AForkTask(this.adto, aService1, aService2, 2);
                A2.fork();
                invokeAll(A1, A2);
                this.adto.setAResult1(A1.join().getAResult1());
                this.adto.setAResult2(A2.join().getAResult2());
        }
        return this.adto;
    }
}
