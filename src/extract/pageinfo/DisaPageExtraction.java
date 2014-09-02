package extract.pageinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

import tools.URL2UTF8;
import tools.uFunc;
import database.DisPage;
import database.Zhwiki;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.parser.Link;
import de.tudarmstadt.ukp.wikipedia.parser.NestedList;
import de.tudarmstadt.ukp.wikipedia.parser.NestedListContainer;
import extract.web.ExtractAPI;

public class DisaPageExtraction {
	static String i = "DisaPageExtraction";
	
	public static void ExtracDisPages(String folder, String infoPath)
	{
		
		uFunc.AlertPath = infoPath;
		uFunc.deleteFile(folder + DisPage.CanonicalPath_pagelist);
		uFunc.deleteFile(folder + DisPage.CanonicalPath_pageoutlinks + "1");
		uFunc.deleteFile(folder + DisPage.CanonicalPath_pageoutlinks);
		uFunc.deleteFile(infoPath);
		uFunc.deleteFile(DisPage.ExceptionListPath);
		
		
		ExtractDisPages(folder, infoPath);
		
		GenerateExceptions(folder + DisPage.CanonicalPath_pageoutlinks + "1", 
				folder + DisPage.ExceptionListPath);
		ResultFiltering(folder + DisPage.CanonicalPath_pageoutlinks + "1",
				folder + DisPage.ExceptionListPath,
				folder + DisPage.CanonicalPath_pageoutlinks);
		
		uFunc.addFile(uFunc.AlertOutput, uFunc.AlertPath);
	}

	private static void ResultFiltering(String srcFile, String exception, 
			String outPath) {
		// TODO Auto-generated method stub
		Zhwiki.init();
		HashMap<String, Integer> targetFreq = 
				new HashMap<String, Integer>();
		BufferedReader br = uFunc.getBufferedReader(exception);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int times = Integer.parseInt(ss[1]);
				if(times > 2)
					targetFreq.put(ss[0], times);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		br = uFunc.getBufferedReader(srcFile);
		String output = "";
		int outNr = 0;
				
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int linkId = Integer.parseInt(ss[1]);
				String linkTitle = uFunc.Simplify(Zhwiki.getTitle(linkId));
				int freq = 1;
				if(targetFreq.containsKey(linkTitle))
				{
					freq = targetFreq.get(linkTitle);
				}
				if((freq > 3 && linkTitle.endsWith("乡")== false && linkTitle.endsWith("镇")))
					continue;
				if(linkTitle.endsWith("省") || linkTitle.endsWith("语") || linkTitle.endsWith("市"))
					continue;
				output += oneLine + "\n";
				outNr ++;
				if(outNr % 1000 == 0)
				{
					uFunc.addFile(output, outPath);
					output = "";
				}
			}
			uFunc.addFile(output, outPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void GenerateExceptions(String firstExtractFile,
			String ExceptionFile) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(firstExtractFile);
		String oneLine = "";
		HashMap<String, Integer> targetFreq = 
				new HashMap<String, Integer>();
		HashMap<String, Integer> PageTarget = 
				new HashMap<String, Integer>();
		
		Zhwiki.init();
		int lastId = 0;
		int pageid = 0;
		int freq = 0;
		try {
			while( (oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 4)
				{
					System.out.println("error line:\"" + oneLine + "\"");
					continue;
				}
				pageid = Integer.parseInt(ss[0]);
				if(pageid != lastId)
				{
					PageTarget.clear();
					lastId = pageid;
				}
				String title = Zhwiki.getTitle(Integer.parseInt(ss[1]));
				if(PageTarget.containsKey(title) == false)
				{
					PageTarget.put(title, 0);
					freq = 1;
					if(targetFreq.containsKey(title))
						freq += targetFreq.remove(title);
					targetFreq.put(title, freq);
				}
					
			}
			Iterator<Entry<String, Integer>> it = 
					targetFreq.entrySet().iterator();
			while( it.hasNext())
			{
				Entry<String, Integer> next = it.next();
				if(next.getValue() <= 1)
					it.remove();
				
			}
			System.out.println("targetFreq.size(): " + targetFreq.size());
			uFunc.SaveHashMap(targetFreq, ExceptionFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static int PageId = 0;
	static String PageTitle = "";
	
	static String output = "";
	static int outNr = 0;
	static boolean PageExtraced = false;
	/**
	 * extra dis pages:99109, 1372763, 3843677, 4140222, 4140539
	 * @param srcFile
	 * @param exception
	 * @param outPath
	 */
	private static void ExtractDisPages(String folder, String infoPath) {
		// TODO Auto-generated method stub
		Zhwiki.init();
		int PageNr = 0;
		int DisNr = 0;
		String info = "";
		
		long time = System.currentTimeMillis();
		for(Page page : Zhwiki.wiki.getPages())
		{
			//page = Zhwiki.getPage(43690);
			PageNr ++;
			PageId = page.getPageId();
			PageTitle = Zhwiki.getTitle(PageId);
			//uFunc.Alert("", PageId + "\t" + PageTitle);
			PageExtraced = true;
			if(PageId <= 0){
				uFunc.Alert(i, "pageid null:" + page.getText());
				continue;
			}
			if(PageNr % 5000 == 0)
			{
				info = "PageNumPast:" + PageNr + "\t" + "DisNr:" + DisNr + 
						"\t" + "cost:" + (System.currentTimeMillis() - time)/1000 +
						"\t" + "LinkOutputNr:" + LinkOutputNr;
				time = System.currentTimeMillis();
				uFunc.Alert("", info);
			}
			if(page.isDisambiguation() == true || 
					PageId == 99109 || PageId == 1372763 
					|| PageId == 3843677 || PageId == 4140222 
					|| PageId == 4140539)
			{
				DisNr ++;
				PageExtraced = false;
				output += PageId + "\t" + PageTitle + "\n";
				outNr ++;
				if(outNr % 1000 == 0)
				{
					uFunc.addFile(output, folder + DisPage.CanonicalPath_pagelist);
					output = "";
				}
				ExtractPageOutlinks(page, folder, infoPath);
			}
			else
			{
				/*
				String text = page.getText().toLowerCase();
				if(text.contains("\\{\\{\\s*disambig"))
				{
					info = "not formated disa:" + PageId;
					System.out.println(info);
					uFunc.Alert("DisaPageExtraction", info);
					
				}
				else{
					ParsedPage parsedPage = page.getParsedPage();
					if(parsedPage != null && 
							parsedPage.getTemplates().size() < 10)
					{
						for(Template temp: page.getParsedPage().getTemplates())
						{
							String name = uFunc.ReplaceBoundSpace(
									uFunc.Simplify(temp.getName().toLowerCase()));
							if(name.equals("disambig") || name.equals("消歧义"))
							{
								info = "not formated disa:" + PageId;
								uFunc.Alert("DisaPageExtraction", info);
							}
						}
					}
				}
				*/
			}
			if(PageExtraced == false)
			{
				ExtractFromWeb(PageId, folder, infoPath);
				if(PageExtraced == false)
					uFunc.Alert(i, "disa extract failed:" + PageId);
				
			}
			//break;
		}
		uFunc.addFile(output, folder + DisPage.CanonicalPath_pagelist);
		uFunc.addFile(LinkOutput, folder + DisPage.CanonicalPath_pageoutlinks + "1");
		uFunc.addFile(errorOutput, infoPath);
	}

	static NodeFilter DIVFilter = new TagNameFilter("DIV");
	static NodeFilter ContFilter = new HasAttributeFilter("ID", "mw-content-text");
	static NodeFilter filter = null;
	static Vector<Tag> LIs = new Vector<Tag>();
	static NodeVisitor Livisitor = new NodeVisitor(){
		public void visitTag(Tag tag){
			String tName = tag.getTagName();
			if(tName.equals("LI") || tName.equals("DD")
					|| tName.equals("P"))
				LIs.add(tag);
		}
	};
	static Vector<String> titles = new Vector<String>();
	static NodeVisitor Linkvisitor = new NodeVisitor(){
		public void visitTag(Tag tag){
			if(tag.getTagName().equals("A")){
				String link = tag.getAttribute("HREF");
				String entity = URL2UTF8.unescape(link.substring
						(link.indexOf("/wiki/") + 6));
				if(entity.contains("#"))
					entity = entity.substring(0,  entity.indexOf("#"));
				titles.add(entity);
			}
		}
	};
	public static String info = "";
	private static void ExtractFromWeb(int pageId, String folder, String infoPath) {
		// TODO Auto-generated method stub
		if(filter == null)
			filter = new AndFilter(DIVFilter, ContFilter);
		LIs.clear();
		titles.clear();
		String url = "http://zh.wikipedia.org/wiki?curid=" + pageId;
		try {
			Parser pageParser = new Parser(url);
			pageParser.setEncoding("utf8");
			NodeList pageNodeList = pageParser.parse(filter);
			pageNodeList.visitAllNodesWith(Livisitor);
			for(Tag tag: LIs){
				titles.clear();
				if(tag.getChildren() == null)
					continue;
				tag.getChildren().visitAllNodesWith(Linkvisitor);
				int id = 0;
				int MaxChnOccr = 0;
				for(int i = 0; i < titles.size(); i ++)
				{
					String entity = titles.get(i);
					int pageid = Zhwiki.getPageId(entity);
					if(pageid <= 0)
						pageid = Zhwiki.getPageId(uFunc.Simplify(entity));
					if(pageid <= 0)
						pageid = Zhwiki.getPageId(uFunc.TraConverter.convert(entity));
					if(pageid <= 0)
						pageid = ExtractAPI.GetPageId(entity);
					if(pageid <= 0)
					{
						String info = "";
						info = "no targer ERROR:" + PageId + "\t"+ PageTitle + "\t" + entity;
						errorOutput += info +"\n";
						errorNr ++;
						if(errorNr % 1000 == 0)
						{
							uFunc.addFile(errorOutput, infoPath);
							errorOutput = "";
						}
						continue;
					}
					if(GetChnOccr(pageid, titles) > MaxChnOccr ||
							i == 0)
					{
						id = pageid;
						MaxChnOccr = GetChnOccr(pageid, titles);
					}
					if(pageDis.containsKey(id))
					{
						//System.out.println(PageId + "\t" );
						continue;
					}
					else{
						pageDis.put(id, 0);
					}
					info  = "web disa:" + Zhwiki.getTitle(id) + "\t" + id
							+ "\t" + PageTitle + "\t" + PageId;
					//uFunc.Alert("", info);
				}
				AddOneInstance(id, folder, infoPath);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	static String LinkOutput = "";
	static int LinkOutputNr = 0;
	static String errorOutput = "";
	static int errorNr = 0;
	static HashMap<Integer, Integer> pageDis = 
			new HashMap<Integer, Integer>(); 
	private static void ExtractPageOutlinks(Page page, String folder, 
			String infoPath) {
		// TODO Auto-generated method stub
		Vector<String> titles = new Vector<String>();
		titles.add(PageTitle);
		for(String redi : page.getRedirects())
			titles.add(redi);
		pageDis.clear();
		//uFunc.Alert(i, page.getText());
		for(NestedListContainer nest : 
			page.getParsedPage().getNestedLists()){
			//uFunc.Alert(i, nest.getText());
			for(NestedList nest2 : nest.getNestedLists()){
				//uFunc.Alert(i, nest2.getText());
				List<Link> linklist = nest2.getLinks();
				if(linklist == null || linklist.size() < 1)
				{
					
					continue;
				}
				int id = 0;
				int MaxChnOccr = 0;
				for(int i = 0 ; i < linklist.size(); i ++)
				{
					String link = linklist.get(i).getTarget();
					int pageid = Zhwiki.getPageId(link);
					if(pageid <= 0)
					{
						link = uFunc.Simplify(link);
						pageid = Zhwiki.getPageId(link);
					}
					if(pageid <= 0)
						pageid = Zhwiki.getPageId(uFunc.TraConverter.convert(link));
					if(pageid <= 0)
					{
						String info = "";
						info = "no targer ERROR:" + PageId + "\t"+ PageTitle + "\t" + id;
						errorOutput += info +"\n";
						errorNr ++;
						if(errorNr % 1000 == 0)
						{
							uFunc.addFile(errorOutput, infoPath);
							errorOutput = "";
						}
						continue;
					}
					if(GetChnOccr(pageid, titles) > MaxChnOccr ||
							i == 0)
					{
						id = pageid;
						MaxChnOccr = GetChnOccr(pageid, titles);
					}
				}
				if(pageDis.containsKey(id))
				{
					//System.out.println(PageId + "\t" );
					continue;
				}
				else{
					pageDis.put(id, 0);
				}
				AddOneInstance(id, folder, infoPath);
			}
		}
	}

	
	private static int GetChnOccr(int id, Vector<String> titles) {
		// TODO Auto-generated method stub
		int sim = 0;
		Vector<String> linkTitles = new Vector<String>();
		try {
			linkTitles.add(Zhwiki.getTitle(id));
			for(String redi : Zhwiki.wiki.getPage(id).getRedirects())
				linkTitles.add(redi);
		} catch (WikiApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int ii = 0; ii < linkTitles.size(); ii ++){
			String link = linkTitles.get(ii);
			for(int i = 0 ; i < titles.size(); i ++)
			{
				int tsim = 0;
				String cont = titles.get(i);
				for(int j = 0 ; j < cont.length(); j ++)
					if(uFunc.hasChineseCharactor("" + cont.charAt(j))
							&& link.contains(cont.charAt(j) + ""))
						tsim ++;
				if(tsim > sim)
					sim = tsim;
			}
		}
		return sim;
	}

	private static void AddOneInstance(int id, String folder, String infoPath) {
		// TODO Auto-generated method stub
		if(id <= 0)
			return;
		LinkOutput += PageId +"\t"+ id +"\t"+ PageTitle +"\t"+ Zhwiki.getTitle(id) +"\n";
		LinkOutputNr++;
		PageExtraced = true;
		if(LinkOutputNr % 1000 == 0){
			uFunc.addFile(LinkOutput, 
					folder + DisPage.CanonicalPath_pageoutlinks + "1");
			LinkOutput = "";
		}
		
	}
}
