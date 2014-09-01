package extract.pageinfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tools.uFunc;
import database.Zhwiki;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

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
		Zhwiki.init();
		for(Page page : Zhwiki.wiki.getPages())
		{
			try {
				page = Zhwiki.wiki.getPage(3407054);
			} catch (WikiApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PageNr ++;
			PageId = page.getPageId();
			PageTitle = Zhwiki.getTitle(PageId);
			if(PageId <= 0){
				uFunc.Alert(i, "pageid null:" + page.getText());
				continue;
			}
			if(PageNr % 10000 == 0)
			{
				info = "PageNumPast:" + PageNr + "\t" + "RediNr:" + RediNr + "\n";
				uFunc.addFile(info, infoPath);
				System.out.print(info);
			}
			System.out.println(page.getNumberOfOutlinks());
			if(page.isRedirect() == true)
			{
				info = PageId + "\t" + "0" + "\t" + 
						PageTitle + "\t" + "null\n";
				output += info;
				System.out.print(info);
			}
			else if(page.getNumberOfOutlinks() == 0)
			{
				if(uFunc.ReplaceBoundSpace(uFunc.Simplify(page.getText()))
						.startsWith("#重定向") == false)
				{
					if(uFunc.ReplaceBoundSpace(uFunc.Simplify(page.getText()))
							.contains("#重定向") == true)
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
				if(uFunc.ReplaceBoundSpace(page.getText())
						.startsWith("#重定向") == true)
				{
					int matchNr = GetRedirectInfo(page.getText(), outputPath);
					RediNr += matchNr;
					if(matchNr != 1)
					{
						uFunc.Alert(i, PageId + " not standd redirect3!!match " + matchNr + " times");
					}
				}
				else if(uFunc.ReplaceBoundSpace(uFunc.Simplify(page.getText()))
						.contains("#重定向") == true)
				{
					uFunc.Alert(i, PageId + " not standd redirect3!!");
				}
			}
			else{
				// there are some cases that "#重定向" exist in a full page's context
				if(page.getText().length() < 200 && 
						uFunc.ReplaceBoundSpace(uFunc.Simplify(page.getText()))
						.contains("#重定向") == true)
				{
					uFunc.Alert(i, PageId + ":redirect extract missed4!!");
				}
			}
			break;
		}
		uFunc.addFile(output, outputPath);
		uFunc.addFile(uFunc.AlertOutput, uFunc.AlertPath);
	}

	private static int GetRedirectInfo(String text, String outputPath) {
		// TODO Auto-generated method stub
		Pattern p = Pattern.compile("#重定向\\s*(\\[\\[[^\\]]{1,}\\]\\])");
		Matcher m = p.matcher(text);
		int matchNr = 0;
		int lastId = 0;
		while(m.find()){
			
			String s = m.group(1);
			s = GetTargetTitle(s);
			int targetId = Zhwiki.getPageId(s);
			
			if(targetId > 0){
				if(targetId == lastId)
					continue;
				else{
					matchNr ++;
					lastId = targetId;
				}
				String info = PageId +"\t"+ targetId + "\t" + PageTitle + "\t"
						+ Zhwiki.getTitle(targetId) + "\n";
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
