package com.yao.rmi.client;

import com.yao.rmi.common.HelloService;

import java.rmi.RemoteException;

/**
 * Created by root on 15-2-14.
 */
public class TestDynamic {
    public static void main(String[]args) throws RemoteException {
        HelloService helloService=ServiceConsumer.getInstance().lookup();
        System.out.println(helloService.sayHello("xxx"));
    }
}
