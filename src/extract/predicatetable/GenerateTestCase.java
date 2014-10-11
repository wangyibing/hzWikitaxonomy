package extract.predicatetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import extract.pageinfo.myPredicateAvg;
import tools.uFunc;

public class GenerateTestCase {
	private static String c = "GenerateTestCase";
	private static String info = "";

	public static void start(){
		uFunc.AlertPath = "data/info/predicate/GenerateTestCase.info";
		String folder = "";
		
		// ("id1 \t id2")
		HashMap<String, Boolean> TagedPair = 
				new HashMap<String, Boolean>();
		LoadTagedpair(TagedPair, "data/predicate/PredMarkedData");
		
		HashMap<Integer, myPredicateAvg> predicatesMap =
				new HashMap<Integer, myPredicateAvg>();
		LoadPredAvg(predicatesMap, );
		long startTime = System.currentTimeMillis();
		info = "input next size of pair you want to label."
				+ " input 0 for end. Program will save the file "
				+ "after finished this task.\ntarget size:";
		System.out.println(info);
		Scanner sc = new Scanner(System.in);
		int nextSize = sc.nextInt();
		int curNr = 0;
		while(curNr < nextSize)
		{
			
		}
		
		uFunc.AlertClose();
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
