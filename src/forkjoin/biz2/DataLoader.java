package forkjoin.biz2;

/**
 * <p>
 * Description
 * </P>
 *
 * @author Chongyu
 * @since 2021/2/17
 */
public interface DataLoader<T> {

    void load(T context);
}
