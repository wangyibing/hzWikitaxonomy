package database;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;





public class Entity {
	public static String id2titFile = 
			"data/pageinfo/entity/EntityId2Titles";
	public static String tit2idFile = 
			"data/pageinfo/entity/EntityTitle2Id";
	public static String FreqFile = 
			"data/pageinfo/entity/EntityFreq";
	public static int getFreq(int pageid)
	{
		if(FreqMapInited == false)
			initFreqMap();
		if(FreqMap.containsKey(pageid))
			return FreqMap.get(pageid);
		return 0;
	}

	public static int getId(String title)
	{
		if(Tit2IdInited == false)
			initTitIdMap();
		if(Tit2IdMap.containsKey(title))
			return Tit2IdMap.get(title);
		return 0;
	}
	
	public static String getTitles(int pageid)
	{
		if(Id2TitsInited == false)
			initId2TitsMap();
		if(Id2TitsMap.containsKey(pageid))
			return Id2TitsMap.get(pageid);
		return null;
	}

	public static HashMap<String, Integer> Str2Id = 
		new HashMap<String, Integer>();
	
	
	private static HashMap<Integer, Integer> FreqMap = 
			new HashMap<Integer, Integer>();
	private static boolean FreqMapInited = false;
	private static void initFreqMap() {
		// TODO Auto-generated method stub
		if(FreqMapInited)
			return;
		BufferedReader br = uFunc.getBufferedReader(FreqFile);
		String oneLine = "";
		try {
			while( (oneLine = br.readLine()) != null)
			{
				if(oneLine.equals(""))
					continue;
				String [] ss = oneLine.split("\t");
				if(ss.length < 2)
				{
					System.out.println("error entity freq:" + oneLine);
					continue;
				}
				int pageid = Integer.parseInt(ss[0]);
				int freq = Integer.parseInt(ss[1]);
				FreqMap.put(pageid, freq);
			}
			System.out.println("entity freq size:" + FreqMap.size());
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FreqMapInited = true;
	}

	// <13, 数学;数学科学;数学系;>
	private static HashMap<Integer, String> Id2TitsMap = 
			new HashMap<Integer, String>();
	private static boolean Id2TitsInited = false;
	private static void initId2TitsMap() {
		if(Id2TitsInited == true)
			return;
		String oneLine = "";
		try{
			BufferedReader reader = uFunc.getBufferedReader(
					id2titFile);
			while((oneLine = reader.readLine())!= null){
				String [] ts = oneLine.split("\t");
				if(ts.length < 2)
				{
					System.out.println("id2tit error:" + oneLine);
					continue;
				}
				int Eid = Integer.parseInt(ts[0]);
				String titles = ts[1];
				Id2TitsMap.put(Eid, titles);
			}
			System.out.println("Id2TitsMap.size:"+ 
					Id2TitsMap.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		Id2TitsInited = true;
	}


	// <数学, 13>
	private static HashMap<String, Integer> Tit2IdMap = 
			new HashMap<String, Integer>();
	private static boolean Tit2IdInited = false;
	private static void initTitIdMap() {
		if(Tit2IdInited == true)
			return;
		String oneLine = "";
		try{
			BufferedReader reader = uFunc.getBufferedReader(tit2idFile);
			while((oneLine = reader.readLine())!= null){
				String [] ts = oneLine.split("\t");
				if(ts.length < 2)
				{
					System.out.println("tit2id error:" + oneLine);
					continue;
				}
				int Eid = Integer.parseInt(ts[1]);
				String title = ts[0];
				if(title == null || title.equals(""))
					continue;
				// new method ensure that there is no chance of one title linking
				// to different pageIds
				Tit2IdMap.put(title, Eid);
			}
			System.out.println("TitsIdMap.size:"+ 
					Tit2IdMap.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		Tit2IdInited = true;
	}

}
