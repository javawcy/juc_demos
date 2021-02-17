package forkjoin.biz2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

/**
 * <p>
 * Description
 * </P>
 *
 * @author Chongyu
 * @since 2021/2/17
 */
public class DefaultForkJoinDataLoader<T> extends AbstractDataLoader<T> {

    private static final long serialVersionUID = -2556400792951936755L;
    private List<AbstractDataLoader<T>> taskList;

    public DefaultForkJoinDataLoader(T context) {
        super(context);
        this.taskList = new ArrayList<>();
    }

    public void addTask(DataLoader<T> dataLoader) {
        taskList.add(new AbstractDataLoader<T>(this.context) {
            private static final long serialVersionUID = -5510016414514881449L;
            @Override
            public void load(T context) {
                dataLoader.load(context);
            }
        });
    }

    @Override
    public void load(T context) {
        this.taskList.forEach(ForkJoinTask::fork);
    }

    @Override
    public T getContext() {
        this.taskList.forEach(ForkJoinTask::join);
        return this.context;
    }
}
