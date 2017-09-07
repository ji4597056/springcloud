package com.spring.cloud.study.common;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Jeffrey
 * @since 2017/05/25 13:22
 */
public class TestCommand extends HystrixCommand {

//    protected TestCommand(HystrixCommandGroupKey group) {
//        super(group);
//    }

    public TestCommand(String name) {
        super(HystrixCommand.Setter.
            //设置GroupKey 用于dashboard 分组展示
                withGroupKey(HystrixCommandGroupKey.Factory.asKey("hello"))
            //设置commandKey 用户隔离线程池，不同的commandKey会使用不同的线程池
            .andCommandKey(HystrixCommandKey.Factory.asKey("hello" + name))
            //设置线程池名字的前缀，默认使用commandKey
            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("hello$Pool" + name))
            //设置线程池相关参数
            .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                .withCoreSize(15)
                .withMaxQueueSize(10)
                .withQueueSizeRejectionThreshold(2))
            //设置command相关参数
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                //是否开启熔断器机制
                .withCircuitBreakerEnabled(true)
                //舱壁隔离策略
                .withExecutionIsolationStrategy(
                    HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                //circuitBreaker打开后多久关闭
                .withCircuitBreakerSleepWindowInMilliseconds(5000)
                //超时时间设置
                .withExecutionTimeoutInMilliseconds(500)));
          //信号量隔离
//        withExecutionIsolationStrategy(
//            HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
//            //设置每组command可以申请的permit最大数
//            .withExecutionIsolationSemaphoreMaxConcurrentRequests(50)

}

    @Override
    protected String run() throws Exception {
        int time = new Random().nextInt(1000);
        System.out.println("====================");
        System.out.println("time:" + time);
        System.out.println("====================");
        Thread.sleep(time);
        //这里需要做实际调用逻辑
        return "Hello";
    }

    @Override
    protected Object getFallback() {
        return "ERROR";
    }

    public static void main(String[] args)
        throws InterruptedException, ExecutionException, TimeoutException, IOException {
        TestCommand command = new TestCommand("test");

        //1.这个是同步调用
//        command.execute();

        //2.这个是异步调用
//        command.queue().get(500, TimeUnit.MILLISECONDS);

        //3.异步回调
        command.observe().subscribe(result -> {
            System.out.println("====================");
            System.out.println("action:" + result);
            System.out.println("====================");
        });
        // 阻塞
        System.in.read();
    }
}
