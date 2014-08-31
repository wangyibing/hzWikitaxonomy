package database;


import tools.uFunc;
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
	public static Wikipedia wiki;
	private static boolean inited = false;
	public static Page page;
	public static int PageNum = 0;
	public static int InfoboxNum = 0;
	public static int PageId = 0;
	public static String PageTitle = "";
	
	
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
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * search title, sim_title, trans_title
	 * @param title
	 * @return
	 */
	public static int getPageId(String title){
		ConnectToDB();
		try {
			if(wiki.existsPage(title) == false)
				return 0;
			return wiki.getPage(title).getPageId();
		} catch (Exception e) {
			e.printStackTrace();
			uFunc.Alert("Zhwiki", "getId error " + title);
			return 0;
		}
	}
	public static String getTitle(int PageId){
		ConnectToDB();
		try {
			return uFunc.Simplify(
					wiki.getPage(PageId).getTitle().getWikiStyleTitle());
		} catch (Exception e) {
			return null;
		}
	}
	public static void ConnectToDB() {
		if(inited == true)
			return;
		try{
			System.out.println("ZHWIKI");
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
	public static void init() {
		// TODO Auto-generated method stub
		ConnectToDB();
		PageNum = 0;
		InfoboxNum = 0;
		PageId = 0;
	}
}
