

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.attribute.standard.PagesPerMinute;

import tools.uFunc;
import database.Zhwiki;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Title;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.parser.NestedList;
import de.tudarmstadt.ukp.wikipedia.parser.NestedListContainer;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Template;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.FlushTemplates;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;
import extract.pageinfo.RediPageExtraction;


public class conn_db {
	public static void main(String [] args)
	{
		// 数据库连接参数配置
		try{
		DatabaseConfiguration dbConfig = new DatabaseConfiguration();
		dbConfig.setHost("localhost");
		dbConfig.setDatabase("wikipedia");
		dbConfig.setUser("root");
		dbConfig.setPassword("19920326");
		//没没用？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
		dbConfig.setLanguage(Language.chinese);
		// 创建Wikipedia处理对象
		Wikipedia wiki = new Wikipedia(dbConfig);

		String title = "";
		Page page = wiki.getPage(1102474);
		System.out.println(page.getTitle().getWikiStyleTitle().equals(title));
		System.out.println("Page title           : "+ wiki.existsPage(title) + page.getTitle());
		System.out.println("Page id              : "+ page.getPageId());
		System.out.println("getPlainText:        : "+ "\"" + page.getText() + "\"");

		Pattern p = Pattern.compile("(#\\s*重定向\\s*:?\\s*(\\[\\[[^\\]]{1,}\\]\\]))|"
				+ "(#\\s*redirect\\s*:?\\s*(\\[\\[[^\\]]{1,}\\]\\]))");
		Matcher m = p.matcher(page.getText().toLowerCase());
		if(m.find())
		System.out.println(m.group(2));
		//System.out.println(uFunc_ZH.isAdministrativeRegion(page.getPageId()));
		for(String t: page.getRedirects()){
			System.out.println(":"+t);
		}
		// 页面的所述的所有类别
		System.out.println("Categories:" + page.LF);
		for (Category category : page.getCategories()) {
		    System.out.println("  " + category.getTitle()  + " " + category.getPageId() + page.LF);
		}
		/*
		for(String ss: page.getRedirects()){
			System.out.println("\""+ss+"\"");
		}
		for(NestedListContainer nest : page.getParsedPage().getNestedLists()){
			System.out.println("\""+ nest.getText() +"\"");
			for(NestedList nest2 : nest.getNestedLists()){
				System.out.println("\"\""+ nest2.getText() +"\"\"");
				if(nest2.getLinks().iterator().hasNext() == true){
					String target = nest2.getLinks().iterator().next().getTarget();
					System.out.println("\t0"+target);
					try{
						if(wiki.existsPage(target) == true){
							int PageId = wiki.getPage(target).getPageId();
							System.out.println("\t00"+target);
							
						}
						else if(wiki.existsPage(uFunc.SimConverter.convert((target)))==true){
							int PageId = wiki.getPage(uFunc.SimConverter.convert((target)))
									.getPageId();
							System.out.println("\t11"+target);
							
						}
						else if(wiki.existsPage(uFunc.TraConverter.convert((target)))==true){
							int PageId = wiki.getPage(uFunc.TraConverter.convert((target)))
									.getPageId();
							System.out.println("\t22"+target);
							
						}
						//hasNest = true;
					}catch(Exception e){
						//System.out.println("error:"+oneline);
						e.printStackTrace();
						continue;
					}
				}
				//System.out.println();
			}
		}
		StringBuilder sb1 = new StringBuilder();
		String infobox="infobox";
		System.out.println(sb1);
		//System.out.println("Page text            : "+page.getText());
		
		System.out.println("getWikipediaId:\t"+wiki.getWikipediaId());
		
		//ParserPage pp=new
		// wikipedia页面的title
		//System.out.println("Queried string       : " + page.getTitle().toString());
		//System.out.println("Title                : " + page.getTitle());
		//System.out.println(page.getCategories());
		//System.out.println("\n\n\n\ngetParsedpageText:   "+page.getParsedPage().getText());
		//System.out.println("\n\n\n\ngetPlainText:   "+page.getText());
		//System.out.println("\n\n\n\ngetText:   \n"+page.getText());
		
		// 是否是消歧页面
		System.out.println("IsDisambiguationPage : " + page.isDisambiguation());       
		// 是否是重定向页面
		System.out.println("redirect page query  : " + page.isRedirect());       
		// 有多少个页面指向该页面
		System.out.println("# of ingoing links   : " + page.getNumberOfInlinks());       
		// 该页面指向了多少个页面
		System.out.println("# of outgoing links  : " + page.getNumberOfOutlinks());
		// 该页面属于多少个类别
		System.out.println("# of categories      : " + page.getNumberOfCategories());
		
		*/
		/*
		StringBuilder sb = new StringBuilder();
		
		sb.append(page.LF);
		
		// 页面的所有重定向页面
		sb.append("Redirects" + page.LF);
		for (String redirect : page.getRedirects()) {
		    sb.append("  " + new Title(redirect).getPlainTitle() + page.LF);
		}
		sb.append(page.LF);       
		// 页面的所述的所有类别
		sb.append("Categories" + page.LF);
		for (Category category : page.getCategories()) {
		    sb.append("  " + category.getTitle() + page.LF);
		}
		sb.append(page.LF);
		for(Template t:page.getParsedPage().getTemplates()){
				System.out.print("\ninfoobox:"+t.getName()+"\n"+t.toString());
		}
		
		// 指向该页面的所有页面
		sb.append("In-Links" + page.LF);
		for (Page inLinkPage : page.getInlinks()) {
		    sb.append("  " + inLinkPage.getTitle() + page.LF);
		}
		sb.append(page.LF);
		// 该页面指向的所有页面
		sb.append("Out-Links" + page.LF);
		for (Page outLinkPage : page.getOutlinks()) {
		    sb.append("  " + outLinkPage.getTitle() + page.LF);
		}            
		
		System.out.println(sb);
		*/

		/*
		
		for (Page inLinkPage : page.getInlinks()) {
			System.out.println("  " + inLinkPage.getTitle() + page.LF);
		}
		
		*/
		//System.out.println(page.getTitle().getWikiStyleTitle());
		
		
		
		/*
		for(Template t:page.getParsedPage().getTemplates()){
				System.out.println("infoobox:"+t.getName());
		}
		for(Template t:page.getParsedPage().getTemplates()){
			if(t.getName().contains("海岸线")){
				System.out.println("Name:"+t.getName());
				
				for(String para: t.getParameters())
					System.out.println("\t" + para);
			}
		}
		for(FormatType t:page.getParsedPage().getFirstParagraph().getFormats()){
			System.out.println("\tFormatType\t" + t.toString());
		}
		System.out.println("getFirstParagraphname:"
				+page.getParsedPage().getFirstParagraph().getType().name());
		System.out.println("getFirstParagraphname:"
				+page.getParsedPage().getFirstParagraph().toString());
		for(Link t:page.getParsedPage().getFirstParagraph().getLinks()){
			System.out.println("\tlink:\tgetTarget:" + t.getTarget()+"\tgetText:"+t.getText());
			System.out.println("\t\tlinkgetHomeElement" + t.getHomeElement().getText());
			System.out.println("\t\tgetType:" + t.getType()+"\tgetPos:"+t.getPos());
		}
		for(Link t:page.getParsedPage().getFirstParagraph().getLinks()){
			System.out.println("\tlink:\tgetTarget:" + t.getTarget()+"\tgetText:"+t.getText());
			System.out.println("\t\tlinkgetHomeElement" + t.getHomeElement().getText());
			System.out.println("\t\tgetType:" + t.getType()+"\tgetPos:"+t.getPos());
		}
		for(NestedListContainer t:page.getParsedPage().getNestedLists()){
			System.out.println("NestedList:\t" + t.getText());
		}
		for(Section t:page.getParsedPage().getSections()){
			System.out.println("Section:\tgetLevel:" + t.getLevel()+"\tgetText:"+t.getText());
			System.out.println("\tgetTitle" + t.getTitle());
		}
		*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void InfoString2Byte(String title) {
		// TODO Auto-generated method stub
		String out = "";
		for(char c : title.toCharArray())
		{
			out += c +":";
		}
		System.out.println(out);
	}

}
