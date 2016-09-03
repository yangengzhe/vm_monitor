package monitor.tomcat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import monitor.sql.sqlService;
import monitor.system.systemRuntimeStatus;

/**
 * appmonitor.jar
 * @author Administrator
 *
 */
public class newStatus {
    
    public static String getHtmlContext(String tempurl, String username, String password) throws IOException {
        URL url = null;
        BufferedReader breader = null;
        InputStream is = null;
        StringBuffer resultBuffer = new StringBuffer();
        try {
            url = new URL(tempurl);
            String userPassword = username + ":" + password;
            String encoding = new sun.misc.BASE64Encoder().encode (userPassword.getBytes());//在classpath中添加rt.jar包，在%java_home%/jre/lib/rt.jar

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Basic " + encoding);
            is = conn.getInputStream();
            breader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = breader.readLine()) != null) {
                resultBuffer.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            if(breader != null)
                breader.close();
            if(is != null)
                is.close();
        }
        return resultBuffer.toString();
    }
    
    
    public static String add(String s1,String s2){
        StringBuffer res=new StringBuffer();
        if(s1.contains("-")&&s2.contains("-")){
            res.append("-");
            s1=s1.replace("-", "");
            s2=s2.replace("-", "");
        }else if((!s1.contains("-")&&s2.contains("-"))){
            return sub(s1,s2.replace("-", ""));
        }else if((s1.contains("-")&&!s2.contains("-"))){
            return sub(s2,s1.replace("-", ""));
        }
        
        int len =Math.abs(s1.length()-s2.length());
        if(s1.length()>s2.length()){
            s2=castSame(s2,len);
        }else{
            s1=castSame(s1,len);
        }
        int n=0;
        for(int i=s1.length()-1;i>=0;i--){
            int temp = (s1.charAt(i)-'0'+s2.charAt(i)-'0'+n);
            if(i==0){
                res.append(temp%10).append(temp/10==0?"":temp/10);
            }else{
                res.append(temp%10);
                n=temp/10;
            }    
        }
        return kickZero(String.valueOf(res.reverse()));
        
    }
    public static String sub(String s1,String s2){
        boolean flag = false;
        StringBuffer res=new StringBuffer();
        if(s1.contains("-")&&s2.contains("-")){
            return sub(s2.replace("-", ""),s1.replace("-", ""));
        
        }else if((!s1.contains("-")&&s2.contains("-"))){
            return add(s1,s2.replace("-", ""));
        }else if((s1.contains("-")&&!s2.contains("-"))){
            return "-"+add(s2,s1.replace("-", ""));
        }
        
        if(!isGreater(s1,s2)){
            flag = true;
            String temp =s1;
            s1=s2;
            s2=temp;
        }
        s2 = castSame( s2,s1.length()-s2.length());
        int n=0;
        for(int i=s1.length()-1;i>=0;i--){
            Integer temp = s1.charAt(i)-'0'-s2.charAt(i)+'0'-n;
            
            temp = i==0?(temp==0?null:temp):temp;
            res.append(temp==null?"":temp>=0?temp:temp+10);
            
            n=temp==null?0:temp<0?1:0;
            
        }
        String r = kickZero(flag?String.valueOf(res.append("-").reverse()):String.valueOf(res.reverse()));  
        if(r.equals("")) return "0";
        return r;
    }
    public static boolean isGreater(String s1,String s2){//890  235
        
        if(s1.length()>s2.length()){
            return true;
        }else if(s1.length()<s2.length()){
            return false;
        }else if(s1.length()==s2.length()){
            int i=0;
            while(i<s1.length()){
                if(s1.charAt(i)<s2.charAt(i)){
                    return false;
                }else if(s1.charAt(i)>s2.charAt(i)){
                    return true;
                }
                i++;
            }
        }
        return true;
        
    }
    public static String castSame(String s,int len){
        StringBuffer sb =new StringBuffer();
        for(int i=0;i<len;i++){
            sb.append("0");
        }
        s=String.valueOf(sb.append(s));
        return s;    
    }
    
    public static String kickZero(String s){
        int i=0;
        while(s.length()>1){
            if('0'==s.charAt(0)){
                s=s.substring(1);
            }else{
                break;
            }
        }
        return s;
        
    }
}


