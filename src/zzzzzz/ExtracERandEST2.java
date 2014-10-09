package zzzzzz;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.uFunc;

/**
 * extract bigger and biggest in enwiki
 * @author hanzhe
 *
 */
public class ExtracERandEST2 {
	static String info = "";
	public static void main(String [] args)
	{
		String folder = "/home/hanzhe/Public/result_hz/Xser/";
		uFunc.AlertPath = folder + "MappingPairstmp2" + ".info";
		uFunc.deleteFile(uFunc.AlertPath);
		/*
		uFunc.deleteFile(folder + "MappingPairs.tmp");
		uFunc.deleteFile(folder + "MappingPairs.tmp2");
		int recordNr = 0;
		recordNr = QsortByEntityId(folder + "MappingPairs", folder + "MappingPairs.tmp");
		QsortPair.SortPair(folder + "MappingPairs.tmp", folder + "MappingPairs.tmp2", false,
				true, recordNr + 100);
				*/
		int totalLineNr = uFunc.GetLineNr(folder + "MappingPairstmp2");
		uFunc.Alert(true, "totalLineNr", totalLineNr + "");
		Extract(folder + "MappingPairstmp2");
		uFunc.AlertClose();
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
					info = (oneLine);
					uFunc.Alert(true, "", info);
					continue;
				}
				int pageid = Integer.parseInt(ss[3]);
				if(pageid == 12){
					info = (oneLine);
					uFunc.Alert(true, "", info);
				}
				String info = ss[0] + "####" + ss[1] + "####" + ss[2];
				output += pageid + "\t" + info + "\n";
				Nr ++;
				if(Nr % 1000 == 0)
				{
					uFunc.addFile(output, pathTmp);
					output = "";
				}
			}
			info = ("data format end, begin qsort!");
			uFunc.Alert(true, "", info);
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
		String[] lastCont = null;
		long startTime = System.currentTimeMillis();
		long t1 = System.currentTimeMillis();
		try {
			while( (oneLine = br.readLine()) != null)
			{
				lineNr ++;
				String [] ss = oneLine.split("\t");
				if(ss.length != 2)
				{
					info = (oneLine);
					uFunc.Alert(true, "", info);
					continue;
				}
				pageid = Integer.parseInt(ss[0]);
				if(ss[1].split("####").length < 3){
					uFunc.Alert(true, "triple not correct", oneLine);
					continue;
				}
				String predicate = ss[1].split("####")[1];
				PageTitle = ss[1].split("####")[0];
				String objects = ss[1].split("####")[2];
				/*
				if(pageid < 8222)
					continue;
				System.out.println(pageid);*/
				if(pageid == lastId)
				{
					//info = (pageid + "\t" + lastId);
					PageAdj = "";
					RegexContent(lastCont, PageTitle, objects, 
							predicate, pageid);
				}
				else{
					//info = (pageid + "\t" + lastId);
					try {
						Query.setInt(1, pageid);
						PageId = pageid;
						PageTitle = ss[0];
						Query.addBatch();
						ResultSet result = Query.executeQuery();
						if(result.next())
						{
							String cont = result.getString(1);
							lastCont = UnifiedContext(cont, pageid);
							PageAdj = "";
							RegexContent(lastCont, PageTitle, objects, 
									predicate, pageid);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
				if(PageAdj.equals("") == false)
				{
					output += PageAdj;
					outNr  ++;
					if(outNr % 100 == 0){
						uFunc.addFile(output, sourceFile + ".out");
						output = "";
					}
					if(outNr % 1000 == 0)
					{
						info = "lineNr:" + lineNr + "\t" + "outNr:" + outNr + 
								"\t cost:" + uFunc.GetTime(System.currentTimeMillis() - t1) +
								" pageid:" + pageid;
						t1 = System.currentTimeMillis();
						uFunc.Alert(true, "", info);
					}
				}
				lastId = pageid;
			}
			
			uFunc.addFile(output, sourceFile + ".out");
			info = "totalLineNr:" + lineNr + "\t" + "outNr:" + outNr + 
					"\ntotalCost:" + uFunc.GetTime(System.currentTimeMillis() - startTime);
			uFunc.Alert(true, "", info);
			disconnectToMysql();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String[] UnifiedContext(String cont, int pageid) {
		// TODO Auto-generated method stub
		if(cont == null ||cont.equals(""))
			return null;
		String cont2 = "";
		int level = 0;
		int rLevel = 0;
		String entity = "";
		//System.out.println(cont.length() + "\n" + cont);
		cont = RemoveNote(cont, pageid);
		for(char c : cont.toCharArray())
		{
			if(c == '{'  || c == '[')
			{
				level ++;
				if(c == '{')
					rLevel ++;
				else if(c == '[')
					entity = "";
			}
			if(level == 0)
				cont2 = cont2 + c;
			if(rLevel == 0 && (c != '[' ) && (c != ']'))
				entity += c;
			if(c == '}'  || c == ']')
			{
				level --;
				if(c == '}' )
					rLevel --;
				else if(c == ']' && rLevel == 0)
				{
					if(entity.toLowerCase().startsWith("file:"))
						entity = "";
					if(entity.contains("|"))
						entity = entity.substring(0, entity.indexOf("|"));
					cont2 += entity;
					entity = "";
				}
			}
		}
		/*
		Pattern p = Pattern.compile("(<!--)((.|\n)*?)(-->)");
		Matcher m = p.matcher(cont2);
		while(m.find())
			System.out.println(m.group());
		*/
		cont2 = cont2.replaceAll("====.+?====", "").replaceAll("===.+?===", "")
				.replaceAll("==.+?==", "").replaceAll("<ref.*?/ref>", "")
				.replaceAll("<ref [^<]{1,}/>", "");
		//System.out.println(2);
		cont2 = cont2.replaceAll("'", "").replaceAll("<br */ *>", "\n")
				.replaceAll("http(s?):.+?( |\n)", "")
				.replaceAll("Category:.+\n", "\n")
				.replaceAll("\\?", "\\?\n")
				;
		cont2 = cont2.replaceAll("\\. ", "\\.\n").replaceAll("!", "!\n");
		// correct wrong split
		String cont3 = "";
		char[] chars = cont2.toCharArray();
		for(int i = 0 ; i < chars.length - 1; i ++)
		{
			if(chars[i] == '\n')
			{
				if(chars[i+1] >= 'a' && chars[i+1] <= 'z')
					cont3 += " ";
				else cont3 += "\n";
			}
			else cont3 += chars[i];
		}
		if(chars.length > 0)
			cont3 += chars[chars.length - 1];
		if(cont3 == null || cont3.equals(""))
			return null;
		/*
		for(int i = 0 ; i < lastCont.length; i ++)
		{
			lastCont[i] = lastCont[i].replaceAll("^( |\\*|\\-)+", "");
			System.out.println(lastCont[i]);
		}*/
		//System.out.println(cont + "\n#########################\n" + cont2);
		return cont3.replaceAll("(\n)+", "\n").split("\n");
	}

	private static String RemoveNote(String cont, int pageid) {
		// TODO Auto-generated method stub
		String result = "";
		int beg = 0;
		int end = 0;
		while(true){
			beg = cont.indexOf("<!--", end);
			if(beg < 0)
				break;
			if(beg > end)
				result += cont.substring(end, beg);
			end = cont.indexOf("-->", beg);
			if(end < 0)
				break;
			//System.out.println(cont.substring(beg, end + 3));
		}
		if(end < 0)
			uFunc.Alert(true, pageid + "", " note not match");
		else if(end < cont.length())
			result += cont.substring(end);
		return result;
		
	}

	static String output = "";
	static String PageAdj = "";
	static int PageId;
	static String PageTitle;
	private static void RegexContent(String[] sents, String subjects,
			String object, String predicate, int pageid) {
		// TODO Auto-generated method stub
		if(sents == null){
			uFunc.Alert(true, pageid + "", "sents is empty");
			return;
		}
		String[] subs = subjects.split(",");
		for(String sent : sents)
			for(String sub : subs){
				if(sent.toLowerCase().contains(sub.toLowerCase()) &&
						sent.toLowerCase().contains((object.toLowerCase())))
				{
					PageAdj += PageId + "\t" + subjects + "\t" + predicate + "\t" +
							object + "\n" + sent + "\n\n";
					break;
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
            info = ("成功加载MySQL驱动！");
			uFunc.Alert(true, "", info);
        }catch(ClassNotFoundException e1){
            info = ("找不到MySQL驱动!");
			uFunc.Alert(true, "", info);
            e1.printStackTrace();
        }
		
		//JDBC的URL
        String url="jdbc:mysql://172.31.222.76:3306/enwiki";    
        //调用DriverManager对象的getConnection()方法，获得一个Connection对象
        try {
            conn = DriverManager.getConnection(url,    "root","19920326");
            //创建一个Statement对象
            Query = conn.prepareStatement(
        			" select text from Page where pageId = ? ");
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
