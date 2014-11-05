package extract.pageinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.Entity;
import database.RediPage;
import database.Zhwiki;
import extract.web.ExtractAPI;
import tools.uFunc;

public class RedirectRepair {
	public static void main(String [] args)
	{
		Repair("data/pageinfo/RediPageExtractionInfo2", 
				RediPage.CanonicalPath);
	}

	/**
	 * RediPageExtraction:135915 not standd redirect!!
	 * RediPageExtraction:209574 not standd redirect2!!match 0 times
	 * RediPageExtraction:318599:redirect extract missed4!!
	 * @param infoPath
	 * @param RediFilePath
	 */
	public static void Repair(String infoPath, String RediFilePath)
	{
		RepairFromInfo(infoPath, RediFilePath);
		
	}

	public static void RepairFromInfo(String infoPath, 
			String rediFilePath) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(infoPath);
		String oneLine = "";
		Scanner sc = new Scanner(System.in);
		String output = "";
		Zhwiki.init();
		try {
			int fixNr = 0;
			while((oneLine = br.readLine()) != null)
			{
				int pageid = 0;
				int tarId = 0;
				String title = "";
				String tarTitle = "";
				if(oneLine.startsWith("RediPageExtraction:"))
				{
					int index = 19;
					while(index < oneLine.length() && 
							oneLine.charAt(index) >= '0' &&
							oneLine.charAt(index) <= '9')
						index ++;
					pageid = Integer.parseInt(oneLine.substring(19, index));
					System.out.println("pageid:" + pageid);
					tarId = sc.nextInt();
					System.out.println("tarid1:" + tarId);
					if(pageid > 0 && tarId > 0 )
					{
						fixNr ++;
						title = Entity.getTitles(pageid);
						tarTitle = Entity.getTitles(tarId);
						output += pageid +"\t"+ tarId + "\t" + title + "\t"
								+ tarTitle + "\n";
						continue;
					}
					//tarId = sc.nextInt();
					//System.out.println("tarid2:" + tarId);
					if(tarId == -1 || tarId > 0)
					{
						if(tarId == -1)
							tarId = GetRedirectInfo(Zhwiki.getPage(pageid).getText());
						if(tarId < 0)
						{
							tarId = sc.nextInt();
							System.out.println("tarid2:" + tarId);
						}
						if(tarId > 0 )
						{
							fixNr ++;
							title = Entity.getTitles(pageid);
							tarTitle = Entity.getTitles(tarId);
							output += pageid +"\t"+ tarId + "\t" + title + "\t"
									+ tarTitle + "\n";
						}
					}
				}
				else{
					System.out.println("error:" + oneLine);
				}
			}
			uFunc.addFile(output, rediFilePath);
			System.out.println("total fixed Nr of redirect Page:" + fixNr);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static int GetRedirectInfo(String text) {
		// TODO Auto-generated method stub
		text = uFunc.ReplaceBoundSpace(text);
		Pattern p = Pattern.compile("(#\\s*重定向\\s*:?\\s*(\\[\\[[^\\]]{1,}\\]\\]))|"
				+ "(#\\s*redirect\\s*:?\\s*(\\[\\[[^\\]]{1,}\\]\\]))");
		Matcher m = p.matcher(text.replaceAll(
				"(r|R)(e|E)(d|D)(i|I)(r|R)(e|E)(c|C)(t|T)", "redirect"));
		int lastId = 0;
		while(m.find()){
			String s = m.group(2);
			if(s == null)
			{
				s = m.group(4);
				//System.out.println(text);
			}
			s = GetTargetTitle(s);
			int targetId = Entity.getId(s);
			if(targetId == 0)
				targetId = ExtractAPI.GetPageId(s);
			if(targetId == 0)
				targetId = ExtractAPI.GetPageId(
						uFunc.PunctuationZh2En(s));
			if(targetId > 0){
				//System.out.println("GetRedirectInfo targetId: " + targetId + "\t" + s);
				if(targetId == lastId)
					continue;
				return targetId;
			}
		}
		return 0;
	}

	private static String GetTargetTitle(String title) {
		// TODO Auto-generated method stub
		if(title.contains("[") == true){
			// 去掉[[]] 标签
			if(title.matches("\\[\\[[^\\]]{1,}\\]\\]")){
				int index = 2;
				if(title.contains("|"))
					title = title.substring(index, title.indexOf("|"));
				else title = title.substring(index, title.length()-index);
				if(title.contains("#"))
					title = title.substring(0, title.indexOf("#"));
				title = uFunc.ReplaceBoundSpace(title);
				return title;
			}
			else{
				// 一定要包含[[]],否则用KPP表判断
				return null;
			}
		}
		else{
			return null;
		}
	}
}
