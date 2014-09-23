package normalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

import tools.predicate;
import tools.uFunc;
import extract.pageinfo.myPredicate;

public class PredNorm {
	public static void main(String [] args)
	{
		Normalize("/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId2",
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId.Normed");
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
		Scanner sc = new Scanner(System.in);
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
				
				if(contUT.equals("分布地区"))
				if(freqP < 5 && freqU > 10)
				{
					info = "contP:" + freqP + " \"" + contP + "\"\t" + 
							"contUT:" + freqU + " \"" + contUT + "\"\n";
					info += pred.toString();
					System.out.println(info);
					sc.nextLine();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
