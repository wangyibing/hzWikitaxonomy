package extract.enwiki;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.uFunc;

public class EnwikiIdTitle {
	public static String info = "";
	public static void main(String [] args)
	{
		connectToMysql();
		String outputPath = "/home/hanzhe/Public/result_hz/enwiki/EnwikiIdTitle";
		uFunc.deleteFile(outputPath);
		String output = "";
		int outNr = 0;
		long t = System.currentTimeMillis();
		try {
			ResultSet result = Query.executeQuery();
			while(result.next())
			{
				int pageid = result.getInt(1);
				String title = result.getString(2);
				if(title == null)
					continue;
				output += pageid + "\t" + title + "\n";
				outNr ++;
				if(outNr % 1000 == 0)
				{
					uFunc.addFile(output, outputPath);
					output = "";
					if(outNr % 100000 == 0)
					{
						System.out.println(outNr + " pages passed, cost:" +
								(uFunc.GetTime(System.currentTimeMillis() - t)));
						t = System.currentTimeMillis();
					}
					
				}
			}
			uFunc.addFile(output, outputPath);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		disconnectToMysql();
	}
	

	private static Connection conn;
	static int batchSize = 1000 ;
	static PreparedStatement Query = null;
	private static void connectToMysql() {
		try{
            //调用Class.forName()方法加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            info = ("成功加载MySQL驱动！");
			uFunc.Alert(true, "", info);
        }catch(ClassNotFoundException e1){
            info = ("找不到MySQL驱动!");
			uFunc.Alert(true, "", info);
            e1.printStackTrace();
        }
		
		//JDBC的URL
        String url="jdbc:mysql://172.31.222.76:3306/enwiki"; 
        //String url="jdbc:mysql://localhost:3306/enwiki";    
        //调用DriverManager对象的getConnection()方法，获得一个Connection对象
        try {
            conn = DriverManager.getConnection(url,    "root","19920326");
            //创建一个Statement对象
            Query = conn.prepareStatement(
        			" select pageId, name from Page");
            Query.setFetchSize(Integer.MIN_VALUE);
            Query.setFetchDirection(ResultSet.FETCH_REVERSE);
//            info = ("成功连接到数据库！");
        } catch (SQLException e){
            e.printStackTrace();
        }
	}
	
	private static void disconnectToMysql() {
        try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
