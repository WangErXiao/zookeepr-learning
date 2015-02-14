package com.yao.rmi.server;

import com.yao.rmi.common.HelloService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by root on 15-2-14.
 */
public class HelloServiceImpl1 extends UnicastRemoteObject implements HelloService {
    public HelloServiceImpl1() throws RemoteException {
    }

    @Override
    public String sayHello(String name) throws RemoteException {
        return "test test ---"+name;
    }
}
