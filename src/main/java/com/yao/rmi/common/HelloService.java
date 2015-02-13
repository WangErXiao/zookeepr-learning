package com.yao.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by root on 15-2-13.
 */
public interface HelloService extends Remote {

    String sayHello(String name) throws RemoteException;
}
