package extract.category;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import database.Category;
import database.Zhwiki;
import tools.uFunc;

public class Survey {

	static String cateFolder = "/home/hanzhe/Public/result_hz/zhwiki/Category/";
	public static void main(String [] args)
	{
		CateLevel();
		//CateFreq();
	}

	/**
	 * level 1 : 1
	 * level 2 : 22
	 * level 3 : 589 
	 * level 4 : 5011 
	 * level 5 : 19981 
	 * level 6 : 76056 
	 * level 7 : 158638 
	 * level 8 : 168724 
	 * level 9 : 172013 
	 * level 10 : 173964 
	 * level 11 : 174081 
	 * level 12 : 174093 
	 * 
	 */
	static int MaxL = 0;
	public static void CateLevel() {
		// TODO Auto-generated method stub
		Category.GetName(4086834);
		String cateid;
		// 幫助	4086834
		cateid = "4086834";
		HashMap<String, Integer> helpCate = 
				new HashMap<String, Integer>();
		AddAllCate(cateid, helpCate);
		System.out.println("helpCate size:" + helpCate.size());
		
		cateid = "254517";
		HashMap<String, Integer> CateLevel = 
				new HashMap<String, Integer>();
		CateLevel.put(cateid, 1);
		int level = 1;
		int MaxLevel = 3;
		AddAllCate(MaxLevel, cateid, level, CateLevel, helpCate);
		System.out.println("catelevel(" + MaxLevel + ") " +
				"size:" + CateLevel.size());
		String srcFile = cateFolder + "Catelevel.tmp";
		uFunc.SaveHashMap(CateLevel, srcFile);
		String targetFile = cateFolder + "Catelevel";
		AddCateName(srcFile, targetFile );
		uFunc.deleteFile(srcFile);
		// 97
		System.out.println("MaxLevel:" + MaxL);
	}

	private static void AddAllCate(int MaxLevel, 
			String cateid, int level,
			HashMap<String, Integer> cateLevel,
			HashMap<String, Integer> helpCate) {
		// TODO Auto-generated method stub
		if(level > MaxL)
			MaxL = level;
		if(level >= MaxLevel)
			return;
		HashMap<String, Integer> nextLevel = 
				new HashMap<String, Integer>();
		Iterator<Entry<String, Integer>> it = 
				cateLevel.entrySet().iterator();
		while((it.hasNext()))
		{
			Entry<String, Integer> next = it.next();
			//System.out.println(next.getKey() + "\t" + next.getValue());
			if(next.getValue() == level)
			{
				int id = Integer.parseInt(next.getKey());
				if(Category.GetSubCates(id) == null)
				{
					continue;
				}
				for(String subCate : Category.GetSubCates(id).split(","))
				{
					if(cateLevel.containsKey(subCate) == false &&
							helpCate.containsKey(subCate) == false)
						nextLevel.put(subCate, level + 1);
				}
			}
		}
		if(nextLevel.size() == 0)
			return;
		cateLevel.putAll(nextLevel);
		System.out.println("level:" + level + "\t" + "size:" + cateLevel.size());
		AddAllCate(MaxLevel, cateid, level + 1, cateLevel, helpCate);
		/*
		if(helpCate.containsKey(cateid) || 
				cateLevel.containsKey(cateid))
			return;
		cateLevel.put(cateid, level);
		if(level + 1 > MaxLevel)
			return;
		int id = Integer.parseInt(cateid);
		if(Category.GetSubCates(id) == null)
			return;
		for(String subCate : Category.GetSubCates(id).split(","))
		{
			AddAllCate(MaxLevel, subCate, 
					level + 1, cateLevel, helpCate);
		}
		*/
	}

	private static void AddAllCate(String cateid,
			HashMap<String, Integer> helpCate) {
		// TODO Auto-generated method stub
		if(helpCate.containsKey(cateid))
			return;
		helpCate.put(cateid + "", 0);
		int id = Integer.parseInt(cateid);
		if(Category.GetSubCates(id) == null)
			return;
		for(String subcate : Category.GetSubCates(id).split(","))
		{
			AddAllCate(subcate, helpCate);
		}
	}

	public static void CateFreq(){
		String cpPair = "data/pageinfo/CateidPageidPair";
		BufferedReader br = uFunc.getBufferedReader(
				cpPair);
		String oneLine = "";
		HashMap<String, Integer> cateFreq = 
				new HashMap<String, Integer>();
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int freq = 1;
				if(cateFreq.containsKey(ss[0]) == true)
					freq += cateFreq.remove(ss[0]);
				cateFreq.put(ss[0], freq);
			}
			System.out.println("catefreq size:" + 
					cateFreq.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		uFunc.SaveHashMap(cateFreq, cateFolder + "catefreq.tmp");
		String srcFile = cateFolder + "catefreq.tmp";
		String targetFile = cateFolder + "cateFreq";
		AddCateName(srcFile, targetFile);
	}

	private static void AddCateName(String srcFile, String targetFile) {
		// TODO Auto-generated method stub
		uFunc.deleteFile(targetFile);
		BufferedReader br = uFunc.getBufferedReader(srcFile);
		int outNr = 0;
		String output = "";
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				String cateName = 
						Category.GetName(Integer.parseInt(ss[0]));
				output += oneLine + "\t" + cateName + "\n";
				outNr ++;
				if(outNr % 1000 == 0)
				{
					uFunc.addFile(output, targetFile);
					output = "";
				}
			}
			uFunc.addFile(output, targetFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
