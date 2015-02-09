package com.yao.lock;

import com.yao.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by root on 15-2-8.
 */
public class ReadWriteLockTest {
    public static void main(String[]args) throws Exception {
        //connect zookeeper
        CuratorFramework client = CuratorFrameworkFactory.newClient(Constants.HOST_PORT, new ExponentialBackoffRetry(1000, 3));
        client.start();
        ReadWriteLock readWriteLock=new ReadWriteLock(client,Constants.LOCK_PATH+"/res1");
        FakeLimitedResource fakeLimitedResource=new FakeLimitedResource();
        new ThreadWrite(readWriteLock,fakeLimitedResource).start();
        new ThreadRead(readWriteLock,fakeLimitedResource).start();
        new ThreadWrite(readWriteLock,fakeLimitedResource).start();
        new ThreadWrite(readWriteLock,fakeLimitedResource).start();
        new ThreadRead(readWriteLock,fakeLimitedResource).start();
    }
    private static class ThreadWrite extends Thread{
        private ReadWriteLock readWriteLock;
        private FakeLimitedResource resource;
        private ThreadWrite(ReadWriteLock readWriteLock,FakeLimitedResource resource) {
            this.readWriteLock = readWriteLock;
            this.resource = resource;
        }
        @Override
        public void run() {
            try {
                readWriteLock.acquireWriteLock();
                System.out.println("get write lock------:"+Thread.currentThread());
                resource.use();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    readWriteLock.releaseWriteLock();
                    System.out.println("release write lock-------"+Thread.currentThread());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static class ThreadRead extends Thread{
        private ReadWriteLock readWriteLock;
        private FakeLimitedResource resource;
        private ThreadRead(ReadWriteLock readWriteLock,FakeLimitedResource resource) {
            this.readWriteLock = readWriteLock;
            this.resource = resource;
        }
        @Override
        public void run() {
            try {
                readWriteLock.acquireWriteLock();
                System.out.println("get read lock------:"+Thread.currentThread());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    readWriteLock.releaseWriteLock();
                    System.out.println("release read lock-------:"+Thread.currentThread());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
