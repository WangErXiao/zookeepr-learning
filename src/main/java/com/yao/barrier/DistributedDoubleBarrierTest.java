package com.yao.barrier;

import com.yao.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 15-2-9.
 */
public class DistributedDoubleBarrierTest {
    private static final int QTY=5;

    public static void main(String[]args) throws InterruptedException {
        CuratorFramework client= CuratorFrameworkFactory
                .newClient(Constants.HOST_PORT,new ExponentialBackoffRetry(1000,3));
        client.start();
        ExecutorService service= Executors.newFixedThreadPool(QTY);

        for (int i = 0; i < QTY; ++i) {
            final DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, Constants.BARRIER_PATH, QTY);
            final int index = i;
            Callable<Void> task = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Thread.sleep((long) (3 * Math.random()));
                    System.out.println("Client #" + index + " enters");
                    barrier.enter();
                    System.out.println("Client #" + index + " begins");
                    Thread.sleep((long) (3000 * Math.random()));
                    barrier.leave();
                    System.out.println("Client #" + index + " left");
                    return null;
                }
            };
            service.submit(task);
        }
        service.shutdown();
        service.awaitTermination(10, TimeUnit.MINUTES);
    }


}
