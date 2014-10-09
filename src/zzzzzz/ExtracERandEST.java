package zzzzzz;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.QsortPair;
import tools.uFunc;

/**
 * extract bigger and biggest in enwiki
 * @author hanzhe
 *
 */
public class ExtracERandEST {
	public static void main(String [] args)
	{
		String folder = "/home/hanzhe/Public/result_hz/Xser/";
		uFunc.deleteFile(folder + "MappingPairs.tmp");
		uFunc.deleteFile(folder + "MappingPairs.tmp2");
		int recordNr = 0;
		recordNr = QsortByEntityId(folder + "MappingPairs", folder + "MappingPairs.tmp");
		QsortPair.SortPair(folder + "MappingPairs.tmp", folder + "MappingPairs.tmp2", false,
				true, recordNr + 100);
		Extract(folder + "MappingPairs.tmp2");
	}

	private static int QsortByEntityId(String path, String pathTmp) {
		// TODO Auto-generated method stub
		String oneLine = "";
		BufferedReader br = uFunc.getBufferedReader(path);
		int Nr = 0;
		try {
			while((oneLine = br.readLine()) != null)
			{
				String[] ss = oneLine.split("\t");
				if(ss.length != 4)
				{
					System.out.println(oneLine);
					continue;
				}
				int pageid = Integer.parseInt(ss[3]);
				if(pageid == 12)
					System.out.println(oneLine);
				String info = ss[0] + "####" + ss[1] + "####" + ss[2];
				output += pageid + "\t" + info + "\n";
				Nr ++;
				if(Nr % 1000 == 0)
				{
					uFunc.addFile(output, pathTmp);
					output = "";
				}
			}
			System.out.println("data format end, begin qsort!");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		uFunc.addFile(output, pathTmp);
		return Nr;
	}

	private static int outNr = 0;
	public static void Extract(String sourceFile)
	{
		String oneLine = "";
		int pageid = 0;
		BufferedReader br = uFunc.getBufferedReader(sourceFile);
		connectToMysql();
		uFunc.deleteFile(sourceFile + ".out");
		int lineNr = 0;
		int lastId = 0;
		String lastCont = "";
		try {
			while( (oneLine = br.readLine()) != null)
			{
				lineNr ++;
				String [] ss = oneLine.split("\t");
				if(ss.length != 2)
				{
					System.out.println(oneLine);
					continue;
				}
				pageid = Integer.parseInt(ss[0]);
				String [] sss = ss[1].split("####");
				if(pageid == lastId)
				{
					PageAdj = "";
					String cont = lastCont;
					RegexContent(cont, sss[0], sss[2], oneLine);
				}
				else{
					try {
						Query.setInt(1, pageid);
						PageId = pageid;
						PageTitle = ss[0];
						Query.addBatch();
						ResultSet result = Query.executeQuery();
						if(result.next())
						{
							String cont = result.getString(1);
							lastCont = cont;
							PageAdj = "";
							RegexContent(cont, sss[0], sss[2], oneLine);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(PageAdj.equals("") == false)
				{
					output += PageAdj;
					outNr  ++;
					if(outNr % 100 == 0)
					{
						String info = "lineNr:" + lineNr + "\t" + "outNr:" + outNr;
						System.out.println(info);
						uFunc.addFile(output, sourceFile + ".out");
						output = "";
					}
				}
				lastId = pageid;
			}
			
			uFunc.addFile(output, sourceFile + ".out");
			disconnectToMysql();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String output = "";
	static String PageAdj = "";
	static int PageId;
	static String PageTitle;
	private static void RegexContent(String cont, String subject,
			String object, String oneline) {
		// TODO Auto-generated method stub
		for(String sent : cont.split("\n|\\.|\\?|!"))
		{
			if(sent.toLowerCase().contains(subject.toLowerCase()) &&
					sent.toLowerCase().contains((object.toLowerCase())))
			{
				PageAdj += PageId + "\t" + PageTitle + "\n" + oneline + "\n"
						+ sent + "\n\n";
			}	
		}
	}

	private static Connection conn;
	static int batchSize = 1000 ;
	static PreparedStatement Query = null;
	private static void connectToMysql() {
		try{
            //调用Class.forName()方法加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("成功加载MySQL驱动！");
        }catch(ClassNotFoundException e1){
            System.out.println("找不到MySQL驱动!");
            e1.printStackTrace();
        }
		
		//JDBC的URL
        String url="jdbc:mysql://localhost:3306/enwiki";    
        //调用DriverManager对象的getConnection()方法，获得一个Connection对象
        try {
            conn = DriverManager.getConnection(url,    "root","19920326");
            //创建一个Statement对象
            Query = conn.prepareStatement(
        			" select text from Page where pageId = ? ");
            System.out.println("成功连接到数据库！");
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
