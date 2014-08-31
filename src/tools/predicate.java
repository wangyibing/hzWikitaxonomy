package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import extract.Extract;

public class predicate {

	public static String PrediDistributionPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/info/predicate/PredDistribution";
	private static HashMap<String, Integer> Predicate = 
			new HashMap<String, Integer>();
	private static boolean PredicateInited = false;
	
	public static void main(String [] args)
	{
		//FindDuplicatePredi(Extract.TriplePath);
		PredDistribution(Extract.TriplePath + "2");
	}

	public static boolean isPred(String content)
	{
		Init();
		if(Predicate.containsKey(content))
			return true;
		return false;
	}
	
	private static void Init() {
		// TODO Auto-generated method stub
		if(PredicateInited == false)
		{
			LoadPredicate();
		}
	}

	private static void LoadPredicate() {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(PrediDistributionPath);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String pred = oneLine.split("\t")[0];
				int freq = Integer.parseInt(oneLine.split("\t")[1]);
				pred = pred.replaceAll("(?m)^(•|\\s)+", "")
						.replaceAll("((?m)^\\[)|((?m)\\]$)|((?m)^ +)|((?m) +$)|", "");
				Predicate.put(pred, freq);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("predicate size:" + Predicate.size());
		PredicateInited = true;
	}

	private static void PredDistribution(String triplePath) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(triplePath);
		String oneLine = "";
		HashMap<String, Integer> PredFreq = new HashMap<String, Integer>();
		
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 3)
					continue;
				int freq = 1;
				ss[1] = uFunc.Simplify(uFunc.ReplaceBoundSpace(ss[1]));
				if(ss[1].length() > 30)
					continue;
				if(PredFreq.containsKey(ss[1]))
					freq += PredFreq.remove(ss[1]);
				//if(ss[1].length() > 15)
				//	System.out.println(oneLine);
				PredFreq.put(ss[1], freq);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		uFunc.SaveHashMap(PredFreq, PrediDistributionPath);
	}

	public static void FindDuplicatePredi(String triplePath) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(triplePath);
		int pageid = 0;
		String lastPred = "";
		HashMap<String, Integer> PagePred = new HashMap<String, Integer>();
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 3)
				{
					System.out.println("not stand triple:" + oneLine);
				}
				if(ss[0].startsWith("null"))
					ss[0] = ss[0].replaceAll("null", "");
				int id = Integer.parseInt(ss[0]);
				if(id != pageid)
				{
					PagePred.clear();
					pageid = id;
				}
				String pred = uFunc.UnifiedWord(ss[1]);
				if(pred.equals(lastPred))
					continue;
				if(PagePred.containsKey(pred))
				{
					System.out.println("predicate duplicate: pageid:" + id + "\t" + pred + "\t" + PagePred.size());
				}
				
				PagePred.put(pred, 0);
				lastPred = pred;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
