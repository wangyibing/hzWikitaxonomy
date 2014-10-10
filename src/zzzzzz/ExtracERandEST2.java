package zzzzzz;

import java.io.BufferedReader;
import java.io.File;
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
public class ExtracERandEST2 {
	static String info = "";
	public static void main(String [] args)
	{
		//String folder = "E:/hanzhe/";
		//String textFileFolder = "E:/Hanzhe/result_hz/Xser/context/";
		String folder = "/home/hanzhe/Public/result_hz/Xser/";
		String textFileFolder = "/home/hanzhe/Public/result_hz/Xser/context/";
		uFunc.AlertPath = folder + "MappingPairs.tmp2" + ".info";
		uFunc.deleteFile(uFunc.AlertPath);
		/*
		uFunc.deleteFile(folder + "MappingPairs.tmp");
		uFunc.deleteFile(folder + "MappingPairs.tmp2");
		int recordNr = 0;
		recordNr = QsortByEntityId(folder + "MappingPairs", folder + "MappingPairs.tmp");
		QsortPair.SortPair(folder + "MappingPairs.tmp", folder + "MappingPairs.tmp2", false,
				true, recordNr + 100);
		int totalLineNr = uFunc.GetLineNr(folder + "MappingPairs.tmp2");
		//totalLineNr:5872467
		uFunc.Alert(true, "totalLineNr", totalLineNr + "");
				*/
		Extract(folder + "MappingPairs.tmp2", textFileFolder, folder);
		uFunc.AlertClose();
	}

	public static int QsortByEntityId(String path, String pathTmp) {
		
		
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
	public static void Extract(String sourceFile, String textFileFolder, String folder)
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
		long to1 = 0;
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
				PageId = pageid;
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
					if(lastCont == null || lastCont.equals(""))
						continue;
					RegexContent(lastCont, PageTitle, objects, 
							predicate, pageid);
				}
				else{
					//info = (pageid + "\t" + lastId);
					try {
						long t2 = System.currentTimeMillis();
						Query.setInt(1, pageid);
						Query.addBatch();
						ResultSet result = Query.executeQuery();
						if(result.next())
						{
							String cont = result.getString(1);
							to1 += System.currentTimeMillis() - t2;
							lastCont = UnifiedContext(cont, pageid, folder);
							
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
						//System.out.println(to1 + "\t" + toComp + "\t" + toUnif + "\t" + toUnif2 + "\t" + toUnif3);
					}
				}
				lastId = pageid;
				//break;
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

	private static void SaveFile(int pageid2, String pageTitle2,
			String[] lastCont, String textFileFolder) {
		// TODO Auto-generated method stub
		if(lastCont == null)
			return;
		pageTitle2 = pageTitle2.replaceAll(
				"\\\\|\\?|\\!|\\/|\\<|\\>|\\:|\\*|\\|", "_");
		String fileName = pageid2 + "_" + pageTitle2;
		File file= new File(textFileFolder + fileName);
		if(file.exists())
			return;
		else uFunc.addFile("", file.getAbsolutePath());
	}

	static long toUnif = 0;
	static long toUnif2 = 0;
	static long toUnif3 = 0;
	static Pattern entityPt = Pattern.compile("\\[\\[[^\\]]{1,}\\]\\]"); 
	private static String[] UnifiedContext(String cont, int pageid, String folder) {
		// TODO Auto-generated method stub
		long t1 = System.currentTimeMillis();
		if(cont == null ||cont.equals(""))
			return null;
		String cont2 = "";
		//System.out.println(cont.length() + "\n" + cont);
		//uFunc.addFile(cont + "\n\n\n\n\n\n\n\n", folder + "tmp");
		cont = RemoveTag(cont, pageid, "<!--", "-->", 3);

		int level = 0;
		StringBuffer sb = new StringBuffer();
		char [] chars = cont.toCharArray();
		for(int i = 0 ; i < cont.length() - 5; i ++)
		{
			if(level == 0 && chars[i] == '<' && chars[i+1] == 'r'
					&& chars[i+2] == 'e' && chars[i+3] == 'f'){
				int end = i + 4;
				level ++;
				while(true)
				{
					if(end >= cont.length())
						break;
					else if(end < cont.length() - 1 && chars[end] == '/' 
							&& chars[end+1] == '>')
					{
						level --;
						if(level == 0)
							break;
						//else System.out.println("s:" + level);
					}
					else if(end < cont.length() - 3 && chars[end] == '<'
							&& chars[end+1] == 'r' && chars[end+2] == 'e' 
							&& chars[end+3] == 'f'){
						level ++;
						//System.out.println("A:" + level);
						//System.out.println("\t" + cont.substring(i, end+3));
					}
					else if(end < cont.length() - 5 && chars[end] == '<'
							&& chars[end+1] == '/' && chars[end+2] == 'r'
							&& chars[end+3] == 'e' && chars[end+4] == 'f'
							&& chars[end+5] == '>')
					{
						level --;
						if(level == 0)
							break;
						//else System.out.println(level);
					}
					end ++;
				}
				if(end < cont.length() && level == 0){
					//int tmp = i;
					if(chars[end] == '/')
						i = end + 1;
					else if(chars[end] == '<')
						i = end + 5;
					//System.out.println(cont.substring(tmp, i+1) + "\n**********************");
				}
			}
			else sb.append(chars[i]);
		}
		cont2 = sb.toString();
		/*
		Pattern pt = Pattern.compile("<ref(.+?)</ref>");
		Matcher mt = pt.matcher(cont);
		while(mt.find())
			System.out.println(mt.group());*/
		toUnif += System.currentTimeMillis() - t1;
		//****** Remove templates begin ******
		level = 0;
		sb = new StringBuffer();
		StringBuffer temp = new StringBuffer();
		for(char c : cont.toCharArray())
		{
			if(c == '{')
				level ++;
			if(level == 0)
				sb.append(c);
			else temp.append(c);
			if(c == '}'){
				level --;
				if(level == 0){
					//System.out.println(temp);
					temp = new StringBuffer();
				}
			}
		}
		cont2 = sb.toString();
		//******  Remove templates end  ******
		
		//******Unified entity tag begin*******
		cont2 = cont2.replaceAll("\\$", "\\\\\\$");
		Matcher m = entityPt.matcher(cont2);
		sb = new StringBuffer();
		while(m.find()){
			String entity = m.group();
			if(entity.contains("|"))
				entity = entity.substring(2, entity.indexOf("|"));
			else entity = entity.substring(2, entity.length() - 2);
			if(entity.toLowerCase().startsWith("file:"))
				entity = "";
			m.appendReplacement(sb, entity);
		}
		m.appendTail(sb);
		String tmp = sb.toString();
		tmp = tmp.replaceAll("\\\\\\$", "\\$");
		//****** Unified entity tag end *******
		toUnif2 += System.currentTimeMillis() - t1;
		/*
		if(cont2.matches("\\s*") == false &&
				cont2.equals(tmp) == true)
			System.out.println("tmp:\n" + tmp + "\ncont2:" + cont2);
			*/
		cont2 = tmp;
		cont2 = cont2.replaceAll("====.+?====", "").replaceAll("===.+?===", "")
				.replaceAll("==.+?==", "").replaceAll("\\'", "")
				.replaceAll("<br/>", "\n");
		cont2 = cont2.replaceAll("\\. ", "\\.\n").replaceAll("!", "!\n")
				.replaceAll("\\?", "\\?\n");
		// correct wrong split
		sb = new StringBuffer();
		chars = cont2.toCharArray();
		for(int i = 0 ; i < chars.length - 1; i ++)
		{
			if(chars[i] == '\n')
			{
				if(chars[i+1] >= 'a' && chars[i+1] <= 'z')
					sb.append(' ');
				else sb.append('\n');
			}
			else sb.append(chars[i]);
		}
		toUnif3 += System.currentTimeMillis() - t1;
		if(chars.length > 0)
			sb.append(chars[chars.length - 1]);
		cont2 = sb.toString();
		if(cont2 == null || cont2.equals(""))
		{
			uFunc.Alert(true, pageid + "", "sents is empty");
			return null;
		}
		/*
		for(int i = 0 ; i < lastCont.length; i ++)
		{
			lastCont[i] = lastCont[i].replaceAll("^( |\\*|\\-)+", "");
			System.out.println(lastCont[i]);
		}*/
		//System.out.println(cont + "\n#########################\n" + cont2);
		return cont2.replaceAll("(\n)+", "\n").split("\n");
	}

	private static String RemoveTag(String cont, int pageid,
			String begRegex, String endRegex, int endSize) {
		Pattern notePt = Pattern.compile(begRegex);
		int end = 0;
		Matcher m = notePt.matcher(cont);
		StringBuffer sb = new StringBuffer();
		while(m.find())
		{
			int start = m.start();
			String gap = cont.substring(end, start);
			if(gap != null){
				sb.append(gap);
			}
			end = cont.indexOf(endRegex) + endSize;
			if(end < 0)
				break;
		}
		if(end < cont.length())
		{
			String gap = cont.substring(end);
			if(gap != null)
				sb.append(gap);
		}
		return sb.toString();
		
	}

	static String output = "";
	static String PageAdj = "";
	static int PageId;
	static String PageTitle;
	static long toComp = 0;
	private static void RegexContent(String[] sents, String subject,
			String objects, String predicate, int pageid) {
		// TODO Auto-generated method stub
		long t1 = System.currentTimeMillis();
		if(sents == null || sents.length == 0){
			return;
		}
		String[] objs = objects.split(",");
		for(String sent : sents)
			for(String obj : objs){
				if(sent.toLowerCase().contains(obj.toLowerCase()))
				{
					PageAdj += PageId + "\t" + subject + "\t" + predicate + "\t" +
							obj + "\n" + sent + "\n\n";
					break;
				}
			}
		toComp += System.currentTimeMillis() - t1;
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
