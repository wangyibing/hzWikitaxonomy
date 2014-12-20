package extract.predicate;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

import com.tag.myPredicateAvg;

import tools.uFunc;

public class GenerateTestCase {
	
	private static String c = "GenerateTestCase";
	private static String info = "";

	public static void start(){
		uFunc.AlertPath = "data/info/predicate/GenerateTestCase.info";
		uFunc.deleteFile(uFunc.AlertPath);
		String folder = "/home/hanzhe/Public/result_hz/wiki_count2/predicate/";
		
		// ("id1 \t id2")
		HashMap<String, Boolean> TagedPair = 
				new HashMap<String, Boolean>();
		LoadTagedpair(TagedPair, folder + "PredMarkedData");
		
		HashMap<Integer, myPredicateAvg> predicatesMap =
				new HashMap<Integer, myPredicateAvg>();
		int MaxId = LoadPredAvg(predicatesMap, folder + "PredicateAvg");
		
		info = "input next size of pair you want to label.\n"
				+ " input 0 for end. \nProgram will save the file "
				+ "after finished this task.\ntarget size:";
		System.out.println(info);
		Scanner sc = new Scanner(System.in);
		int nextSize = sc.nextInt();
		sc.nextLine();
		int curNr = 0;
		int JumpedNr = 0;
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
				myPredicateAvg p1 = predicatesMap.get(id1);
				myPredicateAvg p2 = predicatesMap.get(id2);
				if(p1 == null || p2 == null ||
						HasSimilarChar(p1.Content, p2.Content) == false)
				{
					JumpedNr ++;
					continue;
				}
				boolean isSim = false;
				info = "JumpedNr:" + JumpedNr + "\n"
						+ p1.GetHintInfo() + "\n" + p2.GetHintInfo()
						+ "\n is same?:\n";
				System.out.println(info);
				JumpedNr = 0;
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
		SaveMap(TagedPair, folder + "PredMarkedData");
		uFunc.Alert(true, c, info);
		uFunc.AlertClose();
	}

	private static boolean HasSimilarChar(String con1, String con2) {
		// TODO Auto-generated method stub
		if(con1 == null || con2 == null)
			return false;
		con1 = uFunc.Simplify(con1.toLowerCase());
		con2 = uFunc.Simplify(con2.toLowerCase());
		char [] c1 = con1.toCharArray();
		char [] c2 = con2.toCharArray();
		int simNr = 0;
		for(int i = 0 ; i < c1.length; i ++)
			for(int j = 0; j < c2.length; j ++)
			{
				if(c1[i] == c2[j])
				{
					if(uFunc.hasChineseCharactor(c1[i] + ""))
						simNr += 5;
					else simNr ++;
					break;
				}
				if(uFunc.hasChineseCharactor(c1[i] + "") &&
						uFunc.hasChineseCharactor(c2[j] + ""))
				{
					String s1 = uFunc.GetPinYin(c1[i] + "");
					String s2 = uFunc.GetPinYin(c2[j] + "");
					if(s1 != null && s2 != null && s1.equals(s2))
					{
						simNr += 3;
						break;
					}
				}
			}
		return simNr *(c1.length + c2.length) >
			6 * c1.length * c2.length;
	}

	private static void SaveMap(HashMap<String, Boolean> tagedPair,
			String path) {
		// TODO Auto-generated method stub
		uFunc.deleteFile(path + ".lastBackUp");
		uFunc.AddOneFile(path, path + ".lastBackUp");
		uFunc.deleteFile(path);
		
		Iterator<Entry<String, Boolean>> it = 
				tagedPair.entrySet().iterator();
		StringBuffer sb = new StringBuffer();
		int recordNr = 0;
		while(it.hasNext())
		{
			Entry<String, Boolean> next = it.next();
			sb.append(next.getKey() + "\t" + next.getValue() + "\n");
			recordNr ++;
			if(recordNr % 1000 == 0)
			{
				uFunc.addFile(sb.toString(), path);
				sb.setLength(0);
			}
		}
		uFunc.addFile(sb.toString(), path);
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
					System.out.println("predicateAvg id init error:\n\t\"" + oneLine
							+ "\"");
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
		if(br == null)
			return;
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
