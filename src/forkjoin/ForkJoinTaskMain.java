package forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * <p>
 * ForkJoin计算1w内的和
 * </P>
 *
 * @author Chongyu
 * @since 2021/2/17
 */
public class ForkJoinTaskMain {

    static final class SumTask extends RecursiveTask<Integer> {

        private static final long serialVersionUID = -9065397640120798742L;
        private final int start;
        private final int end;

        public SumTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            if (end - start < 1000) {
                System.out.println(Thread.currentThread().getName() + " 开始执行: " + start + "-" + end);
                int sum = 0;
                for (int i = start; i <= end; i++) {
                    sum += i;
                }
                return sum;
            }

            SumTask t1 = new SumTask(start, (start + end) / 2);
            SumTask t2 = new SumTask((start + end) / 2 + 1, end);
            invokeAll(t1,t2);
            return t2.join() + t1.join();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> forkJoinTask = new SumTask(1, 10000);
        forkJoinPool.submit(forkJoinTask);
        System.out.println(forkJoinTask.get());
    }
}
