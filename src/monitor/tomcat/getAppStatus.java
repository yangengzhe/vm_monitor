package monitor.tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获得列表并检测
 * @date 2016年7月28日 上午9:19:15
 * @author yangengzhe
 *
 */
public class getAppStatus extends Thread{
    String Webapp_path="d:/";
    String ip = "";
    int timeout = 0;
    ArrayList<String> sysApp=new ArrayList<String>();
    public getAppStatus(String Webapp_path,String ip,int timeout){
        this.Webapp_path = Webapp_path;
        this.ip = ip;
        this.timeout = timeout;
        sysApp.add("docs");
        sysApp.add("examples");
        sysApp.add("host-manager");
        sysApp.add("manager");
        sysApp.add("probe");
        sysApp.add("ROOT");
    }
    @Override
    public void run() {
        ArrayList<String> apps;
        HashMap<String,Thread> threads = new HashMap<String,Thread>();
        while(true){
            apps = getList();
            for (String app : apps) {
                if(threads.containsKey(app) && threads.get(app)!=null && threads.get(app).getName().equals(app)){
                    continue;
                }
                statusThread st = new statusThread(app,ip,timeout);
                st.setName(app);
                st.start();
                threads.put(app,st);
                System.out.println("监控应用："+app);
            }
            try {
                Thread.sleep(timeout*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public ArrayList<String> getList(){
        ArrayList<String> app = new ArrayList<String>();
        File file=new File(Webapp_path);
        File[] tempList = file.listFiles();
        app.clear();
        for (int i = 0; i < tempList.length; i++) {
         if (tempList[i].isDirectory() && !sysApp.contains(tempList[i].getName())) {
          app.add(""+tempList[i].getName());
         }
        }
        return app;
    }
    
}