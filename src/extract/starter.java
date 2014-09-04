package extract;

import database.Entity;
import database.RediPage;
import extract.pageinfo.DisaPageExtraction;
import extract.pageinfo.EntityTitleExtraction;
import extract.pageinfo.RediPageExtraction;
import tools.uFunc;


public class starter {

	public static String Folder = 
			"/home/hanzhe/Public/result_hz/wiki_count/pageinfo/";
	public static void main(String [] args)
	{
		//ExtractDis();
		EntityTitleExtraction.Extract(Entity.CanonicalPath_titles, 
				Folder + "EntityExtractionInfo");
		//ExtractRedi();
		//test();
	}

	public static void ExtractDis() {
		// TODO Auto-generated method stub
		DisaPageExtraction.ExtracDisPages("", 
				"data/DisPageExtractionInfo");
	}

	public static void ExtractRedi() {

		RediPageExtraction.ExtracRedirectPages(RediPage.CanonicalPath, 
				"data/RediPageExtractionInfo");
	}

	public static void test() {
		// TODO Auto-generated method stub
		uFunc.Alert(null, uFunc.TraConverter.convert("重定向"));;
	}
}
