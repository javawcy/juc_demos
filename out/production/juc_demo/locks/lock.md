## LOCK 接口 ##

### 加锁 ###

- lock() 类似synchronized关键字，如果锁不可用，出于线程调度的目的，线程将休眠到获得锁 
- lockInterruptibly() 于lock()唯一区别是锁不可用则立即中断所以抛出中断异常
- tryLock() 尝试获取锁，可以在参数中增加等待时间，期间线程不会中断
- unlock() 释放锁

### Condition接口 ###

- await() 执行await方法后，当前线程立即释放锁并进入唤醒等待区等待其他线程将其唤醒或中断，可以
在参数中加入等待唤醒的时间。
- awaitUninterruptibly() 等待超时不会被中断会一直等待。
- awaitUntil(),awaitUntilNanos() 设置等待时间或截止日期。
- signal() 唤醒等待线程。
- signalAll() 唤醒所有线程。

### ReadWriteLock ### 

- 概念 读写锁，是一对相关的锁——读锁和写锁，读锁用于只读操作，写锁用于写入操作。
读锁可以由多个线程同时保持，而写锁是独占的，只能由一个线程获取。


## LOCK接口实现 ##


### ReentrantLock ###

- 概念 ReentrantLock类，实现了Lock接口，是一种可重入的独占锁，它具有与使用 synchronized 相同的一些基本行为和语义，但功能更强大。
ReentrantLock内部通过内部类实现了AQS框架(AbstractQueuedSynchronizer)的API来实现独占锁的功能。

- 构造器，构造方法中通过指定 boolean类型参数 fair来指定锁是公平还是非公平策略。默认非公平。
一般情况下，使用公平策略的程序在多线程访问时，总体吞吐量（即速度很慢，常常极其慢）比较低，因为此时在线程调度上面的开销比较大。

    - 公平策略：在多个线程争用锁的情况下，公平策略倾向于将访问权授予等待时间最长的线程。也就是说，相当于有一个线程等待队列，先进入等待队列的线程后续会先获得锁，这样按照“先来后到”的原则，对于每一个等待线程都是公平的。
    - 非公平策略：在多个线程争用锁的情况下，能够最终获得锁的线程是随机的（由底层OS调度）。
    
### ReentrantReadWriteLock ###

- 概念 读写锁，它是ReadWriteLock接口的直接实现，该类在内部实现了具体独占锁特点的写锁，以及具有共享锁特点的读锁。
和ReentrantLock一样，ReentrantReadWriteLock类也是通过定义内部类实现AQS框架的API来实现独占/共享的功能。

- 支持锁重入和锁降级，但是写锁的重入和降级建立在获取写锁的前提下。多线程下可以获取多个读锁，但写锁只有一个线程拥有。
- 读写锁的condition只有写锁可以获取，读锁不需要condition


## LockSupport ##

- 概念 LockSupport类，是JUC包中的一个工具类，是用来创建锁和其他同步类的基本线程阻塞原语。
可以把这种锁看成是ReentrantLock的公平锁简单版本，且是不可重入的，就是说当一个线程获得锁后，其它等待线程以FIFO的调度方式等待获取锁。

- park() 阻塞当前线程
- unpark() 唤醒指定线程


## AQS ##

- 概念 AbstractQueuedSynchronizer抽象类（以下简称AQS）是整个java.util.concurrent包的核心。在JDK1.5时，Doug Lea引入了J.U.C包，该包中的大多数同步器都是基于AQS来构建的。AQS框架提供了一套通用的机制来管理同步状态（synchronization state）、阻塞/唤醒线程、管理等待队列。
     
     我们所熟知的ReentrantLock、CountDownLatch、CyclicBarrier等同步器，其实都是通过内部类实现了AQS框架暴露的API，以此实现各类同步器功能。这些同步器的主要区别其实就是对同步状态（synchronization state）的定义不同。
     
     AQS框架，分离了构建同步器时的一系列关注点，它的所有操作都围绕着资源——同步状态（synchronization state）来展开，并替用户解决了如下问题：
     
     - 资源是可以被同时访问？还是在同一时间只能被一个线程访问？（共享/独占功能）
     - 访问资源的线程如何进行并发管理？（等待队列）
     - 如果线程等不及资源了，如何从等待队列退出？（超时/中断）
- 这其实是一种典型的模板方法设计模式：父类（AQS框架）定义好骨架和内部操作细节，具体规则由子类去实现。
  AQS框架将剩下的一个问题留给用户：什么是资源？如何定义资源是否可以被访问？
  
- 由于并发的存在，需要考虑的情况非常多，因此能否以一种相对简单的方法来完成这两个目标就非常重要，因为对于用户（AQS框架的使用者来说），很多时候并不关心内部复杂的细节。而AQS其实就是利用模板方法模式来实现这一点，AQS中大多数方法都是final或是private的，也就是说Doug Lea并不希望用户直接使用这些方法，而是只覆写部分模板规定的方法。
     - tryAcquire 排他获取
     - tryRelease 拍他释放
     - tryAcquireShared 共享获取
     - tryReleaseShared 共享释放
     - isHeldExclusively 是否排他
     
- 使用了AQS框架的同步器，都支持下面的操作：

    - 阻塞和非阻塞（例如tryLock）同步
    - 可选的超时设置，让调用者可以放弃等待
    - 可中断的阻塞操作
    
- 使用了AQS的同步器同样都支持condition,Condition接口，可以看做是Obejct类的wait()、notify()、notifyAll()方法的替代品，与Lock配合使用。
AQS框架内部通过一个内部类ConditionObject，实现了Condition接口，以此来为子类提供条件等待的功能


- CAS操作，即CompareAndSet,通过Unsafe类实现对字段的原子操作。
    
    - CompareAndSetStatus 修改同步状态
    - CompareAndSetHead 修改头指针
    - CompareAndSetTail 修改尾指针
    - CompareAndSetWaitStatus 修改等待状态
    - CompareAndSetNext 修改结点的next指针

- CAS 等待队列操作
    
    - enq 入队
    - addWaiter 入队
    - setHead 设置头结点
    - unparkSuccessor 唤醒后继结点
    - doReleaseShared 释放共享结点
    - setHeadAndPropagate 设置头结点并传播唤醒
    
- CAS 资源获取操作

    - cancelAcquire 取消获取资源
    - shouldParkAfterFailedAcquire 判断是否阻塞当前调用线程
    - acquireQueued 尝试获取资源，获取失败则尝试阻塞线程
    - doAcquireInterruptibly 独占资源，失败中断
    - doAcquireNanos 独占资源，限时等待
    - doAcquireShared 共享的获取资源
    - doAcquireSharedInterruptibly 共享资源，失败中断
    - doAcquireSharedNanos 共享资源，限时等待
    - acquire 独占资源
    - acquireShared 共享获取
    - release 释放独占资源
    - releaseShared 释放共享资源
    
- CAS 核心
    - 同步状态（synchronization state）的管理，
      同步状态，其实就是资源。AQS使用单个int（32位）来保存同步状态，并暴露出getState、setState以及compareAndSetState操作来读取和更新这个状态。
    - 阻塞/唤醒线程的操作，在JDK1.5之前，除了内置的监视器机制外，没有其它方法可以安全且便捷得阻塞和唤醒当前线程。
      JDK1.5以后，java.util.concurrent.locks包提供了LockSupport类来作为线程阻塞和唤醒的工具。
    - 线程等待队列的管理，等待队列，是AQS框架的核心，整个框架的关键其实就是如何在并发状态下管理被阻塞的线程。
      等待队列是严格的FIFO队列，是Craig，Landin和Hagersten锁（CLH锁）的一种变种，采用双向链表实现，因此也叫CLH队列。
    
    


