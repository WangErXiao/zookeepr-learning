package com.yao.barrier;

import com.yao.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 15-2-9.
 */
public class DistributedBarrierTest {
    private static final int QTY=5;

    public static void main(String[]args) throws Exception {
        CuratorFramework client= CuratorFrameworkFactory
                .newClient(Constants.HOST_PORT, new ExponentialBackoffRetry(1000, 3));
        client.start();
        ExecutorService service= Executors.newFixedThreadPool(QTY);
        DistributedBarrier controlBarrier=new DistributedBarrier(client,Constants.BARRIER_PATH);
        controlBarrier.setBarrier();


        for(int i=0;i<QTY;++i){
            final  DistributedBarrier barrier=new DistributedBarrier(client,Constants.BARRIER_PATH);
            final  int index=i;
            Callable<Void> task=new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Thread.sleep((long) (3 * Math.random()));
                    System.out.println("Client #" + index + " waits on Barrier");
                    barrier.waitOnBarrier();
                    System.out.println("Client #" + index + " begins");
                    return null;
                }
            };
            service.submit(task);
        }
        Thread.sleep(10000);
        System.out.println("all Barrier instances should wait the condition");
        controlBarrier.removeBarrier();
        service.shutdown();
        service.awaitTermination(10, TimeUnit.MINUTES);
    }
}
