package monitor.sql;

import java.sql.ResultSet;

/**
 * 用于数据归档
 * @date 2016年9月3日 上午10:58:10
 * @author yangengzhe
 *
 */
public class cleanService {
    static String sql = null;  
    static DBHelper db1 = null;  
    static ResultSet ret = null;  
    
    public static void cleanVmStatus(String ip) {
        try {
            sql = "select id from csp_vmitem where IP='"+ip+"'";//SQL语句  
            db1 = new DBHelper(sql);//创建DBHelper对象  
            ret = db1.pst.executeQuery();//执行语句，得到结果集
            String vmid ="";
          //查找虚拟机
            while (ret.next()) {
                vmid = ret.getString(1); 
            }
            if(vmid == "") return;
/** 清理数据 **/
            //清理1小时之前的数据
            long delete_timestamp = System.currentTimeMillis() - 60*60*1000;
            sql ="delete from cmp_vmstatus where vmid = "+vmid+" AND timestamp<"+delete_timestamp;
            System.out.println(sql);
            db1 = new DBHelper(sql);//创建DBHelper对象  
            db1.pst.executeUpdate();//执行语句，得到结果集
            ret.close();  
            db1.close();//关闭连接  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    public static void cleanAppStatus(String ip,String webapp) {
        int id = 0;
        try {  
            System.out.println("本地IP:"+ip);
            
            sql = "select id from csp_vmitem where IP='"+ip+"'";//SQL语句  
            db1 = new DBHelper(sql);//创建DBHelper对象  
            ret = db1.pst.executeQuery();//执行语句，得到结果集
            String condition ="";
          //查找虚拟机
            while (ret.next()) {
                condition = "vm="+ret.getString(1); 
            }
            if(condition == "") return;
            sql = "select id,loadfilename from clp_cloudapp where "+condition;
            db1 = new DBHelper(sql);//创建DBHelper对象  
            ret = db1.pst.executeQuery();//执行语句，得到结果集
            while (ret.next()) {//查找webID
                String war_name = ret.getString(2);
                war_name = war_name.substring(0,war_name.lastIndexOf("."));
                war_name = war_name.substring(war_name.indexOf('-')+1);
                if(war_name.equals(webapp)){
                    id = Integer.parseInt(ret.getString(1));
                    break;
                }
            }
            if(id == 0) return;
/** 清理数据 **/
            //清理1小时之前的数据
            long delete_timestamp = System.currentTimeMillis() - 60*60*1000;
            sql ="delete from cmp_appstatus where appid = "+id+" AND timestamp<"+delete_timestamp;
            System.out.println(sql);            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
}
