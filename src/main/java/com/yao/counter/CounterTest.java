package com.yao.counter;

import com.google.common.collect.Lists;
import com.yao.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 15-2-9.
 */
public class CounterTest implements SharedCountListener{

    private static final int QTY = 5;

    public static void main(String[]args) throws Exception {
        CounterTest counterTest=new CounterTest();

        final Random rand = new Random();

        CuratorFramework client= CuratorFrameworkFactory
                .newClient(Constants.HOST_PORT,new ExponentialBackoffRetry(1000,3));
        client.start();

        SharedCount baseCount=new SharedCount(client,Constants.COUNT_PATH,0);

        baseCount.addListener(counterTest);

        baseCount.start();

        List<SharedCount> examples = Lists.newArrayList();
        ExecutorService service = Executors.newFixedThreadPool(QTY);
        for (int i = 0; i < QTY; ++i) {
            final SharedCount count = new SharedCount(client, Constants.COUNT_PATH, 0);
            examples.add(count);
            Callable<Void> task = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    count.start();
                    for(int i=0;i<100;i++){
                        Thread.sleep(rand.nextInt(1000));
                        long start=System.currentTimeMillis();
                        System.out.println("Increment:" + count.trySetCount(count.getVersionedValue(), count.getCount() + rand.nextInt(10)));
                        System.out.println("time consumer:"+(System.currentTimeMillis()-start));
                    }
                    return null;
                }
            };
            service.submit(task);
        }
        //service.shutdown();
        service.awaitTermination(100, TimeUnit.MINUTES);
        for (int i = 0; i < QTY; ++i) {
            examples.get(i).close();
        }
        baseCount.close();
    }

    @Override
    public void countHasChanged(SharedCountReader sharedCountReader, int i) throws Exception {
        System.out.println("Counter's value is changed to " + i);
    }

    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        System.out.println("State changed: " + curatorFramework.toString());
    }

}
