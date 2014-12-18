package category;


import java.util.HashMap;

import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

/**
 * remain to be correct
 * @author hanzhe
 *
 */
public class SubCategory {
	// 数据库连接
	static DatabaseConfiguration dbConfig = new DatabaseConfiguration();
	//wikipedia实例
	static Wikipedia wiki;
	static boolean inited;

	public static void main(String [] args){
		ConnectToDB();
		String fathName = "一级行政区";
		String SonName = "中华人民共和国省份";
		try {
			Category father = wiki.getCategory(fathName);
			Category son = wiki.getCategory(SonName);
			System.out.println(IsSuperCategory(father, son, 0));
		} catch (WikiApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	static HashMap<Integer, Integer> SearchedCate = 
			new HashMap<Integer, Integer>();
	public static boolean IsSuperCategory(Category father2, Category son2, int level) {
		// TODO Auto-generated method stub
		if(inited == false)
			ConnectToDB();
		if(level > 4)
			return false;
		SearchedCate.put(father2.getPageId(), 0);
		
		if(father2.getChildren() == null)
			return false;
		for(Category subfather : father2.getChildren()){
			if(SearchedCate.containsKey(subfather.getPageId())== true){
				continue;
			}
			if(subfather.getPageId() == son2.getPageId())
				return true;
			SearchedCate.put(subfather.getPageId(), 8);
			if(IsSuperCategory(subfather, son2, level+1)== true)
				return true;
		}
		return false;
	}

	static void ConnectToDB() {
		try{
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
