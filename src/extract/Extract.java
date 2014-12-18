package extract;

import java.io.File;

import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;

import database.Entity;
import database.Infobox;
import database.RediPage;
import tools.uFunc;
/**
 * 1. extract in each folder: ExtractFromLocalFiles
 * 2. extract in each file: ExtractTriplefromOneFile
 * 3. parse page.html to generate class.PageNode 
 * 		(the following process by PageNode.java)
 * 4. PageNode: parse PageNode to find each Infobox (class.InfoboxNode)
 * 		(the following process by InfoboxNode.java)
 * 5. InfoboxNode: parse InfoboxNode to find each line (each TR tag)
 * 		(the following process by TRprocessor.java)
 * 6. TRprocessor: filtering the wrong TR tag and extract UpperTitle and other info,
 * 		(the following TR->triple processed by GeneratorDistributor.java)
 * 7. GeneratorDistributor: collect triple info and distribute to each generator(
 * 	tripleGenerator, predicateTableGenerator, tripleMysqlGenerator, ...)
 * @author hanzhe
 *
 */
public class Extract{
	private static String i = "Extract";

	public static String TripleFolder = 
			"/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Web/";
	public static String PredicateIdPath = 
			"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId";
	
	public static String TriplePath = TripleFolder + "Triple";
	public static String InfoboxIdListPath;
	private static String info = "";
	public static void main(String [] args)
	{
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
		for(int i = 1; i <= 1073; i ++)
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
			if(RediPage.getTargetPageid(pageid) > 0)
				continue;
			if(Infobox.isNotInfobox(pageid))
				continue;
			String titles = uFunc.Simplify(Entity.getTitle(pageid));
			if(titles == null)
			{
				info = "pagetitle missed in Entity:" + pageid;
				uFunc.Alert(true, i, info);
				continue;
			}
			boolean jump = false;
			for(String title : titles.split("####"))
			{
				if(title != null && (title.contains("列表") 
						|| title.contains("年表") 
						|| title.endsWith("时间表")
						|| title.endsWith("系表")))
				{
					jump = true;
					break;
				}
			}
			if(jump)
				continue;
			////////////////////////////////
			//if(pageid != 481 && pageid != 105461)continue;
			////////////////////////////////
			fileNr ++;
			String result = "";
			boolean Alert = true;
			result  = ExtractTriplefromOneFile(pageid, file.getAbsolutePath(), Alert);
			
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
		{
			uFunc.Alert(i, pageid + " file not exist locally, extracting from web...");
			Path = "http://zh.wikipedia.org/wiki?curid=" + pageid;
		}
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
