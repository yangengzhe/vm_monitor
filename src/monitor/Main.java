package monitor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;
import java.util.Properties;

import com.ices.cmp.app.dynamic.service.cmp_Monitor;

import monitor.rpcServer.SMRMISocket;
import monitor.rpcServer.cmp_MonitorImpl;
import monitor.system.systemRuntimeStatus;
import monitor.tomcat.getAppStatus;

/**
 * srs.jar
 * @date 2016年7月28日 下午8:22:09
 * @author yangengzhe
 *
 */
public class Main {
    public static void main(String[] args) {
        String webapp_path = null,ip = null;
        int timeout=0;
        try {//读配置文件
            InputStream in = new BufferedInputStream(new FileInputStream("config.properties"));   
            Properties p = new Properties();   
            p.load(in);  
            webapp_path = p.getProperty("webapp_path");
            ip = p.getProperty("ip");
            timeout = Integer.parseInt(p.getProperty("timeout"));
            System.out.println(webapp_path);
            System.out.println(ip);
        } catch (IOException e) { 
            System.out.println("配置文件出错");
            return ;
        } 
        getAppStatus gThread = new getAppStatus(webapp_path,ip,timeout);
        gThread.start();
        
        systemRuntimeStatus sThread = new systemRuntimeStatus(ip,timeout);
        sThread.start();
        
        //创建一个rpc远程对象 
        try {
            int port = 8400;
            String bindName = "cmp_monitor";
            String ipadress = InetAddress.getLocalHost().getHostAddress();
            String bindUrl = "//"+ipadress+":"+ port +"/"+bindName;
            RMISocketFactory.setSocketFactory(new SMRMISocket());  //定义数据传输端口 8500
            LocateRegistry.createRegistry(port); //定义服务注册与查找服务端口 8400
            cmp_Monitor cmp_monitor = new cmp_MonitorImpl(); 
            Naming.rebind(bindUrl, cmp_monitor);
            System.out.println("启动RPC服务器:"+bindUrl);
        } catch (Exception e) { 
            System.out.println("创建远程对象发生异常！"+e.getMessage()); 
            e.printStackTrace(); 
        }        
    }
}
