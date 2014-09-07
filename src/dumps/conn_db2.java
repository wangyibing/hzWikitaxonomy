package dumps;
import java.util.Iterator;


import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Template;


public class conn_db2 {
	// 数据库连接参数配置
	public static void main(String [] args)
	{
		String LF="\n";
		try
		{
			DatabaseConfiguration dbConfig = new DatabaseConfiguration();
			dbConfig.setHost("localhost");
			dbConfig.setDatabase("wikipedia");
			dbConfig.setUser("root");
			dbConfig.setPassword("19920326");
			dbConfig.setLanguage(Language.chinese);
			
			// 创建Wikipedia处理对象
			Wikipedia wiki = new Wikipedia(dbConfig);
			String title = "地理";
			// 创建类对象
			Category cat = wiki.getCategory(4086834);
			StringBuilder sb = new StringBuilder();
			// 类别名
			sb.append("Title : " + cat.getTitle() + LF);
			sb.append("PageId : " + cat.getPageId() + LF);

			sb.append("number of pages:"+cat.getArticles().size());
			// 类别的父类信息
			sb.append("# super categories : " + cat.getParents().size() + LF);
			for (Category parent : cat.getParents()) {
			    sb.append("  " + parent.getTitle()  + " " + parent.getPageId() + LF);
			}
			// 类别的子类信息
			sb.append("# sub categories : " + cat.getChildren().size() + LF);
			for (Category child : cat.getChildren()) {
			    sb.append("  " + child.getTitle() + "\t" + child.getPageId() + LF);
			}
			///for (int pageid: cat.getPageId()) {
			//    sb.append("  " + wiki.getPage(pageid).getTitle() + LF);
			//}
			sb.append(LF);
			/*
			sb.append(LF);       
			sb.append(LF);
			// 类别下的所有页面
			sb.append("# pages : " + cat.getArticles().size() + LF);
			for (Page page : cat.getArticles()) {
			    sb.append("  " + page.getTitle() + LF);
			}       
			*/
			System.out.println(sb); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
