package normalization;


import normalization.predicate.UpperTitleExtract;
import normalization.predicate.linkExtract;

import org.htmlparser.util.NodeList;

import tools.uFunc;

public class PredExtraction {
	public static String UpperTitleFeaturePath = 
			"/home/hanzhe/Public/result_hz/zhwiki/info/predicate/UpperTitle";
	public static String LinkFeaturePath = 
			"/home/hanzhe/Public/result_hz/zhwiki/info/predicate/LinkInfo";

	public static void Extract(int pageid, NodeList infobox)
	{
		String upperInfo = UpperTitleExtract.Extract(pageid, infobox);
		if(upperInfo != null && upperInfo.equals("") == false)
			uFunc.addFile(upperInfo, UpperTitleFeaturePath);
		
		String linkInfo = linkExtract.Extract(pageid, infobox);
		if(linkInfo != null && linkInfo.equals("") == false)
			uFunc.addFile(linkInfo, LinkFeaturePath);

	}
}
