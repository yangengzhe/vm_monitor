package monitor.system.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import monitor.system.pojo.systemStatusPojo;

public interface systemStatusService extends Remote{
    public systemStatusPojo getRuntime(String ip) throws RemoteException;//内存 M 网络 kb/s
}
