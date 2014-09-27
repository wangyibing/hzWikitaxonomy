package extract.pageinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import normalization.word2vec;
import tools.uFunc;
import database.Entity;
import database.Page;
import database.Zhwiki;


public class myPredicateAvg {
	private static String c = "myPredicateAvg";
	private static String info = "";
	int id;

	public String Content;
	public String Pinyin;
	public int Frequency;
	public float [] vectorW2V;
	HashMap<Integer, Double> Links;//
	int LinkNr;
	// <entityid, freq>
	HashMap<Integer, Double> SubjectCateDistr;//
	int SubIdNr;
	HashMap<Integer, Double> PredCateDistr;//
	int PredIdNr;
	HashMap<Integer, Double> ObjCateDistr;//
	int ObjIdNr;
	
	HashMap<String, Double> UpperTitles;//
	int UTNr;
	HashMap<String, Double> InfoboxNames;//
	int InboxNr;
	HashMap<String, Double> WikitextContents;//
	int WiCoNr;
	HashMap<String, Double> ObjTypes;
	int ObjTNr;
	
	public myPredicateAvg(String cont, int ID)
	{
		id = ID;
		Content = cont;
		Pinyin = uFunc.GetPinYin(cont);
		Frequency = 0;
		vectorW2V = word2vec.GetW2Vvector(cont);
		Links = new HashMap<Integer, Double>();
		LinkNr = 0;
		SubjectCateDistr = new HashMap<Integer, Double>();
		SubIdNr = 0;
		PredCateDistr = new HashMap<Integer, Double>();
		PredIdNr = 0;
		ObjCateDistr = new HashMap<Integer, Double>();
		ObjIdNr = 0;
		UpperTitles = new HashMap<String, Double>();
		UTNr = 0;
		InfoboxNames = new HashMap<String, Double>();
		InboxNr = 0;
		WikitextContents = new HashMap<String, Double>();
		WiCoNr = 0;
		ObjTypes = new HashMap<String, Double>();
		ObjTNr = 0;
	}
	
	public void addOnePredicate(myPredicate one)
	{
		String string;
		Frequency ++;
		//********** LINK **************
		string = one.Link;
		int linkId = 0;
		if(string != null)
		{
			linkId = Integer.parseInt(string.substring
					(1, string.length() - 1));
			if(linkId > 0)
			{
				UpdateIntMap(linkId, Links);
				LinkNr ++;
			}
			else {
				info = "link id not found:" + string + "\t" + one.Link;
				uFunc.Alert(true, c, info);
			}
		}
		
		//********* UPPERTITLE ************
		string = uFunc.ReplaceBoundSpace(
				uFunc.UnifiedSentenceZh2En(one.UpperTitle));
		if(string != null)
		{
			UpdateStringMap(string, UpperTitles);
			UTNr ++;
		}
		//******* INFOBOXNAME **********
		string = uFunc.ReplaceBoundSpace(
				uFunc.UnifiedSentenceZh2En(one.InfoboxName));
		if(string != null)
		{
			UpdateStringMap(string, InfoboxNames);
			InboxNr ++;
		}
		//******* WIKICONTENTS **********
		string = uFunc.ReplaceBoundSpace(
				uFunc.UnifiedSentenceZh2En(one.WikitextContent));
		if(string != null)
		{
			UpdateStringMap(string, WikitextContents);
			WiCoNr ++;
		}
		
		//******* SubjectCateDistr ********
		string = Page.getCategories(one.Pageid);
		UpdateInts2Map(string, SubjectCateDistr);
		SubIdNr ++;
		
		//******* PredCateDistr ********
		string = Page.getCategories(linkId);
		UpdateInts2Map(string, PredCateDistr);
		PredIdNr ++;
		
		//******* ObjCateDistr ********
		string = "";
		for(String obj : one.Objs)
		{
			if(obj.contains("->"))
			{
				String tar = obj.substring(obj.indexOf("->") + 3, obj.length() - 1);
				int id = Integer.parseInt(tar);
				if(id > 0)
					string += Page.getCategories(id);
				else{
					info = "obj link id not found:" + string + "\t" + obj;
					uFunc.Alert(true, c, info);
				}
			}
		}
		UpdateInts2Map(string, ObjCateDistr);
		ObjIdNr ++;
	}

	private void UpdateInts2Map(String cates,
			HashMap<Integer, Double> subjectCateDistr2) {
		// TODO Auto-generated method stub
		if(cates == null)
		{
			return;
		}
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
	/**
	 * 
	int id;

	String Content;
	String Pinyin;
	int Frequency;
	float [] vectorW2V;
	HashMap<Integer, Double> Links;//
	// <entityid, freq>
	HashMap<Integer, Double> SubjectCateDistr;//
	HashMap<Integer, Double> PredCateDistr;//
	HashMap<Integer, Double> ObjCateDistr;//
	
	HashMap<String, Double> UpperTitles;//
	HashMap<String, Double> InfoboxNames;//
	HashMap<String, Double> WikitextContents;//
	 */
	public String toString()
	{
		String info = "";
		info += "id:" + id + "\n" + "Content:" + Content + "\n" +
				"Pinyin:" + Pinyin + "\n" + "Frequency:" + Frequency + "\n" + 
				"vectorW2V:";
		if(vectorW2V == null)
			info += "null";
		else {
			for( int i = 0 ; i < vectorW2V.length; i ++)
				info += vectorW2V[i] + "\t";
		}
		info += "\n" + "Links:" + IntMapAsString(Links) + "\n" +
				"SubjectCateDistr:" + IntMapAsString(SubjectCateDistr) + "\n" +
				"PredCateDistr:" + IntMapAsString(PredCateDistr) + "\n" +
				"ObjCateDistr:" + IntMapAsString(ObjCateDistr) + "\n" +
				"UpperTitles:" + StringMapAsString(UpperTitles) + "\n" +
				"InfoboxNames:" + StringMapAsString(InfoboxNames) + "\n" +
				"WikitextContents:" + StringMapAsString(WikitextContents) + "\n";
		return info;
	}

	private String IntMapAsString(HashMap<Integer, Double> links2) {
		// TODO Auto-generated method stub
		List<Entry<Integer, Double>> pair =
				new ArrayList<Entry<Integer, Double>>();
		Collections.sort(pair, new Comparator<Map.Entry<Integer, Double>>(){
			public int compare(Map.Entry<Integer, Double> p1, 
					Map.Entry<Integer, Double> p2){
				if(p2.getValue() > p1.getValue())
					return 1;
				else if(p2.getValue() == p1.getValue())
					return 0;
				return -1;
			}
		});
		String result = "";
		for(int i = 0 ; i < pair.size(); i ++)
			result += "####" + pair.get(i).getKey() + "$$" + pair.get(i).getValue();
		if(result.equals(""))
			return null;
		return result;
	}
	private String StringMapAsString(HashMap<String, Double> links2) {
		// TODO Auto-generated method stub
		List<Entry<String, Double>> pair =
				new ArrayList<Entry<String, Double>>();
		Collections.sort(pair, new Comparator<Map.Entry<String, Double>>(){
			public int compare(Map.Entry<String, Double> p1, 
					Map.Entry<String, Double> p2){
				if(p2.getValue() > p1.getValue())
					return 1;
				else if(p2.getValue() == p1.getValue())
					return 0;
				return -1;
			}
		});
		String result = "";
		for(int i = 0 ; i < pair.size(); i ++)
			result += "####" + pair.get(i).getKey() + "$$" + pair.get(i).getValue();
		if(result.equals(""))
			return null;
		return result;
	}
}
