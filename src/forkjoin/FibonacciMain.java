package forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * <p>
 * Description
 * </P>
 *
 * @author Chongyu
 * @since 2021/2/17
 */
public class FibonacciMain {

    static final class Fibonacci extends RecursiveTask<Integer> {

        private static final long serialVersionUID = -7962617470207267079L;
        private final int n;

        public Fibonacci(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n <= 1) {
                return n;
            }
            Fibonacci f1 = new Fibonacci(n - 1);
            Fibonacci f2 = new Fibonacci(n - 2);
            invokeAll(f1,f2);
            return f1.join() + f2.join();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(4);
        ForkJoinTask<Integer> forkJoinTask = new Fibonacci(20);
        Integer result = pool.invoke(forkJoinTask);
        System.out.println(result);
    }
}
