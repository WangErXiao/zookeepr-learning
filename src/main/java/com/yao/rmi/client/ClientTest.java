package com.yao.rmi.client;

import com.yao.rmi.common.HelloService;

import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by root on 15-2-14.
 */
public class ClientTest {
    public static void main(String[]args) throws RemoteException, InterruptedException {
        CountDownLatch latch=new CountDownLatch(1);
        HelloService helloService=ServiceConsumer.getInstance().lookup();
        System.out.println(helloService.sayHello("yao"));
        latch.await();
    }
}
