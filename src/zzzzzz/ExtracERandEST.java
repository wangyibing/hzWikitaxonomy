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
	public static void main(String [] args)
	{
		Extract("E:/Hanzhe/entityPairs");
	}

	private static int outNr = 0;
	public static void Extract(String sourceFile)
	{
		String oneLine = "";
		int pageid = 0;
		BufferedReader br = uFunc.getBufferedReader(sourceFile);
		connectToMysql();
		uFunc.deleteFile(sourceFile + ".out");
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
				String number = ss[2];
				if(number.contains("."))
					number = number.substring(0, number.indexOf("."));
				if(number.contains("-"))
					number = number.substring(0, number.indexOf("-"));
				int id = Integer.parseInt(number);
				try {
					Query.setInt(1, pageid);
					PageId = pageid;
					PageTitle = ss[0];
					Query.addBatch();
					ResultSet result = Query.executeQuery();
					if(result.next())
					{
						String cont = result.getString(1);
						PageAdj = "";
						RegexContent(cont, id);
						//System.out.println(PageAdj);
						if(PageAdj.equals("") == false)
						{
							output += oneLine + "\n" + PageAdj;
							outNr  ++;
							if(outNr % 100 == 0)
							{
								System.out.println(outNr);
								uFunc.addFile(output, sourceFile + ".out");
								output = "";
							}
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			uFunc.addFile(output, sourceFile + ".out");
			disconnectToMysql();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static Pattern p = Pattern.compile("( |\\.|-)(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)( |\\.|-)");
	static Pattern p2 = Pattern.compile("January|February|March|April|May|June|July"
			+ "|Augest|September|October|November|December".toLowerCase());
	static Pattern p3 = Pattern.compile("january|february|march|april|may|june|july"
			+ "|augest|september|october|november|december|over|"
			+ "number|chapter|other|however".toLowerCase());
	
	static String output = "";
	static String PageAdj = "";
	static int PageId;
	static String PageTitle;
	private static void RegexContent(String cont, int id) {
		// TODO Auto-generated method stub
		String cont2 = "";
		int level = 0;
		for(char c : cont.toCharArray())
		{
			if(c == '{' || c == '<' || c == '[')
				level ++;
			if(level == 0)
				cont2 = cont2 + c;
			if(c == '}' || c == '>' || c == ']')
				level --;
		}
		for(String sent : cont2.split("\n|\\.|\\?|!"))
		{
			Matcher matcher1 = p.matcher(sent);
			Matcher matcher2 = p2.matcher(sent);
			if(sent.contains(id + ""))
			{
				String [] ss = sent.split("\\.|\\,|\\:|\\!|\\s");
				//System.out.println(sent);
				String adj = "";
				for(int i = 0 ; i < ss.length ; i ++)
				{
					String word = ss[i];
					if(p3.matcher(word.toLowerCase()).find())
							continue;
					if(word.endsWith("er") || word.endsWith("est"))
						adj += word + ";";
					else if((word.equals("most") || word.equals("more") ||
							word.equals("less") || word.equals("least")) && i < ss.length - 1)
					{
						adj += word + " " + ss[i + 1] + ";";
					}
				}
				if(adj.equals("") == false)
				{
					PageAdj += PageId + "\t" + PageTitle + "\n" + 
							sent + "\n"  + "\t" + adj + "\n\n";
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
