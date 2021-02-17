package forkjoin.biz2;

import java.util.concurrent.RecursiveAction;

/**
 * <p>
 * Description
 * </P>
 *
 * @author Chongyu
 * @since 2021/2/17
 */
public abstract class AbstractDataLoader<T> extends RecursiveAction implements DataLoader<T> {

    private static final long serialVersionUID = 6453726025820052847L;
    protected T context;

    public AbstractDataLoader(T context) {
        this.context = context;
    }

    public void compute() {
        load(context);
    }

    public T getContext() {
        this.join();
        return context;
    }

    public void setContext(T context) {
        this.context = context;
    }
}
