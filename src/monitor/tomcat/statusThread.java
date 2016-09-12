package monitor.tomcat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import monitor.sql.cleanService;
import monitor.sql.sqlService;

/**
 * 
 * 监控状态的线程类
 * @date 2016年7月28日 上午9:18:50
 * @author yangengzhe
 *
 */
public class statusThread extends Thread{
    public String webapp = "probe";
    public String ip = "";
    public int timeout = 0;
    public statusThread(String webapp,String ip,int timeout){
        this.webapp = webapp;
        this.ip = ip;
        this.timeout = timeout;
    }
    @Override
    public void run() {
        try {
            String last_ResponseTime="0",last_Request="0",last_Error="0";
            Long ResponseTime=0l,Request=0l,Error=0l;
            String online_user="0";
            float perError;
            Long avg_ResponseTime;
            long timestamp;
            //吞吐率(个/分) Request; 平均响应时间(ms) avg_ResponseTime; 错误率 perError; 在线用户 online_user
            while(true){
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
                System.out.println("应用名"+webapp+"吞吐率"+Request+"响应时间"+avg_ResponseTime+"错误率"+perError+"在线用户"+online_user);
                sqlService.sendStatus(ip,webapp,Request, avg_ResponseTime, Integer.parseInt(online_user), System.currentTimeMillis(), perError);
                cleanService.cleanAppStatus(ip, webapp);
                while(System.currentTimeMillis() - timestamp <timeout*1000)
                    Thread.sleep(1000);
            }
        }
        catch(Exception e){
//            e.printStackTrace();
            System.out.println(this.getName()+" 进程停止");
        }
    }
    
}