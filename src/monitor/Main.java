package monitor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
    }
}
