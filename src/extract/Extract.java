package extract;

import java.io.File;

import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

import database.InfoboxIdList;
import database.Page;
import tools.uFunc;

public class Extract{
	private static String i = "Extract";

	public static String TripleFolder = 
			"/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Web/";
	public static String TriplePath = TripleFolder + "Triple";
	public static String InfoboxIdListPath;
	public static String PredicateIdPath = 
			"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId";
	private static String info = "";
	public static void main(String [] args)
	{
		InfoboxIdList.LoadInfoboxNameList();
		TriplePath = TripleFolder + "Triple";
		InfoboxIdListPath = TripleFolder + "InfoboxIdList";
		uFunc.deleteFile(TriplePath);
		uFunc.deleteFile(InfoboxIdListPath);
		InfoboxNr = 0;
		fileNr = 0;
		uFunc.deleteFile(uFunc.InfoFolder + "/FailedExtractPages");
		uFunc.deleteFile(PredicateIdPath);
		uFunc.AlertPath = "data/info/Extraction";
		long start = System.currentTimeMillis();
		long t1 = System.currentTimeMillis();
		for(int i = 1; i <= 1003; i ++)
		{
			ExtractFromLocalFiles(uFunc.WebPagesFolder + "/" + i);
			if(i % 10 == 0)
			{
				long sec = (System.currentTimeMillis() - t1);
				t1 = System.currentTimeMillis();
				info = ("folder" + i + " cost: " + uFunc.GetTime(sec) + ";"
						+ " total inofboxNr:" + InfoboxNr );
				uFunc.Alert("Extract", info);
			}
		}
		info = "total time:" + (System.currentTimeMillis() - start)/60000 + " min";
		uFunc.Alert(true, i, info);
		uFunc.AlertClose();
		uFunc.addFile(triples, TriplePath);
		uFunc.SaveHashMap(PageNode.classAttrName, TripleFolder + "ClassAttrName");
		//uFunc.outputCountStrings(uFunc.InfoFolder + "/predicate/dotCount");
	}


	static int fileNr = 0;
	private static int InfoboxNr = 0;
	private static void ExtractFromLocalFiles(String path) {
		File folder = new File(path);
		if(folder.isDirectory() == false)
		{
			uFunc.Alert(i, folder + " not a folder");
			return;
		}
		String idList = "";
		for(File file : folder.listFiles())
		{
			int pageid = Integer.parseInt(file.getName().substring(
					0, file.getName().indexOf("_")));
			String title = uFunc.Simplify(Page.getTitles(pageid));
			if(title != null && (title.contains("列表") 
					|| title.contains("年表") 
					|| title.contains("时间表")))
				continue;
			////////////////////////////////
			//if(pageid != 149093)continue;
			////////////////////////////////
			fileNr ++;
			String result = "";
			boolean Alert = true;
			result  = ExtractTriplefromOneFile(
					pageid, file.getAbsolutePath(), Alert);
			if(result != null && result.equals("") == false)
			{
				triples += result;
				InfoboxNr  ++;
				if(InfoboxNr % 100 == 0)
				{
					uFunc.addFile(triples, TriplePath);
					triples = "";
				}
				idList += pageid + "\n";
			}
		}
		uFunc.addFile(idList, InfoboxIdListPath);
	}


	public static String triples = "";
	public static String ExtractTriplefromOneFile(int pageid, String Path, boolean alert)
	{
		Parser pageParser ;
		String PageTriples = "";
		File file = new File(Path);
		// for empty file, may be extract null
		if(file.exists() == false || file.length() < 10)
			Path = "http://zh.wikipedia.org/wiki?curid=" + pageid;
		try {
			try{
				pageParser = new Parser(Path);
			}catch(Exception e1){
				pageParser = new Parser("http://zh.wikipedia.org/wiki?curid=" + pageid);
			}
			pageParser.setEncoding("UTF-8");
			NodeList pageNodeList = pageParser.parse(null);
			
			//System.out.println("Extract.java:pageNodeList:\"" + pageNodeList.toHtml() + "\"" + pageParser.getURL());
			PageNode pageNode = new PageNode(pageid, pageNodeList);
			PageTriples = pageNode.GetTriples(alert);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			uFunc.Alert(i, Path);
			e.printStackTrace();
		}
		return PageTriples;
	}
}
