package com.yao.leader;

import com.google.common.collect.Lists;
import com.yao.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by root on 15-2-8.
 */
public class LeaderSelectorAppCxt {
    private static final  int CLIENT_QTY=10;

    public static void main(String[]args) throws IOException {
        List<CuratorFramework>clients= Lists.newArrayList();
        List<LeaderSelectorClient> selectorClients=Lists.newArrayList();
        try {
            for (int i = 0; i < CLIENT_QTY; ++i) {
                /*CuratorFramework client = CuratorFrameworkFactory
                        .builder().connectString(Constants.HOST_PORT)
                        .namespace(Constants.LEAD_SPACE)
                        .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                        .connectionTimeoutMs(5000).build();*/
                CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.HOST_PORT, new ExponentialBackoffRetry(1000, 3));
                clients.add(client);
                LeaderSelectorClient selectorClient =
                        new LeaderSelectorClient(client, Constants.LEAD_SPACE, "" + i);
                selectorClients.add(selectorClient);
                client.start();
                selectorClient.start();
            }
            System.out.println("Press any key to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        }finally {
            System.out.println("Shutting down ...");
            for (LeaderSelectorClient leaderSelector:selectorClients){
                CloseableUtils.closeQuietly(leaderSelector);
            }
            for (CuratorFramework client:clients){
                CloseableUtils.closeQuietly(client);
            }
        }
    }
}
