package monitor.rpcServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ices.cmp.app.dynamic.service.cmp_Monitor;

import monitor.sql.cleanService;
import monitor.sql.sqlService;
import monitor.tomcat.newStatus;



public class cmp_MonitorImpl extends UnicastRemoteObject implements Remote, cmp_Monitor {

    public cmp_MonitorImpl() throws RemoteException{
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public HashMap<String, String> getAppStatus(String webapp, String ip) throws RemoteException {
        System.out.println("rpc服务调用：APP状态获取"+webapp);
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            String last_ResponseTime="0",last_Request="0",last_Error="0";
            Long ResponseTime=0l,Request=0l,Error=0l;
            String online_user="0";
            float perError;
            Long avg_ResponseTime;
            long timestamp;
            //吞吐率(个/分) Request; 平均响应时间(ms) avg_ResponseTime; 错误率 perError; 在线用户 online_user

                
                
                //1秒时间 检测1秒的系统情况。（最短间隔1秒）
                for(int q=0;q<2;q++){
                    timestamp = System.currentTimeMillis();
                    String str = newStatus.getHtmlContext("http://127.0.0.1:8080/probe/appprocdetails.ajax?webapp=/"+webapp,"admin","123456");
                    str = str.replaceAll("&nbsp;", "");
                    str = str.replaceAll("\\s*", "");
                    str = str.replaceAll("<[^>]+>", "");
                    Matcher m =Pattern.compile("[^0-9:]").matcher(str);
                    str = m.replaceAll("").trim();
                    String[] strs = str.split(":");
                  //处理时间
                    ResponseTime = Long.valueOf(newStatus.sub(strs[1], last_ResponseTime));
                    last_ResponseTime = strs[1];
                    
                    str = newStatus.getHtmlContext("http://127.0.0.1:8080/probe/appreqdetails.ajax?webapp=/"+webapp,"admin","123456");
                    str = str.replaceAll("&nbsp;", "");
                    str = str.replaceAll("\\s*", "");
                    str = str.replaceAll("<[^>]+>", "");
                    m =Pattern.compile("[^0-9:]").matcher(str);
                    str = m.replaceAll("").trim();
                    strs = str.split(":");
                  //请求数(吞吐率 个/分)
                    Request = Long.valueOf(newStatus.sub(strs[1], last_Request));
                    last_Request = strs[1];
                  //错误数
                    Error = Long.valueOf(newStatus.sub(strs[2], last_Error));
                    last_Error = strs[2];
                    
                    str = newStatus.getHtmlContext("http://127.0.0.1:8080/probe/appruntimeinfo.ajax?webapp=/"+webapp,"admin","123456");
                    str = str.replaceAll("&nbsp;", "");
                    str = str.replaceAll("\\s*", "");
                    str = str.substring(str.indexOf("Datasourceusage")+15);
                    str = str.replaceAll("<[^>]+>", "");
                    str = str.substring(0, str.indexOf("yes"));
                    //在线人数
                    online_user = str;
                    //平均响应时间
                    avg_ResponseTime = (Request == 0?0:(ResponseTime / Request));
                    //错误率
                    perError = (Request == 0?0:(Error*100 / Request));
                    
                  //发送到服务器
                    map.put("webapp", webapp);
                    map.put("ip", ip);
                    map.put("request", Request.toString());//请求数
                    map.put("response", avg_ResponseTime.toString());//响应时间
                    map.put("error", String.valueOf(perError));//错误率
                    map.put("online", online_user);//在线人数
                    map.put("time", String.valueOf(System.currentTimeMillis()));
                    
                    while(q==0 && (System.currentTimeMillis() - timestamp <1000))
                        Thread.sleep(100);
                }
                
        }
        catch(Exception e){
//            e.printStackTrace();
            System.out.println("rpc服务 停止");
        }
        return map;
    }

    @Override
    public HashMap<String, String> getSystemStatus(String ip) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

}
