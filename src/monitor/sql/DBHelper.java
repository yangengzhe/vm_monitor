package monitor.sql;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.SQLException;
import java.util.Properties;

import sun.management.counter.Variability;  
  
public class DBHelper {  
    public static final String name = "com.mysql.jdbc.Driver";  
    public static final String user = "root";  
    public static final String password = "root";  
  
    public Connection conn = null;  
    public PreparedStatement pst = null;  
  
    public DBHelper(String sql) {
        try {
            Class.forName(name);//指定连接类型
            String url = "jdbc:mysql://";//构造数据库地址
            try {//读配置文件
                InputStream in = new BufferedInputStream(new FileInputStream("config.properties"));   
                Properties p = new Properties();   
                p.load(in);  
                url= url + p.getProperty("database") +":3306/cloudplatform?useUnicode=true&amp;characterEncoding=UTF-8";
            } catch (IOException e) { 
                System.out.println("配置文件出错");
                return ;
            }
            conn = DriverManager.getConnection(url, user, password);//获取连接  
            pst = conn.prepareStatement(sql);//准备执行语句  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public void close() {  
        try {  
            this.conn.close();  
            this.pst.close();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
}  