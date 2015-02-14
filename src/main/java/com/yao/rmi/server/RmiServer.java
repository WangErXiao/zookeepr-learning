package com.yao.rmi.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Created by root on 15-2-13.
 */
public class RmiServer {
    public static void main(String[]args) throws RemoteException, MalformedURLException {
        int port = 1099;
        String url = "rmi://127.0.0.1:1099/com.yao.rmi.server.HelloServiceImpl";
        LocateRegistry.createRegistry(port);
        Naming.rebind(url, new HelloServiceImpl());
    }
}
