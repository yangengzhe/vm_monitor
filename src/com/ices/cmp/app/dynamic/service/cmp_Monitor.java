package com.ices.cmp.app.dynamic.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;


public interface cmp_Monitor extends Remote {
    public HashMap<String, String> getAppStatus(String webapp,String ip) throws RemoteException;
    public HashMap<String, String> getSystemStatus(String ip) throws RemoteException;
}
