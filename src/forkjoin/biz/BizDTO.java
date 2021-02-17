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
public class BizDTO implements Serializable {
    private static final long serialVersionUID = 7202572151372065939L;

    private int req;

    public int getReq() {
        return req;
    }

    public void setReq(int req) {
        this.req = req;
    }
}
