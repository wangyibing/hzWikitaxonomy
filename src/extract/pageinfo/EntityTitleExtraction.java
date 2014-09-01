package extract.pageinfo;

import java.util.HashMap;

import tools.uFunc;
import database.DisPage;
import database.RediPage;
import database.Zhwiki;
import de.tudarmstadt.ukp.wikipedia.api.Page;

public class EntityTitleExtraction {

	private static int PageId;
	private static String PageTitle;
	private static String i = "EntityTitleExtraction";

	static HashMap<String, Integer> simTitles2Id = 
			new HashMap<String, Integer>();
	static HashMap<String, Integer> traTitles2Id = 
			new HashMap<String, Integer>();
	/**
	 * simplfy: simplified TitleContent 
	 * tradition: original TitleContent
	 * redirect: TitleContent containing redirect titles
	 * 
	 * after test we find that redirect titles may be duplicate
	 * normal pages and disam pages may have same TitleContent
	 * @param outputPath
	 * @param outputTitlesPath
	 * @param infoPath
	 */
	public static void Extract(String outputTitlesPath, String infoPath)
	{
		int PageNr = 0;
		int RediNr = 0;
		int DisaNr = 0;
		String outputTitles = "";
		String info = "";
		String type = "";
		uFunc.deleteFile(infoPath);
		uFunc.deleteFile(outputTitlesPath);
		uFunc.AlertPath = infoPath;
		Zhwiki.init();
		HashMap<String, Integer> simTitle2Id = 
				new HashMap<String, Integer>();
		HashMap<String, Integer> traTitle2Id = 
				new HashMap<String, Integer>();
		long time = System.currentTimeMillis();
		for(Page page : Zhwiki.wiki.getPages())
		{
			PageNr ++;
			PageId = page.getPageId();
			PageTitle = Zhwiki.getTitle(PageId);
			if(PageId <= 0){
				uFunc.Alert(i , "pageid null:" + page.getText());
				continue;
			}
			if(PageTitle == null)
			{
				uFunc.Alert(i, "title null:" + PageId);
			}
			if(PageNr % 5000 == 0)
			{
				info = "PageNumPast:" + PageNr + "\t" + "cost:" +
						(System.currentTimeMillis()- time)/1000 + "\t" +
						"RediNr:" + RediNr + "\t" + "DisaNr:" + DisaNr + "\n";
				time = System.currentTimeMillis();
				uFunc.addFile(info, infoPath);
				System.out.print(info);
			}
			if(RediPage.getTargetPageid(PageId) > 0)
			{
				type = "redi";
				RediNr ++;
				if(DisPage.GetOutlinks(PageId) != null)
				{
					uFunc.Alert(i, PageId + "is redirect and disambiguation page");
				}
			}
			else if(DisPage.GetOutlinks(PageId) != null)
			{
				type = "Disa";
				DisaNr ++;
			}
			else
			{
				type = "title";
			}
			
			// only calculate normal pages(not redirect and disambiguation page)
			if(type.equals("title") && PageTitle != null)
			{
				if(simTitle2Id.containsKey(uFunc.Simplify(PageTitle)))
				{
					System.out.println("simplify entity repeated:" + uFunc.Simplify(PageTitle)
							+ "\t" + simTitle2Id.get(uFunc.Simplify(PageTitle)) + ";" + PageId);
				}
				else{
					simTitle2Id.put(uFunc.Simplify(PageTitle), PageId);
				}
				simTitles2Id.put(PageTitle, PageId);
				
				if(type.equals("redi") == false)
				{
					if(traTitle2Id.containsKey(PageTitle))
					{
						System.out.println("tradition entity repeated:" + PageTitle + "\t" + 
								traTitle2Id.get(PageTitle) + ";" + PageId);
					}
					else{
						traTitle2Id.put(PageTitle, PageId);
					}
					traTitles2Id.put(PageTitle, PageId);
				}
			}
			
			
			info = PageId + "\t" + PageTitle + "\t" + type + "\n";
			outputTitles += info;
			for(String redi : page.getRedirects())
			{
				outputTitles += PageId + "\t" + redi + "\t" + "redi" + "\n";
				/*
				// only calculate normal pages(not redirect and disambiguation page)
				if(type.equals("title") == true)
				{
					if(simTitles2Id.containsKey(uFunc.Simplify(redi)) &&
							simTitles2Id.get(uFunc.Simplify(redi)) != PageId)
					{
						info = "simplify entity redirect repeated:" + 
								uFunc.Simplify(redi) + "\t" + 
								simTitles2Id.get(uFunc.Simplify(redi)) + ";" + PageId;
					}
					else{
						simTitles2Id.put(uFunc.Simplify(redi), PageId);
					}
				}
				// only calculate normal pages(not redirect and disambiguation page)
				if(type.equals("redi") == false)
				{
					if(traTitles2Id.containsKey(redi) &&
							traTitles2Id.get(redi) != PageId)
					{
						info = "tradition entity redirect repeated:" + redi + "\t" + 
								traTitle2Id.get(redi) + ";" + PageId;
					}
					else{
						traTitles2Id.put(redi, PageId);
					}
				}
				*/
			}
			if(PageNr % 1000 == 0)
			{
				uFunc.addFile(outputTitles, outputTitlesPath);
				outputTitles = "";
			}
		}
		uFunc.addFile(outputTitles, outputTitlesPath);
		uFunc.addFile(uFunc.AlertOutput, uFunc.AlertPath);
		
	}
}
