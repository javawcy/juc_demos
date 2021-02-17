package forkjoin.biz;

import java.io.Serializable;

/**
 * <p>
 * Description
 * </P>
 *
 * @author Chongyu
 * @since 2021/2/17
 */
public class SomeDTO implements Serializable {
    private static final long serialVersionUID = 7659299956781484856L;

    private ADTO resultA;
    private String resultB;
    private String resultC;

    public SomeDTO() {
    }

    public SomeDTO(ADTO resultA) {
        this.resultA = resultA;
    }

    @Override
    public String toString() {
        return "SomeDTO{" +
                "resultA=" + resultA +
                ", resultB='" + resultB + '\'' +
                ", resultC='" + resultC + '\'' +
                '}';
    }

    public ADTO getResultA() {
        return resultA;
    }

    public void setResultA(ADTO resultA) {
        this.resultA = resultA;
    }

    public String getResultB() {
        return resultB;
    }

    public void setResultB(String resultB) {
        this.resultB = resultB;
    }

    public String getResultC() {
        return resultC;
    }

    public void setResultC(String resultC) {
        this.resultC = resultC;
    }
}
