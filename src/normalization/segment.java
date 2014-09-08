package normalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import database.Entity;
import tools.predicate;
import tools.uFunc;

public class segment {
	public static String ANSJSegPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/data2/ANSJ_segment";

	public static String WordsListPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/info/WordsList";
	

	public static void main(String [] args)
	{
		//ANSJsegment(TextMerge.Data2TextPath, ANSJSegPath);
		//CalculateWords(ANSJSegPath, WordsListPath);
		//CalculateEntityFreq(ANSJSegPath);
		CalculatePrediFreq(ANSJSegPath);
		
	}

	public static String PrediFreqPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/info/predicate/PrediSegmentFreq";
	public static String PrediDistributionPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/info/predicate/PrediSegmentDistribution";
	private static void CalculatePrediFreq(String aNSJSegPath2) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(aNSJSegPath2);
		String oneLine = "";
		HashMap<String, Integer> PrediFreq = 
				new HashMap<String, Integer>();
		
		try {
			int LineNr = 0;
			while( (oneLine = br.readLine()) != null)
			{
				LineNr ++;
				if(LineNr % 500000 == 0)
					System.out.println(LineNr + " lines parsed"  + 
							";Predi size:" + PrediFreq.size());
				oneLine = uFunc.Simplify(oneLine);
				for(String word : oneLine.split(" |\t"))
				{
					if(predicate.isPred(word))
					{
						int freq = 1;
						if(PrediFreq.containsKey(word) == true)
						{
							freq += PrediFreq.remove(word);
						}
						PrediFreq.put(word, freq);
					}
				}
			}
			System.out.println("total p Nr:" + PrediFreq.size());
			uFunc.SaveHashMap(PrediFreq, PrediFreqPath);
			uFunc.CalcuFreqDistr(PrediFreq, PrediDistributionPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static String EntityFreqPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/info/EntityFreq";
	public static String EntityDistributionPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/info/EntityDistribution";
	public static void CalculateEntityFreq(String aNSJSegPath2) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(aNSJSegPath2);
		String oneLine = "";
		HashMap<String, Integer> EntityFreq = 
				new HashMap<String, Integer>();
		
		try {
			int LineNr = 0;
			while( (oneLine = br.readLine()) != null)
			{
				LineNr ++;
				if(LineNr % 500000 == 0)
					System.out.println(LineNr + " lines parsed"  + 
							";Entity size:" + EntityFreq.size());
				oneLine = uFunc.Simplify(oneLine);
				for(String word : oneLine.split(" |\t"))
				{
					if(Entity.getId(word) > 0)
					{
						System.out.println("segment.java:" + Entity.getId(word) + ":" + word);
						int freq = 1;
						if(EntityFreq.containsKey(word) == true)
						{
							freq += EntityFreq.remove(word);
						}
						EntityFreq.put(word, freq);
					}
				}
			}
			System.out.println("total Entity Nr:" + EntityFreq.size());
			uFunc.SaveHashMap(EntityFreq, EntityFreqPath);
			uFunc.CalcuFreqDistr(EntityFreq, EntityDistributionPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * calculate all the words exist in the training data
	 * @param aNSJSegPath2
	 * @param wordsListPath2 
	 */
	public static void CalculateWords(String aNSJSegPath2, String wordsListPath2) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(aNSJSegPath2);
		String oneLine = "";
		HashMap<String, Integer> WordFreq = 
				new HashMap<String, Integer>();
		
		try {
			int freq;
			int LineNr = 0;
			while( (oneLine = br.readLine()) != null)
			{
				LineNr ++;
				if(LineNr % 500000 == 0)
					System.out.println(LineNr + " lines parsed"  + 
							";word size:" + WordFreq.size());
				oneLine = uFunc.Simplify(oneLine);
				for(String word : oneLine.split(" |\t"))
				{
					freq = 1;
					if(WordFreq.containsKey(word) == true)
					{
						freq += WordFreq.remove(word);
					}
					WordFreq.put(word, freq);
				}
			}
			System.out.println("total word Nr:" + WordFreq.size());
			uFunc.SaveHashMap(WordFreq, wordsListPath2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 12640372 lines
	 * @param path
	 * @param savePath
	 */
	public static void ANSJsegment(String path, String savePath) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(path);
		String oneLine = "";
		String output = "";
		int outNr = 0;
		uFunc.deleteFile(savePath);																																																												
		try {
			while((oneLine = br.readLine())!= null)
			{
				
				oneLine = uFunc.ReplaceBoundSpace(oneLine); 
				List<Term> seg = NlpAnalysis.parse(oneLine);
				String oneOut = "";
				for(Term term : seg)
				{
					oneOut += term.getName() + " ";
				}
				output += oneOut + "\n";
				outNr ++;
				if(outNr % 1000 == 0)
				{
					uFunc.addFile(output, savePath);
					output = "";
					if(outNr % 100000 == 0)
					{
						System.out.println(outNr + " lines passed");
					}
				}																								
			}
			System.out.println(outNr + " lines total");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
