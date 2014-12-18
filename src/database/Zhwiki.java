package database;


import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

public class Zhwiki {
	// 数据库连接
	static DatabaseConfiguration dbConfig = new DatabaseConfiguration();
	//wikipedia实例
	private static Wikipedia wiki;
	private static boolean inited = false;
	public static Page page;
	public static int PageNum = 0;
	public static int InfoboxNum = 0;
	public static int PageId = 0;
	public static String PageTitle = "";
	
	public static Iterable<Page> getPages()
	{
		ConnectToDB();
		return wiki.getPages();
	}
	public static Category getCategory(int cateId)
	{
		ConnectToDB();
		Category cate = wiki.getCategory(cateId);
		return cate;
	}
	
	public static Page getPage(int pageId)
	{
		ConnectToDB();
		try {
			Page p = wiki.getPage(pageId);
			return p;
		} catch (WikiApiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
		
	}
	
	private static void ConnectToDB() {
		if(inited == true)
			return;
		try{
			System.out.println("ZHWIKI");
			System.setProperty("hibernate.c3p0.timeout", "10");
			//连接到wikipedia
			dbConfig.setHost("localhost");
			dbConfig.setDatabase("wikipedia");
			dbConfig.setUser("root");
			dbConfig.setPassword("19920326");
			dbConfig.setLanguage(Language.chinese);
			wiki = new Wikipedia(dbConfig);
			inited = true;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
