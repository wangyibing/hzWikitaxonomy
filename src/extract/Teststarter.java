package extract;

import database.RediPage;
import extract.pageinfo.DisaPageExtraction;
import extract.pageinfo.RediPageExtraction;
import extract.predicate.WikitextPredicate;
import tools.uFunc;


public class Teststarter {

	public static String Folder = 
			"/home/hanzhe/Public/result_hz/wiki_count/pageinfo/";
	public static void main(String [] args)
	{
		//Langlinks.GetEntityIdEnTitlePair(null);
		//ExtractDis();
		//EntityTitleExtraction.Extract(Entity.CanonicalPath_titles, 
		//		Folder + "EntityExtractionInfo");
		
		WikitextPredicate.Extract(
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId.sorted",
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/dumpsTriples",
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId2");
		//ExtractRedi();
		//test();
	}

	public static void ExtractDis() {
		// TODO Auto-generated method stub
		DisaPageExtraction.ExtracDisPages("", 
				"data/pageinfo/DisPageExtractionInfo");
	}

	public static void ExtractRedi() {

		RediPageExtraction.ExtracRedirectPages(RediPage.CanonicalPath, 
				"data/pageinfo/RediPageExtractionInfo");
	}

	public static void test() {
		// TODO Auto-generated method stub
		uFunc.Alert(null, uFunc.TraConverter.convert("重定向"));;
	}
}
