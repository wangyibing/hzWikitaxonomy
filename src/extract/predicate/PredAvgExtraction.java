package extract.predicate;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import com.tag.myPredicate;
import com.tag.myPredicateAvg;

import tools.uFunc;

public class PredAvgExtraction {
	private static String info;
	private static String c = "PredAvgExtraction";
	

	public static void main(String [] args)
	{
		String folder = "/home/hanzhe/Public/result_hz/wiki_count2/predicate/";
		Extract(folder + "predicateId.Normed", folder + "PredicateAvg");
	}
	static HashMap<String, Integer> Cont2PredseqMap = 
			new HashMap<String, Integer>();
	static myPredicateAvg[] predicates = new myPredicateAvg[30000]; 
	static int predSeq = 0;
	public static void Extract(String predicateId2_Normal, String tar)
	{
		BufferedReader br = uFunc.getBufferedReader(predicateId2_Normal);
		String oneLine = "";
		int predicateNr = 0;
		int lastPageId = 0;
		int pageNr = 0;
		try {
			long starter = System.currentTimeMillis();
			long time = starter;
			while((oneLine = br.readLine()) != null)
			{
				long predId = Long.parseLong(oneLine);
				int pageId = Integer.parseInt(
						oneLine.substring(0, oneLine.length() - 3));
				if(pageId != lastPageId)
				{
					pageNr ++;
					lastPageId = pageId;
					if(pageNr % 10000 == 0)
					{
						info = pageNr + " page passed" + "\tpredicateNr:" + 
								predicateNr + " \tcost:" + 
								(System.currentTimeMillis() - time)/1000 + "sec ";
						uFunc.Alert(true, c, info);
						//break;
					}
				}
				myPredicate pred = new myPredicate(predId, pageId);
				pred.CompleteInfo(br);
				if(pred.Content == null)
					continue;
				predicateNr ++;
				//info = pred.Content + " " + pred.Pageid;
				//System.out.println(info);
				if(Cont2PredseqMap.containsKey(pred.Content))
				{
					int id = Cont2PredseqMap.get(pred.Content);
					predicates[id].addOnePredicate(pred);
				}
				else{
					predSeq ++;
					Cont2PredseqMap.put(pred.Content, predSeq);
					predicates[predSeq] = new myPredicateAvg(
							pred.Content, predSeq);
					// do not get word2vec vector
					/*
					if(predicates[predSeq].vectorW2V == null)
					{
						info = predSeq + "\t" + pred.Content + "\t" + 
								" word2vec is null";
						uFunc.Alert(true, c, info);
					}*/
					predicates[predSeq].addOnePredicate(pred);
				}
			}
			System.out.println("\ntotal predicate nr:" + predSeq);
			SavePredicateAvg(tar);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void SavePredicateAvg(String tar) {
		// TODO Auto-generated method stub
		uFunc.deleteFile(tar);
		String output = "";
		int outNr = 0;
		for(int i = 1 ; i <= predSeq ; i ++)
		{
			output += predicates[i].toString() + "\n\n";
			outNr ++;
			if(outNr % 50 == 0)
			{
				uFunc.addFile(output, tar);
				output = "";
			}
		}
		uFunc.addFile(output, tar);
	}
}
