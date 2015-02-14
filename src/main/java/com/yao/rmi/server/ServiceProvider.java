package com.yao.rmi.server;

import com.yao.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 15-2-13.
 */
public class ServiceProvider {
    private static final Logger LOGGER= LoggerFactory.getLogger(ServiceProvider.class);
    private static ServiceProvider provider;
    //发布服务
    public void publish(Remote remote,String host,int port){
       CuratorFramework zookeeperClient=null;
       PersistentEphemeralNode node=null;
       try{
           String url=publishService(remote,host,port);
           if(url!=null){
                zookeeperClient=connectZookeeper();
                node=createNode(zookeeperClient,url,remote.getClass().getName());
           }
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           /*if (zookeeperClient!=null){
               CloseableUtils.closeQuietly(zookeeperClient);
           }
           if (node!=null){
               CloseableUtils.closeQuietly(node);
           }*/
       }
    }

    private String publishService(Remote remote, String host, int port) throws RemoteException, MalformedURLException {
        String url = String.format("rmi://%s:%d/%s",host,port,remote.getClass().getName());
        LocateRegistry.createRegistry(port);
        Naming.rebind(url, new HelloServiceImpl());
        return url;
    }

    //connect zookeeper
    private CuratorFramework connectZookeeper(){
        CuratorFramework curatorFramework= CuratorFrameworkFactory.newClient(Constants.HOST_PORT,
                new ExponentialBackoffRetry(1000,3));
        curatorFramework.start();
        LOGGER.debug("create zookeepr client curatorFramework:{}", curatorFramework.hashCode());
        return  curatorFramework;
    }
    //create zNode
    private PersistentEphemeralNode createNode(CuratorFramework client,String url,String name) throws Exception {
        byte[]dates=url.getBytes();
        PersistentEphemeralNode node=new PersistentEphemeralNode(client, PersistentEphemeralNode.Mode.EPHEMERAL,
                Constants.PROVIDER_NODE_PATH+"/"+name, dates);
        node.start();

        node.waitForInitialCreate(3, TimeUnit.SECONDS);
        LOGGER.debug("create znode  hashCode {}",node.hashCode());
        return node;
    }
    public static ServiceProvider getInstance(){
        if(provider!=null){
            return provider;
        }else{
            synchronized (ServiceProvider.class){
                return new ServiceProvider();
            }
        }
    }

    private ServiceProvider(){

    }
    //test
    public static void main(String[]args){
        try {
             ServiceProvider.getInstance().publish(new HelloServiceImpl(), "127.0.0.1", 1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
