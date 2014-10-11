package extract.predicatetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import extract.pageinfo.myPredicateAvg;
import tools.uFunc;

public class GenerateTestCase {
	private static String c = "GenerateTestCase";
	private static String info = "";

	public static void start(){
		uFunc.AlertPath = "data/info/predicate/GenerateTestCase.info";
		String folder = "/home/hanzhe/Public/result_hz/wiki_count2/";
		
		// ("id1 \t id2")
		HashMap<String, Boolean> TagedPair = 
				new HashMap<String, Boolean>();
		LoadTagedpair(TagedPair, "data/predicate/PredMarkedData");
		
		HashMap<Integer, myPredicateAvg> predicatesMap =
				new HashMap<Integer, myPredicateAvg>();
		int MaxId = LoadPredAvg(predicatesMap, folder + "PredicateAvg");
		
		info = "input next size of pair you want to label."
				+ " input 0 for end. Program will save the file "
				+ "after finished this task.\ntarget size:";
		System.out.println(info);
		Scanner sc = new Scanner(System.in);
		int nextSize = sc.nextInt();
		int curNr = 0;
		long startTime = System.currentTimeMillis();
		Random rand = new Random();
		while(curNr < nextSize)
		{
			int id1 = rand.nextInt(MaxId + 1);
			int id2;
			while(true){
				id2 = rand.nextInt(MaxId + 1);
				if(id2 != id1)
					break;
			}
			if(id1 > id2){
				int tmp = id1;
				id1 = id2;
				id2 = tmp;
			}
			String key1 = id1 + "\t" + id2;
			if(TagedPair.containsKey(key1))
				continue;
			else{
				boolean isSim = false;
				System.out.println(predicatesMap.get(id1).GetHintInfo() 
						+ "\n" + predicatesMap.get(id2).GetHintInfo());
				String feedback = sc.nextLine();
				if(feedback.equals("1"))
					isSim = true;
				TagedPair.put(key1, isSim);
				curNr ++;
			}
		}
		sc.close();
		info = "total cost:" + uFunc.GetTime(System.currentTimeMillis() - startTime)
				+ "\t" + " per cost:" + (System.currentTimeMillis() - startTime)/nextSize 
				+ "minsec";
		uFunc.Alert(true, c, info);
		uFunc.AlertClose();
	}

	private static int LoadPredAvg(
			HashMap<Integer, myPredicateAvg> predicateAvg, String path) {
		// TODO Auto-generated method stub
		String oneLine = "";
		int MaxId = 0;
		BufferedReader br = uFunc.getBufferedReader(path);
		try {
			while((oneLine = br.readLine()) != null)
			{
				if(oneLine.equals(""))
					continue;
				int id = 0;
				String Content;
				if(oneLine.startsWith("id:")){
					id = Integer.parseInt(oneLine.substring(oneLine.indexOf(":") + 1));
					if(id > MaxId)
						MaxId = id;
					if(predicateAvg.containsKey(id)){
						System.out.println("predicateAvg content init error:" + oneLine);
						return MaxId;
					}
					oneLine = br.readLine();
					if(oneLine.startsWith("Content:")){
						Content = oneLine.substring(oneLine.indexOf(":") + 1);
						myPredicateAvg one = new myPredicateAvg(Content, id);
						one.CompleteInfo(br);
						predicateAvg.put(id, one);
					}
					else{
						System.out.println("predicateAvg content init error:" + oneLine);
						return MaxId ;
					}
				}
				else{
					System.out.println("predicateAvg id init error:" + oneLine);
					return MaxId;
				}
				
			}
			info = "predicateAvg map size:" + predicateAvg.size();
			uFunc.Alert(true, c, info);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MaxId;
	}

	private static void LoadTagedpair(HashMap<String, Boolean> tagedPair,
			String path) {
		BufferedReader br = uFunc.getBufferedReader(path);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 3)
				{
					info = "marked data wrong:" + oneLine; 
					uFunc.Alert(true, c, info);
					continue;
				}
				boolean isSim = Boolean.parseBoolean(ss[2]);
				String key1 = ss[0] + "\t" + ss[1];
				String key2 = ss[1] + "\t" + ss[0];
				if(tagedPair.containsKey(key2))
				{
					uFunc.Alert(true, c, "pair not formal:" + key2);
					continue;
				}
				if(tagedPair.containsKey(key1))
				{
					boolean pre = tagedPair.get(key1);
					if(pre != isSim)
						uFunc.Alert(true, c, "pair contradict:" + key1);
					continue;
				}
				tagedPair.put(key1, isSim);
			}
			uFunc.Alert(true, c, "Taged data pair Nr:" + tagedPair.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
