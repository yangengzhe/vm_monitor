package monitor.system;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

import monitor.sql.cleanService;
import monitor.sql.sqlService;
import monitor.system.pojo.systemStatusPojo;
import monitor.system.service.systemStatusService;
import monitor.system.service.systemStatusServiceImpl;

public class systemRuntimeStatus extends Thread{
    public String ip;
    int timeout;
    public systemRuntimeStatus(String ip,int timeout){
        this.ip = ip;
        this.timeout = timeout;
    }
    @Override
    public void run() {
        while(true){
            try {
                systemStatusService sss = new systemStatusServiceImpl();
                systemStatusPojo ssp = sss.getRuntime(ip);
                sqlService.sendVmStatus(ip, (float)ssp.getCpu_combined(), ssp.getMemory_uesd(), ssp.getTx_speed(), ssp.getRx_speed(), System.currentTimeMillis());
                cleanService.cleanVmStatus(ip);
                Thread.sleep(timeout*1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }


}
