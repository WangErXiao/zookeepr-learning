package com.yao.rmi.server;

import java.rmi.RemoteException;

/**
 * Created by root on 15-2-14.
 */
public class TestDynamic {
    public static void main(String[]args) throws RemoteException {
        ServiceProvider.getInstance().publish(new HelloServiceImpl1(),"127.0.0.1",2099);
    }
}
