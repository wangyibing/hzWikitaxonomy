package zzzzzz;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tools.uFunc;

/**
 * extract bigger and biggest in enwiki
 * @author hanzhe
 *
 */
public class ExtracERandEST {

	private static int outNr = 0;
	public static void Extract(String sourceFile)
	{
		String oneLine = "";
		int pageid = 0;
		BufferedReader br = uFunc.getBufferedReader(sourceFile);
		try {
			while( (oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 4)
				{
					System.out.println(oneLine);
					continue;
				}
				pageid = Integer.parseInt(ss[3]);
				try {
					Query.setInt(1, pageid);
					Query.addBatch();
					ResultSet result = Query.executeQuery();
					String cont = result.getString("text");
					PageAdj = "";
					RegexContent(cont);
					if(PageAdj.equals("") == false)
					{
						output += pageid + "\t" + PageAdj + "\n";
						outNr  ++;
						if(outNr % 100 == 0)
						{
							uFunc.addFile(output, sourceFile + ".out");
							output = "";
						}
						uFunc.addFile(output, sourceFile + ".out");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String number = ss[2];
				
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static Pattern p = Pattern.compile("( |\\.|-)(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)( |\\.|-)");
	static Pattern p2 = Pattern.compile("January|February|March|April|May|June|July"
			+ "|Augest|September|October|November|December");
	
	static String output = "";
	static String PageAdj = "";
	private static void RegexContent(String cont) {
		// TODO Auto-generated method stub
		int level = 0;
		cont = cont.replaceAll("\\{\\{[^\\{^\\}]{1,}\\}\\}", "")
				.replaceAll("\\{\\{[^\\{^\\}]{1,}\\}\\}", "");
		for(String sent : cont.split("\n|\\.|\\?|!|"))
		{
			Matcher matcher1 = p.matcher(sent);
			Matcher matcher2 = p2.matcher(sent);
			if(matcher1.find() || matcher2.find() || uFunc.containNumber(sent))
			{
				String [] ss = sent.split("\\.|\\,|\\:|\\!|\\s");
				for(int i = 0 ; i < ss.length ; i ++)
				{
					String word = ss[i];
					if(word.endsWith("er") || word.endsWith("est"))
						PageAdj += word + ";";
					else if((word.equals("most") || word.equals("more") ||
							word.equals("less") || word.equals("least")) && i < ss.length - 1)
					{
						PageAdj += word + " " + ss[i + 1] + ";";
					}
				}
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
        String url="jdbc:mysql://localhost:3306/wikipedia";    
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
