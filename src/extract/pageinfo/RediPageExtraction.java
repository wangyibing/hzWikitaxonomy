package extract.pageinfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tools.uFunc;
import database.Entity;
import database.Zhwiki;
import de.tudarmstadt.ukp.wikipedia.api.Page;

public class RediPageExtraction {
	static String i = "RediPageExtraction";
	static String output = "";
	static int outNr = 0;
	static int PageId = 0;
	static String PageTitle = "";
	
	public static void ExtracRedirectPages(String outputPath, String infoPath)
	{
		int RediNr = 0;
		int PageNr = 0;
		String info = "";
		uFunc.deleteFile(infoPath);
		uFunc.deleteFile(outputPath);
		uFunc.AlertPath = infoPath;
		long time = System.currentTimeMillis();
		for(Page page : Zhwiki.getPages())
		{
			//page = Zhwiki.getPage(617541);
			PageNr ++;
			PageId = page.getPageId();
			PageTitle = Entity.getTitle(PageNr);
			if(PageId <= 0){
				uFunc.Alert(i, "pageid null:" + page.getText());
				continue;
			}
			if(PageNr % 10000 == 0)
			{
				info = "PageNumPast:" + PageNr + "\t" + "RediNr:" + RediNr + 
						"\tcost:" + (System.currentTimeMillis() - time)/1000
						+ "\n";
				time = System.currentTimeMillis();
				uFunc.addFile(info, infoPath);
				System.out.print(info);
				//break;
			}
			String text = page.getText();
			if(page.isRedirect() == true)
			{
				info = PageId + "\t" + "0" + "\t" + 
						PageTitle + "\t" + "null\n";
				output += info;
				System.out.print(info);
			}
			else if(page.getNumberOfOutlinks() == 0)
			{
				if(IsRedirectPage(text) == false)
				{
					if(ContainsRedirectPage(text) == true)
					{
						uFunc.Alert(i, PageId + " not standd redirect!!");
					}
					continue;
				}
				int matchNr = GetRedirectInfo(page.getText(), outputPath);
				RediNr += matchNr;
				if(matchNr != 1)
				{
					uFunc.Alert(i, PageId + " not standd redirect2!!match " + matchNr + " times");
				}
			}
			else if(page.getNumberOfOutlinks() == 1)
			{
				if(IsRedirectPage(text) == true)
				{
					int matchNr = GetRedirectInfo(page.getText(), outputPath);
					RediNr += matchNr;
					if(matchNr != 1)
					{
						uFunc.Alert(i, PageId + " not standd redirect3!!match " + matchNr + " times");
					}
				}
				else if(text.length() < 200 && 
						ContainsRedirectPage(text) == true)
				{
					uFunc.Alert(i, PageId + " not standd redirect3!!");
				}
			}
			else
			{
				if(IsRedirectPage(text) == true)
				{
					int matchNr = GetRedirectInfo(page.getText(), outputPath);
					RediNr += matchNr;
					if(matchNr != 1)
					{
						uFunc.Alert(i, PageId + " not standd context!!match " + matchNr + " times");
					}
				}
				// there are some cases that "#重定向" exist in a full page's context
				else if(text.length() < 200 && 
						ContainsRedirectPage(text) == true)
				{
					uFunc.Alert(i, PageId + ":redirect extract missed4!!");
				}
			}
		}
		uFunc.addFile(output, outputPath);
		uFunc.AlertClose();
	}

	static Pattern redirect = Pattern.compile("(#\\s*重定向)|(#\\s*redirect)");
	static Matcher matcher;
	private static boolean ContainsRedirectPage(String text) {
		// TODO Auto-generated method stub
		matcher = redirect.matcher(text.replaceAll(
				"(r|R)(e|E)(d|D)(i|I)(r|R)(e|E)(c|C)(t|T)", "redirect"));
		if(matcher.find())
			return true;
		return false;
	}

	static Pattern begin = Pattern.compile("(^#\\s*重定向)|(^#\\s*redirect)");
	static Matcher matcher2;
	private static boolean IsRedirectPage(String text) {
		// TODO Auto-generated method stub
		text = uFunc.ReplaceBoundSpace(text);
		matcher = begin.matcher(text.replaceAll(
				"((r|R)(e|E)(d|D)(i|I)(r|R)(e|E)(c|C)(t|T))", "redirect"));
		if(matcher.find())
			return true;
		return false;
	}

	public static int GetRedirectInfo(String text, String outputPath) {
		// TODO Auto-generated method stub
		text = uFunc.ReplaceBoundSpace(text);
		Pattern p = Pattern.compile("(#\\s*重定向\\s*:?\\s*(\\[\\[[^\\]]{1,}\\]\\]))|"
				+ "(#\\s*redirect\\s*:?\\s*(\\[\\[[^\\]]{1,}\\]\\]))");
		Matcher m = p.matcher(text.replaceAll(
				"(r|R)(e|E)(d|D)(i|I)(r|R)(e|E)(c|C)(t|T)", "redirect"));
		int matchNr = 0;
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
			if(targetId > 0){
				//System.out.println("GetRedirectInfo targetId: " + targetId + "\t" + s);
				if(targetId == lastId)
					continue;
				else{
					matchNr ++;
					lastId = targetId;
				}
				String info = PageId +"\t"+ targetId + "\t" + PageTitle + "\t"
						+ Entity.getTitle(targetId) + "\n";
				output += info;
				outNr ++;
				if(outNr % 1000 == 0){
					uFunc.addFile(output, outputPath);
					output = "";
				}
			}

		}
		return matchNr;
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
