## AQS独占功能 Demo解析 ## 
> 三个线程依次获取公平锁，其过程是
> A首先执行lock()方法,其实就是FairSync的lock方法
```
final void lock() {
    acquire(1);
}
```
> 其中acquire方法来自AQS, 方法作用：如果没有获取锁且没有加入阻塞队列则中断自身
```
public final void acquire(int arg) {
    if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```
> 其中tryAcquire()需要AQS的子类自己实现，ReentrantLock的实现是
```
//尝试获取锁
protected final boolean tryAcquire(int acquires) { 
    //入参是信号量 acquire=1
    //1.获取当前线程
    final Thread current = Thread.currentThread();
    //2.获取当前锁的同步状态
    int c = getState();
    //3.如果同步状态0代表没有线程获取锁
    if (c == 0) {
        //3.1 锁未被占用开始判断如果当前阻塞队列中没有其他线程就进行原子操作
        //将当前状态置为入参信号量即1，并且将锁的拥有者设置为当前线程，然后返回成功
        if (!hasQueuedPredecessors() &&
            compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
        //这一步阻塞队列里有其他线程（刚好被唤醒之类的意外）返回false
    }
    //4.如果同步状态不为0，有两种可能
    //4.1 可能1：当前线程已经是锁的拥有者，锁可重入，信号量++，返回成功
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    //4.2 可能2：当前线程不是锁拥有者返回失败
    return false;
}
```
> 可以看到，在ReentrantLock中，信号量/状态 一共有三种可能
- 0 锁可用
- 1 锁被占用
- 大于1 锁被占用且值表示锁的重入次数
> A 获取锁成功后，此时AQS中阻塞队列为空，然后B开始尝试获取锁
> B 一开始也会调用lock方法，tryAcquire肯定返回false. 此时B开始执行addWaiter方法，EXCLUSIVE代表独占
```
addWaiter(Node.EXCLUSIVE) 
```
> addWaiter方法本质就是将当前线程包装成阻塞队列的一个Node，添加到队尾
```
private Node addWaiter(Node mode) {
    //将当前线程包装为node
    Node node = new Node(Thread.currentThread(), mode);
    //先尝试一次将node直接置为队尾，如果这步成功下面就不用执行enq方法了
    Node pred = tail;
    if (pred != null) {
        //取队尾指针
        node.prev = pred;
        //将队尾指针进行原子操作置为当前线程node
        if (compareAndSetTail(pred, node)) {
            //成功后，将原队尾指针的下一个node置为当前线程node
            pred.next = node;
            return node;
        }
    }

    //尝试失败则通过自旋的方式将当前node置为队尾指针
    enq(node);
    return node;
}
```
```
//自旋无锁操作，防止多线程问题
private Node enq(final Node node) {
    //1.无限循环开始
    for (;;) {
        //2.取阻塞队列尾节点
        Node t = tail;
        
        //2.1 没有尾节点代表就初始化一个头空节点，并将这个节点同时也设置为尾，并进行下一轮
        if (t == null) { // Must initialize
            if (compareAndSetHead(new Node()))
                tail = head;
        } else {
            //2.2 第二轮把当前节点的前节点设置为上一轮的空节点
            node.prev = t;
            //2.3 设置当前节点为尾节点，并将上一轮节点的下一个节点设置为当前节点
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;
            }
        }
    }
}
```
> 此时B线程已经添加到阻塞队列的尾节点，开始执行acquireQueued方法，这是AQS的核心方法
```
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        //核心代码是无限循环，代表除非触发某些条件不然会一直阻塞，也就是死锁
        for (;;) {
            //1.取当前节点的前一个节点
            final Node p = node.predecessor();
            //2.前为头节点开始尝试获取锁
            if (p == head && tryAcquire(arg)) {
                //2.1 获取到锁后将当前节点置为头节点
                setHead(node);
                //2.2 原头节点脱离链表
                p.next = null; // help GC
                //2.3 结果改为成功，返回不打断标识
                failed = false;
                return interrupted;
            }
            //3.此时A占有锁，毫无疑问上一步失败了，此时就要开始判断是否要阻塞并继续等待
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```
```
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    int ws = pred.waitStatus;
    //取前节点的等待状态
    if (ws == Node.SIGNAL)
        //表示后续节点要被唤醒，则安心等待阻塞
        /*
         * This node has already set status asking a release
         * to signal it, so it can safely park.
         */
        return true;
    if (ws > 0) {
        //大于0 表示前节点被中断取消，需要被移除
        /*
         * Predecessor was cancelled. Skip over predecessors and
         * indicate retry.
         */
        do {
            //将队首的前节点置为队首，使原队首脱链
            node.prev = pred = pred.prev;
        } while (pred.waitStatus > 0);
        pred.next = node;
    } else {
        /*
         * waitStatus must be 0 or PROPAGATE.  Indicate that we
         * need a signal, but don't park yet.  Caller will need to
         * retry to make sure it cannot acquire before parking.
         */
        //其他情况，这里设置初始状态
        compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
    }
    return false;
}

```
> 判断线程阻塞，独占情况下只用到了三种信号量
- 1 canceled 取消
- -1 阻塞
- -2 条件队列专用，表示当前节点因为某个条件被阻塞

> 由此我们得出，阻塞队列中线程，如果要阻塞等待一定要确保将来有线程可以唤醒自己，AQS通过设置前节点
> 的信号量为-1即SIGNAL表示将来会唤醒该线程，则可以安心等待阻塞
> 补充：如果阻塞过程中被中断，是不会抛出异常的，只会在acquiredQueued方法返回值告诉使用者有没有被中断
> 线程B阻塞后，线程C一样跟B经历同样的过程然后被置为阻塞队列队尾
> 此时A开始释放锁，调用unlock方法，unlock内部调用的其实是AQS的release方法
```
public final boolean release(int arg) {
    //1.尝试释放资源
    if (tryRelease(arg)) {
        //释放成功，取头节点
        Node h = head;
        //头节点不为null而且等待状态不为0 = canceled
        if (h != null && h.waitStatus != 0)
            //唤醒头节点
            unparkSuccessor(h);
        return true;
    }
    //3.释放失败返回false
    return false;
}
```
```
//尝试释放锁
protected final boolean tryRelease(int releases) {
    //入参代表尝试释放的信号量 1 
    //用当前状态信号量 - 释放量 = 结果量
    int c = getState() - releases;
    //1.如果当前线程不是锁拥有者报错
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    boolean free = false;
    //2.如果当前已经被释放为无锁状态，则设置锁拥有者为null（安全）
    if (c == 0) {
        //无锁状态
        free = true;
        setExclusiveOwnerThread(null);
    }
    //3.排除其他可能后，设置结果信号量为当前状态信号量
    setState(c);
    //4.返回是否无锁，重入情况下此时可能还是有锁
    return free;
}
```
```
//唤醒当前节点的后继节点
private void unparkSuccessor(Node node) {
    /*
     * If status is negative (i.e., possibly needing signal) try
     * to clear in anticipation of signalling.  It is OK if this
     * fails or if status is changed by waiting thread.
     */
    int ws = node.waitStatus;
    //也是通过判断节点的等待状态
    if (ws < 0)
        //小于0 SINGLE 设置当前等待状态为0表示后续节点即将唤醒
        compareAndSetWaitStatus(node, ws, 0);

    /*
     * Thread to unpark is held in successor, which is normally
     * just the next node.  But if cancelled or apparently null,
     * traverse backwards from tail to find the actual
     * non-cancelled successor.
     */
    Node s = node.next;
    //这里条件判断的目的是，正常情况下直接唤醒后续节点，但是如果后续节点被取消了，也就是当前节点
    //等待状态大于0，则向后找一个未被cancel的节点并置为后续节点并唤醒
    if (s == null || s.waitStatus > 0) {
        s = null;
        for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
                s = t;
    }
    //这里是唤醒操作。直接使用LockSupport中的唤醒方法
    if (s != null)
        LockSupport.unpark(s.thread);
}
```
> 此时B被唤醒，B唤醒后首先从parkAndCheckInterrupt这个方法开始执行，判断阻塞期间有没有被中断，
> 然后经历一样release操作。直到C也释放后，阻塞队列中只剩下一个空的节点。

> 上面的情况是公平锁的情况，非公平的唯一区别是，公平下每次尝试获取锁首先会判断阻塞队列中是否有线程
> 在当前线程之前，这也是公平的含义，即先到先得。

> 上面获取锁都是执行了lock方法，线程会一直阻塞。而lockinterruptibly和nanos则会设定获取失败时中断或一定时间后中断
> 其原理就是，在acquireQueued中，只是用一个标识符代表中断状态，而他们则是抛出异常或阻塞一段时间后抛出异常

