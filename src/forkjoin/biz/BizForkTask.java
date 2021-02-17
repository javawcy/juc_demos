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
public class BizForkTask extends RecursiveTask<SomeDTO> {

    private final SomeDTO someDTO;
    private final AService1 aService1;
    private final AService2 aService2;
    private final BService bService;
    private final CService cService;
    private final int flag;

    public BizForkTask(SomeDTO someDTO, AService1 aService1, AService2 aService2, BService bService, CService cService, int flag) {
        this.someDTO = someDTO;
        this.aService1 = aService1;
        this.aService2 = aService2;
        this.bService = bService;
        this.cService = cService;
        this.flag = flag;
    }

    @Override

    protected SomeDTO compute() {

        switch (flag) {
            case 1:
                this.someDTO.setResultB(bService.result());
                break;
            case 2:
                this.someDTO.setResultC(cService.result());
                break;
            default:
                AForkTask A = new AForkTask(new ADTO(null, null), aService1, aService2, 0);
                A.fork();
                BizForkTask B = new BizForkTask(this.someDTO, aService1, aService2, bService, cService, 1);
                B.fork();
                BizForkTask C = new BizForkTask(this.someDTO, aService1, aService2, bService, cService, 2);
                C.fork();
                invokeAll(A,B,C);
                this.someDTO.setResultA(A.join());
                this.someDTO.setResultB(B.join().getResultB());
                this.someDTO.setResultC(C.join().getResultC());
                break;
        }
        return this.someDTO;
    }
}
