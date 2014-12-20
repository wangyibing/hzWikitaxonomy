package word2vec;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import database.WordFreq;
import tools.QsortPair;
import tools.uFunc;

public class PredicateDist_text {
	public static void main(String [] args)
	{
		GetWordsInContent("/home/hanzhe/Public/result_hz/zhwiki/info/predicate/PredDistribution",
				"/home/hanzhe/Public/result_hz/zhwiki/info/predicate/PredDistWordFreq");
	}

	public static void GetWordsInContent(String srcFile, String tgFile){
		BufferedReader br = uFunc.getBufferedReader(srcFile);
		String oneLine = "";
		HashMap<String, Integer> predicateAsWordFreqDistribute = 
				new HashMap<String, Integer>();
		HashMap<String, Integer> predicateFreq = 
				new HashMap<String, Integer>();
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				String word = ss[0];
				int wordfreq = WordFreq.Get(ss[0]);
				if(wordfreq > 0)
					predicateFreq.put(word, wordfreq);
				int freq = 1;
				if(predicateAsWordFreqDistribute.containsKey(wordfreq + "" ))
					freq += predicateAsWordFreqDistribute.remove(wordfreq + "");
				predicateAsWordFreqDistribute.put(wordfreq + "" , freq);
			}
			uFunc.SaveHashMap(predicateFreq, tgFile);
			uFunc.SaveHashMap(predicateAsWordFreqDistribute, tgFile + "Dist");
			QsortPair.SortPair( tgFile + "Dist",  tgFile + "Dist.sorted", false, false, 0);
			uFunc.deleteFile( tgFile + "Dist");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
