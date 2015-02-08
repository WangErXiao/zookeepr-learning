package com.yao.leader;

import com.google.common.collect.Lists;
import com.yao.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 15-2-8.
 */
public class LeaderLatchAppCtx {
    private static final int CLIENT_QTY=10;
    public static void main(String[]args){
        List<CuratorFramework> clients= Lists.newArrayList();
        List<LeaderLatch> latches=Lists.newArrayList();
        try {
            for (int i = 0; i < CLIENT_QTY; ++i) {
                CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.HOST_PORT, new ExponentialBackoffRetry(1000, 3));
                clients.add(client);
                LeaderLatch latch = new LeaderLatch(client, Constants.LEAD_SPACE, "" + i);
                latches.add(latch);
                client.start();
                latch.start();
            }
            Thread.sleep(20000);
            LeaderLatch currentLeader = null;
            for (int i = 0; i < CLIENT_QTY; ++i) {
                LeaderLatch latch = latches.get(i);
                if (latch.hasLeadership()) {
                    currentLeader = latch;
                }
            }
            System.out.println("current leader is " + currentLeader.getId());
            System.out.println("release the leader " + currentLeader.getId());
            currentLeader.close();
            latches.get(0).await(2, TimeUnit.SECONDS);
            System.out.println("Client #0 maybe is elected as the leader or not although it want to be");
            System.out.println("the new leader is " + latches.get(0).getLeader().getId());
            System.out.println("Press key to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Shutting down...");
            for (LeaderLatch latch : latches) {
                try{
                    CloseableUtils.closeQuietly(latch);
                }catch (Exception e){
                }
            }
            for (CuratorFramework client : clients) {
                try {
                    CloseableUtils.closeQuietly(client);
                }catch (Exception e){
                }
            }
        }
    }
}
