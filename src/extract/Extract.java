package extract;

import java.io.File;

import normalization.PredExtraction;

import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

import dumps.InfoboxIdList;
import tools.uFunc;

public class Extract{

	public static String TriplePath = 
			"/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Web/triple";
	public static String testFilesPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/testPages/1";
	public static void main(String [] args)
	{
		InitExtraction();
		for(int i = 1; i <= 1003; i ++)
		{
			long t1 = System.currentTimeMillis();
			ExtractFromLocalFiles(uFunc.WebPagesFolder + "/" + i);
			long t2 = System.currentTimeMillis();
			long sec = (t2 - t1)/1000;
			System.out.println("folder" + i + " cost: " + sec + "sec;"
					+ " total inofboxNr:" + InfoboxNr);
		}
		uFunc.addFile(triples, TriplePath);
		//uFunc.outputCountStrings(uFunc.InfoFolder + "/predicate/dotCount");
	}


	static int fileNr = 0;
	private static int testFileNr = 0;
	private static int InfoboxNr = 0;
	private static void ExtractFromLocalFiles(String path) {
		File folder = new File(path);
		if(folder.isDirectory() == false)
		{
			System.out.println(folder + " not a folder");
			return;
		}
		
		for(File file : folder.listFiles())
		{
			int pageid = Integer.parseInt(file.getName().substring(
					0, file.getName().indexOf("_")));
			
			//if(pageid != 1001309)continue;
			
			fileNr ++;
			if(testFileNr > 0 && fileNr > testFileNr)
				break;
			if(InfoboxIdList.InfoboxIdList.containsKey(pageid))
			{
				//System.out.println("\t pageid:" + pageid);
				ExtractTriplefromOneFile(pageid, file.getAbsolutePath());
				InfoboxNr  ++;
				if(InfoboxNr % 100 == 0)
				{
					uFunc.addFile(triples, TriplePath);
					triples = "";
				}
			}
		}
	}


	private static void InitExtraction() {
		// TODO Auto-generated method stub
		InfoboxIdList.LoadInfoboxNameList();
		uFunc.deleteFile(TriplePath);
		InfoboxNr = 0;
		fileNr = 0;
		uFunc.deleteFile(uFunc.InfoFolder + "/FailedExtractPages");
		uFunc.deleteFile(PredExtraction.UpperTitleFeaturePath);
		uFunc.deleteFile(PredExtraction.LinkFeaturePath);
	}


	public static String triples = "";
	public static String ExtractTriplefromOneFile(int pageid, String Path)
	{
		Parser pageParser ;
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
			triples += pageNode.GetTriples();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Extract.java:" + Path);
			e.printStackTrace();
		}
		return null;
	}
}
