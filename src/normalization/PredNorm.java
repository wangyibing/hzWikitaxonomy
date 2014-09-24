package normalization;

import java.io.BufferedReader;
import java.util.HashMap;

import tools.predicate;
import tools.uFunc;
import extract.pageinfo.myPredicate;

public class PredNorm {
	public static void main(String [] args)
	{
		/*
		Survey("/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId2",
				"/home/hanzhe/Public/result_hz/zhwiki/info/");
				*/
		
		Normalize("/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId2",
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId.Normed");
		
	}

	private static void Survey(String predicate2, String infoFolder) {
		// TODO Auto-generated method stub
		String oneLine = "";
		myPredicate pred;
		BufferedReader br = uFunc.getBufferedReader(predicate2);
		HashMap<String, Integer> UTFreq = 
				new HashMap<String, Integer>();
		HashMap<String, Integer> PairFreq = 
				new HashMap<String, Integer>();
		int pFreqThr = 50;
		int UFreqThr = 1;
		try {
			while((oneLine = br.readLine()) != null)
			{
				long predId = Long.parseLong(oneLine);
				int pageId = Integer.parseInt(
						oneLine.substring(0, oneLine.length() - 3));
				pred = new myPredicate(predId, pageId);
				pred.CompleteInfo(br);
				String contP = pred.Content;
				int freqP = predicate.PredFreq(contP);
				String contUT = pred.UpperTitle;
				int freqU = predicate.PredFreq(contUT);
				if(freqP < pFreqThr && freqU > UFreqThr)
				{
					int freq = 1;
					if(UTFreq.containsKey(contUT))
						freq += UTFreq.remove(contUT);
					UTFreq.put(contUT, freq);
					freq = 1;
					if(PairFreq.containsKey(contUT + "##" + contP))
						freq += PairFreq.remove(contUT + "##" + contP);
					PairFreq.put(contUT + "##" + contP, freq);
				}
			}
			info = "PredUTPairFreq size:" + PairFreq.size() + "\n" + 
					"UTFreq size:" + UTFreq.size();
			System.out.println(info);
			uFunc.SaveHashMap(PairFreq, infoFolder + "PredUTPairFreq");
			uFunc.SaveHashMap(UTFreq, infoFolder + "UTFreq");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String info;

	/**
	 * 1. replace all the predicate with its UpperTitle where the pred's
	 * 		frequency is less than 5 while its UpperTitle's frequency is 
	 * 		more than 10(?)
	 * @param src
	 * @param tar
	 */
	public static void Normalize(String src, String tar)
	{
		String oneLine = "";
		myPredicate pred;
		String output = "";
		int outNr = 0;
		BufferedReader br = uFunc.getBufferedReader(src);
		uFunc.deleteFile(tar);
		try {
			while((oneLine = br.readLine()) != null)
			{
				long predId = Long.parseLong(oneLine);
				int pageId = Integer.parseInt(
						oneLine.substring(0, oneLine.length() - 3));
				pred = new myPredicate(predId, pageId);
				pred.CompleteInfo(br);
				String contP = pred.Content;
				//int freqP = predicate.PredFreq(contP);
				String contUT = pred.UpperTitle;
				//int freqU = predicate.PredFreq(contUT);
				
				if(contUT != null && contUT.equals("分布地区"))
				{
					for(int i = 0 ; i < pred.Objs.size(); i ++)
					{
						pred.Objs.set(i, contP + "##" + pred.Objs.get(i));
					}
					pred.Content = pred.UpperTitle;
				}
				
				output += pred.toString();
				outNr ++;
				if(outNr % 100 == 0)
				{
					uFunc.addFile(output, tar);
					output = "";
				}
				
				/*
				else if(contUT != null &&
						(contUT.equals("概览") || contUT.equals("政府")))
				{
					// 不需要替换
				}
				else if(freqP < 5 && freqU > 10)
				{
					info = "contP:" + freqP + " \"" + contP + "\"\t" + 
							"contUT:" + freqU + " \"" + contUT + "\"\n";
					info += pred.toString();
					System.out.println(info);
					sc.nextLine();
				}
				*/
			}
			uFunc.addFile(output, tar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
