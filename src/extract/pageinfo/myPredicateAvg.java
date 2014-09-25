package extract.pageinfo;

import java.util.HashMap;

import tools.uFunc;
import database.Entity;
import database.Page;
import database.Zhwiki;


public class myPredicateAvg {
	private static String c = "myPredicateAvg";
	private static String info = "";
	int id;

	String Content;
	String Pinyin;
	int Frequency;
	float [] vectorW2V;
	HashMap<Integer, Integer> Links;//
	// <entityid, freq>
	HashMap<Integer, Double> SubjectCateDistr;//
	HashMap<Integer, Double> PredCateDistr;//
	HashMap<Integer, Double> ObjCateDistr;//
	
	HashMap<String, Double> UpperTitles;//
	HashMap<String, Double> InfoboxNames;//
	HashMap<String, Double> WikitextContents;//
	HashMap<String, Double> ObjTypes;
	
	public myPredicateAvg(String cont, int ID)
	{
		id = ID;
		Content = cont;
		Pinyin = uFunc.GetPinYin(cont);
		Frequency = 0;
		vectorW2V = null;
		Links = new HashMap<Integer, Integer>();
		SubjectCateDistr = new HashMap<Integer, Double>();
		PredCateDistr = new HashMap<Integer, Double>();
		ObjCateDistr = new HashMap<Integer, Double>();
		UpperTitles = new HashMap<String, Double>();
		InfoboxNames = new HashMap<String, Double>();
		WikitextContents = new HashMap<String, Double>();
		ObjTypes = new HashMap<String, Double>();
	}
	
	public void addOnePredicate(myPredicate one)
	{
		String string;
		Frequency ++;
		//********** LINK **************
		string = one.Link;
		string = string.substring(string.indexOf("[") + 1, 
				string.length() - 1);
		int linkId = Entity.getId(string);
		if(linkId <= 0)
			linkId = Zhwiki.getPageId(string);
		if(linkId > 0)
		{
			int freq = 1;
			if(Links.containsKey(linkId))
				freq += Links.remove(linkId);
			Links.put(linkId, freq);
		}
		else {
			info = "link id not found:" + string + "\t" + one.Link;
			uFunc.Alert(true, c, info);
		}
		//********* UPPERTITLE ************
		string = uFunc.ReplaceBoundSpace(
				uFunc.UnifiedSentenceZh2En(one.UpperTitle));
		if(string != null)
		{
			UpdateStringMap(string, UpperTitles);
		}
		//******* INFOBOXNAME **********
		string = uFunc.ReplaceBoundSpace(
				uFunc.UnifiedSentenceZh2En(one.InfoboxName));
		if(string != null)
		{
			UpdateStringMap(string, InfoboxNames);
		}
		//******* WIKICONTENTS **********
		string = uFunc.ReplaceBoundSpace(
				uFunc.UnifiedSentenceZh2En(one.WikitextContent));
		if(string != null)
		{
			UpdateStringMap(string, WikitextContents);
		}
		//******* SubjectCateDistr ********
		string = Page.getCategories(one.Pageid);
		UpdateInts2Map(string, SubjectCateDistr);
		//******* PredCateDistr ********
		string = Page.getCategories(linkId);
		UpdateInts2Map(string, PredCateDistr);
		//******* ObjCateDistr ********
		string = "";
		for(String obj : one.Objs)
		{
			if(obj.contains("->"))
			{
				String tar = obj.substring(obj.indexOf("->") + 3, obj.length() - 1);
				int id = Entity.getId(tar);
				if(id < 0)
					id = Zhwiki.getPageId(tar);
				if(id > 0)
					string += Page.getCategories(id);
				else{
					info = "obj link id not found:" + string + "\t" + obj;
					uFunc.Alert(true, c, info);
				}
			}
		}
		UpdateInts2Map(string, ObjCateDistr);
	}

	private void UpdateInts2Map(String cates,
			HashMap<Integer, Double> subjectCateDistr2) {
		// TODO Auto-generated method stub
		if(cates.equals(""))
			return;
		for(String s : cates.split(";"))
		{
			int cateid = Integer.parseInt(s); 
			UpdateIntMap(cateid, subjectCateDistr2);
		}
	}

	private static final void UpdateIntMap(int linkId, 
			HashMap<Integer, Double> hashmap) {
		// TODO Auto-generated method stub
		double freq = 1;
		if(hashmap.containsKey(linkId))
			freq += hashmap.remove(linkId);
		hashmap.put(linkId, freq);
	}

	private static final void UpdateStringMap(String key, 
			HashMap<String, Double> hashmap) {
		// TODO Auto-generated method stub
		double freq = 1;
		if(hashmap.containsKey(key))
			freq += hashmap.remove(key);
		hashmap.put(key, freq);
	}
	
	public String toString()
	{
		String info = "";
	}
}
