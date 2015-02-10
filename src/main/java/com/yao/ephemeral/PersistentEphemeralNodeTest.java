package com.yao.ephemeral;

import com.yao.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.KillSession;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by root on 15-2-10.
 */
public class PersistentEphemeralNodeTest {
    public static void main(String[]args){
        CuratorFramework client=null;
        PersistentEphemeralNode node=null;
        try {
            client= CuratorFrameworkFactory.newClient(Constants.HOST_PORT,new ExponentialBackoffRetry(1000,3));
            client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                @Override
                public void stateChanged(CuratorFramework client, ConnectionState newState) {
                    System.out.println("client state:" + newState.name());
                }
            });
            client.start();
            node = new PersistentEphemeralNode(client, PersistentEphemeralNode.Mode.EPHEMERAL,Constants.EPHEMERAL_PATH, "test".getBytes());
            node.start();
            node.waitForInitialCreate(3, TimeUnit.SECONDS);
            String actualPath = node.getActualPath();
            System.out.println("node " + actualPath + " value: " + new String(client.getData().forPath(actualPath)));
            client.create().forPath(Constants.NODE_PATH, "persistent node".getBytes());
            System.out.println("node " + Constants.NODE_PATH + " value: " + new String(client.getData().forPath(Constants.NODE_PATH)));
            KillSession.kill(client.getZookeeperClient().getZooKeeper(), Constants.HOST_PORT);
            System.out.println("node " + actualPath + " doesn't exist: " + (client.checkExists().forPath(actualPath) == null));
            System.out.println("node " + Constants.NODE_PATH + " value: " + new String(client.getData().forPath(Constants.NODE_PATH)));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(node);
            CloseableUtils.closeQuietly(client);
        }

    }
}
