package extract.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import org.htmlparser.Parser;

import database.Entity;
import tools.uFunc;

/**
 * extract all the entity page an save them on 
 * pageid > 1200000
 * @author hz
 *
 */
public class ExtractHtmlPages {

	public static String WebPageFolder = 
			"/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/FileSplit";
	static String EntityIdTitPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/EntityId_unif.txt";
	static String FailureIdTitPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/FailureIdTit.txt";
	//static int RestartPageid = 1126608;
	public static void main(String [] args){
		//ExtractProcess();
		int threadNr = 5;
		AddNewPages(1004, 1, 5);
	}
	private static void AddNewPages(int existFolderNr, int threadK, int threadNr) {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> extractedPage = 
				new HashMap<Integer, Integer>();
		long t1 = System.currentTimeMillis();
		File  folder = null;
		for(int i = 1 ; i <= existFolderNr; i ++)
		{
			folder = new File(uFunc.WebPagesFolder + "/" + i);
			if(folder.isDirectory() == false)
			{
				System.out.println("not a folder:" + folder.getAbsolutePath());
				continue;
			}
			
			for(File f : folder.listFiles())
			{
				String fName = f.getName();
				int pageid = Integer.parseInt(fName.substring(0, fName.indexOf("_")));
				if(extractedPage.containsKey(pageid))
					System.out.println(pageid + " exist twice!" + i);
				extractedPage.put(pageid, 0);
			}
		}
		System.out.println("current page Nr:" + extractedPage.size() + "\t"
				+ "cost:" + (System.currentTimeMillis() - t1));
		
		BufferedReader br = uFunc.getBufferedReader(
				Entity.CanonicalPath_titles);
		String oneLine = "";
		int PageNrSize = 0;
		int newPageNr = folder == null ? 0 : folder.listFiles().length;
		int folderNr = existFolderNr;
		File newFolder = new File(WebPageFolder +"/"+ folderNr);
		if(!(newFolder.exists() && newFolder.isDirectory()))
		{
			System.out.println("folder:" + folderNr + " created");
			newFolder.mkdir();
		}
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss[2].equals("title"))
				{
					PageNrSize ++;
					int pageid = Integer.parseInt(ss[0]);
					if(extractedPage.containsKey(pageid))
						continue;
					else
					{
						if(oneLine.contains("中国") || oneLine.contains("党"))
						{
							uFunc.addFile(oneLine + "\n", FailureIdTitPath);
							continue;
						}
						//System.out.println(oneLine);
						String PageTitle = uFunc.Simplify(ss[1]);
						PageTitle = PageTitle.replaceAll(
								"\\\\|\\?|\\!|\\/|\\<|\\>|\\:|\\*|\\|", "_");
						String outputPath = WebPageFolder +"/"+ folderNr 
								+ "/" +pageid +"_"+ PageTitle;
						File file = new File(outputPath);
						if(file.exists())
							continue;
						try {
							URL url = new URL("http://zh.wikipedia.org/wiki?curid="
									+ pageid);
							BufferedReader br2 = new BufferedReader(
									new InputStreamReader(url.openStream()));
							String output = "";
							int outNr = 0;
							String tmp = "";
							while((tmp = br2.readLine()) != null)
							{
								output += tmp + "\n";
								outNr ++;
								if(outNr % 1000 == 0)
								{
									uFunc.addFile(output, outputPath);
									output = "";
								}
							}
							uFunc.addFile(output, outputPath);
							newPageNr ++;
							if(newPageNr % 800 == 0)
							{
								folderNr ++;
								newFolder = new File(WebPageFolder +"/"+ folderNr);
								if(!(newFolder.exists() && newFolder.isDirectory()))
									newFolder.mkdir();
							}
							try{
								if(newPageNr % 10 == 0){
									Thread.sleep(100);
									if(newPageNr % 200 == 0){
										//System.out.println((System.currentTimeMillis() - t2)/1000);
										//t2 = System.currentTimeMillis();
										Thread.sleep(20000);
										System.out.println(newPageNr);
									}
									
								}
							}catch(Exception e){
								System.out.println("sleep error");
							}
						} catch (Exception e) {
							System.out.println("error:" + oneLine);
							e.printStackTrace();
							uFunc.addFile(oneLine + "\n", FailureIdTitPath);
						}
					}
				}
			}
			String info = "PageNrSize:" + PageNrSize + "\n" + 
					"newExtractPageNr:" + newPageNr;
			System.out.println(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/*
	static long t1,t2,t3,t4;
	static long cost1 = 0, cost2 = 0, cost3 = 0;
	public static void ExtractProcess() {
		// TODO Auto-generated method stub
		t1 = System.currentTimeMillis();
		BufferedReader br = 
				uFunc.getBufferedReader(EntityIdTitPath);
		int PageNr = 0;
		String oneLine = "";
		int PageId = 0;
		String PageTitle = "";
		String url;
		String outputPath = "";
		int lineNr  = 0;
		while(true){
			oneLine = null;
			try {
				oneLine = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
				continue;
			}
			lineNr += 3;
			if(lineNr % 3 != 0)
				continue;
			if(oneLine == null)
				break;
			String [] ss = oneLine.split("\t");
			if(ss.length < 2)
				continue;
			PageId = Integer.parseInt(ss[0]);
			//if(PageId <= RestartPageid)
			//	continue;
			if(PageId == 0)
				continue;
			//System.out.print("b");
			//System.out.println(lineNr +" \t" + oneLine);
			PageTitle = uFunc.Simplify(ss[1]);
			PageTitle = PageTitle.replaceAll("\\\\|\\?|\\!|\\/|\\<|\\>|\\:|\\*|\\|", "_");
			outputPath = WebPageFolder +"/"+ PageId +"_"+ PageTitle;
			File file = new File(outputPath);
			if(file.exists())
				continue;
			url = "http://zh.wikipedia.org/wiki?curid=" + PageId;
			Parser parser;
			try {
				parser = new Parser(url);
				uFunc.addFile(parser.parse(null).toHtml(true), outputPath);
			} catch (Exception e) {
				System.out.println(oneLine);
				e.printStackTrace();
				uFunc.addFile(oneLine + "\n", FailureIdTitPath);
				continue;
			}
			//System.out.println("end");

			PageNr ++;
			try{
				if(PageNr % 10 == 0){
					Thread.sleep(100);
					if(PageNr % 200 == 0){
						System.out.println((System.currentTimeMillis() - t2)/1000);
						t2 = System.currentTimeMillis();
						Thread.sleep(20000);
						System.out.println(PageNr);
					}
					
				}
			}catch(Exception e){
				System.out.println("sleep error");
			}
		}
	}
	*/
}
