package com.yao.rmi.client;

import com.yao.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by root on 15-2-13.
 */
public class ServiceConsumer {

    private static final Logger LOGGER= LoggerFactory.getLogger(ServiceConsumer.class);

    private static ServiceConsumer instance=null;
    private volatile List<String> urlList = new ArrayList<String>();
    private CuratorFramework client=null;

    private ServiceConsumer() {
        client=connectZookeeper();
        try {
            watchNode(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //connect zookeeper
    private CuratorFramework connectZookeeper(){
        CuratorFramework curatorFramework= CuratorFrameworkFactory.newClient(Constants.HOST_PORT,
                new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();
        return  curatorFramework;
    }

    // 查找 RMI 服务
    public <T extends Remote> T lookup() {
        T service = null;
        int size = urlList.size();
        if (size > 0) {
            String url;
            if (size == 1) {
                url = urlList.get(0); // 若 urlList 中只有一个元素，则直接获取该元素
                LOGGER.debug("using only url: {}", url);
            } else {
                url = urlList.get(ThreadLocalRandom.current().nextInt(size)); // 若 urlList 中存在多个元素，则随机获取一个元素
                LOGGER.debug("using random url: {}", url);
            }
            service = lookupService(url); // 从 JNDI 中查找 RMI 服务
        }
        return service;
    }

    //watch node
    private void watchNode(final CuratorFramework client) throws Exception {
        List<String>nodeList=client.getChildren().forPath(Constants.PROVIDER_NODE_PATH);
        List<String>dataList=new ArrayList<String>();
        for (String node :nodeList){
            byte[]data=client.getData().forPath(Constants.PROVIDER_NODE_PATH+"/"+node);
            dataList.add(new String(data));
        }
        urlList=dataList;
        client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                    try {
                        watchNode(client); // 若子节点有变化，则重新调用该方法（为了获取最新子节点中的数据）
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).forPath(Constants.PROVIDER_NODE_PATH);
    }
    // 在 JNDI 中查找 RMI 远程服务对象
    @SuppressWarnings("unchecked")
    private <T> T lookupService(String url) {
        T remote = null;
        try {
            remote = (T) Naming.lookup(url);
        } catch (Exception e) {
            if (e instanceof ConnectException) {
                // 若连接中断，则使用 urlList 中第一个 RMI 地址来查找（这是一种简单的重试方式，确保不会抛出异常）
                LOGGER.error("ConnectException -> url: {}", url);
                if (urlList.size() != 0) {
                    url = urlList.get(0);
                    return lookupService(url);
                }
            }
            LOGGER.error("", e);
        }
        return remote;
    }
    public static ServiceConsumer getInstance(){
        if(instance!=null){
            return  instance;
        }else{
            synchronized (ServiceConsumer.class){
                instance=new ServiceConsumer();
                return instance;
            }
        }
    }
}
