package com.yao.rmi.client;

import com.yao.rmi.common.HelloService;
import com.yao.rmi.server.HelloServiceImpl;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by root on 15-2-13.
 */
public class RmiClient {
    public static void main(String[]args) throws RemoteException, MalformedURLException, NotBoundException {
        String url = "rmi://localhost:1099/com.yao.rmi.server.HelloServiceImpl";
        long start=System.currentTimeMillis();
        HelloService helloService = (HelloService) Naming.lookup(url);
        String result = helloService.sayHello("Jack");
        System.out.println(result);
        System.out.println("time consumer:"+(System.currentTimeMillis()-start));
        start=System.currentTimeMillis();
        System.out.println(new HelloServiceImpl().sayHello("Jack"));
        System.out.println("time consumer:"+(System.currentTimeMillis()-start));
    }
}
