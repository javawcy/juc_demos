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
public class ADTO implements Serializable {
    private static final long serialVersionUID = 4502253473195474139L;

    private String AResult1;
    private String AResult2;

    @Override
    public String toString() {
        return "ADTO{" +
                "AResult1='" + AResult1 + '\'' +
                ", AResult2='" + AResult2 + '\'' +
                '}';
    }

    public ADTO(String AResult1, String AResult2) {
        this.AResult1 = AResult1;
        this.AResult2 = AResult2;
    }

    public String getAResult1() {
        return AResult1;
    }

    public void setAResult1(String AResult1) {
        this.AResult1 = AResult1;
    }

    public String getAResult2() {
        return AResult2;
    }

    public void setAResult2(String AResult2) {
        this.AResult2 = AResult2;
    }
}
