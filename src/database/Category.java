package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;

public class Category {
	private static String info;
	private static String i = "Category";
	private static boolean FathersInited = false;
	private static HashMap<Integer, String> Fathers = 
			new HashMap<Integer, String>();
	
	public static boolean IsFather(int fatherCateId, int subCateId)
	{
		if(FathersInited == false)
			InitFathers();
		// recursive search
		return IsFather2(fatherCateId, subCateId);
	}
	
	private static boolean IsFather2(int fatherCateId, int subCateId) {
		// TODO Auto-generated method stub
		if(fatherCateId == subCateId)
			return true;
		if(Fathers.containsKey(subCateId) == false)
		{
			System.out.println("father not exist, cateid:" + subCateId);
			return false;
		}
		for(String f : Fathers.get(subCateId).split(","))
		{
			int subfatherid = Integer.parseInt(f);
			if(IsFather2(subfatherid, subCateId) == true)
				return true;
		}
		return false;
	}

	private static void InitFathers() {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(CateFatherFile);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int cateid = Integer.parseInt(ss[0]);
				int fatherid = Integer.parseInt(ss[1]);
				String fathers = "";
				if(Fathers.containsKey(cateid))
				{
					fathers = Fathers.remove(cateid) + "," + fatherid;
				}
				else{
					fathers = fatherid + "";
				}
				Fathers.put(cateid, fathers);
			}
			System.out.println("father category map inited, size:" + 
					Fathers.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String GetCateName(int cateId)
	{
		if(id2titInited == false)
			InitId2Tit();
		if(CateId2Title.containsKey(cateId))
			return CateId2Title.get(cateId);
		return null;
	}

	public static String GetSubCates(int cateId)
	{
		if(SonsInited  == false)
			InitSons();
		if(Sons.containsKey(cateId))
			return Sons.get(cateId);
		return null;
	}
	

	private static String CateFatherFile = 
			"/home/hanzhe/git/hzWikitaxonomy/data/pageinfo/CateFather";
	private static boolean SonsInited = false;
	private static HashMap<Integer, String> Sons = 
			new HashMap<Integer, String>();
	private static void InitSons() {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(CateFatherFile);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int cateid = Integer.parseInt(ss[0]);
				int fatherid = Integer.parseInt(ss[1]);
				String catesons = "";
				if(Sons.containsKey(fatherid) == true)
				{
					catesons = Sons.remove(fatherid) + "," + cateid;
				}
				else{
					catesons = cateid + "";
				}
				Sons.put(fatherid, catesons);
			}
			System.out.println("subcategory map inited, size:" + Sons.size());
			SonsInited = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	private static String CategoryTitleFile = 
			"data/pageinfo/CateidCateTitlePair";
	private static HashMap<Integer, String> CateId2Title = 
			new HashMap<Integer, String>();
	private static boolean id2titInited = false;
	private static void InitId2Tit() {
		// TODO Auto-generated method stub
		if(id2titInited == true)
			return;
		BufferedReader br = uFunc.getBufferedReader(CategoryTitleFile);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int cateid = Integer.parseInt(ss[0]);
				
				if(CateId2Title.containsKey(cateid))
				{
					info = "cateid repeated:" + cateid + "\t" + ss[1] 
							+ ";" + CateId2Title.get(cateid);
					uFunc.Alert(true, i , info);
				}
				CateId2Title.put(cateid, ss[1]);
			}
			System.out.println("category id2title map inited, size:" + 
					CateId2Title.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		id2titInited = true;
	}
}
