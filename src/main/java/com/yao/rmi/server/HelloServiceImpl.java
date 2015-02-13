package com.yao.rmi.server;

import com.yao.rmi.common.HelloService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by root on 15-2-13.
 */
public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {
    public HelloServiceImpl() throws RemoteException {

    }

    @Override
    public String sayHello(String name) throws RemoteException {

        return String.format("Hello %s",name);
    }
}

