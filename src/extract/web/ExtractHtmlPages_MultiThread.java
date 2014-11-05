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
class ExtractThread implements Runnable{
	public ExtractThread()
	{
		String infoPath = "data/info/extract/ExtractThread.info";
		uFunc.AlertPath = infoPath;
		uFunc.deleteFile(uFunc.AlertPath);
		WebPageFolder = WholeFolder + "FileSplit";
		FailureIdTitPath = WholeFolder + "FailureIdTit.txt";
		br = uFunc.getBufferedReader(Entity.id2titFile);
		t1 = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		File filesplit = new File(WebPageFolder);
		if(filesplit.exists() == false ||
				filesplit.isDirectory() == false){
			System.out.println("filesplit folder error!");
			return;
		}
		existFolderNr = filesplit == null ? 0 : filesplit.listFiles().length;
		newPageNr = 0;
		for(File folder : filesplit.listFiles())
		{
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
					System.out.println(pageid + " exist twice! folder:" + folder.getName());
				extractedPage.put(pageid, 0);
			}
			if(folder.getName().equals(existFolderNr + ""))
				newPageNr = folder == null ? 0 : folder.listFiles().length;
		}
		System.out.println("current page Nr:" + extractedPage.size() + "\t" +
				"existFolderNr:" + existFolderNr + "\n" +
				"pageNr in last folder:" + newPageNr + "\t" + 
				"cost:" + (System.currentTimeMillis() - t1));
		t1 = System.currentTimeMillis();
	}
	private String WholeFolder = 
			"/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/";
	private static String WebPageFolder;
	private String FailureIdTitPath;
	private static String c = "ExtractThread";
	private static String info;
	
	HashMap<Integer, Integer> extractedPage = 
			new HashMap<Integer, Integer>();
	BufferedReader br;
	static int existFolderNr;
	static int newPageNr;
	private long startTime;
	private static long t1; 
	public void run()
	{
		String oneLine;
		try {
			while((oneLine = br.readLine()) != null){
				String [] ss = oneLine.split("\t");
				int pageid = Integer.parseInt(ss[0]);
				if(extractedPage.containsKey(pageid))
					continue;
				else
				{
					if(newPageNr % 5 == 0)
						System.out.println(oneLine);
					String PageTitle = uFunc.Simplify(ss[1]);
					if(PageTitle.contains("####"))
						PageTitle = PageTitle.substring(0, PageTitle.indexOf("####"));
					PageTitle = PageTitle.replaceAll(
							"\\\\|\\?|\\!|\\/|\\<|\\>|\\:|\\*|\\|", "_");
					String outputPath = WebPageFolder +"/"+ existFolderNr 
							+ "/" +pageid +"_"+ PageTitle;
					File file = new File(outputPath);
					if(file.exists())
						continue;
					try {
						URL url = new URL("http://zh.wikipedia.org/wiki?curid="
								+ pageid);
						BufferedReader br = new BufferedReader(
								new InputStreamReader(url.openStream()));
						StringBuffer sb = new StringBuffer();
						String ts;
						while((ts = br.readLine()) != null)
							sb.append(ts + "\n");
						//BufferedReader br2 = new BufferedReader(new InputStreamReader(url.openStream()));
						File outputFile = new File(outputPath);
						if(outputFile.exists())
							continue;
						uFunc.addFile(sb.toString(), outputPath);
						newPageNr ++;
						if(newPageNr >= 800)
							CreateNewFolder();
						mySleep();
					} catch (Exception e) {
						uFunc.Alert(false, c, "http error:" + oneLine);
						//e.printStackTrace();
						uFunc.addFile(oneLine + "\n", FailureIdTitPath);
					}
				}
			}
			uFunc.AlertClose();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String info = "newPageNr:" + newPageNr + "\n" + 
				"newExtractPageNr:" + newPageNr;
		uFunc.Alert(true, c, info);
	}

	private void mySleep() {
		// TODO Auto-generated method stub
		try{
			if(newPageNr % 10 == 0){
				Thread.sleep(100);
				if(newPageNr % 100 == 0){
					Thread.sleep(1000);
					//System.out.println(newPageNr + " pages");
				}
				
			}
		}catch(Exception e){
			System.out.println("sleep error");
		}
	}

	private static boolean CreateNewFolder(){
		existFolderNr ++;
		File newFolder = new File(WebPageFolder + "/" + existFolderNr + "/");
		if(!(newFolder.exists() && newFolder.isDirectory()))
		{
			newFolder.mkdir();
			System.out.println();
			long t2 = System.currentTimeMillis();
			info = "folder:" + (existFolderNr-1) + " cost:" + 
					uFunc.GetTime(t2 - t1) + "\n" + 
					"folder:" + newFolder.getName() + " created";
			uFunc.Alert(true, c, info);
			t1 = t2;
			newPageNr = 0;
			return true;
		}
		return false;
		
	}
}
public class ExtractHtmlPages_MultiThread {

	//static int RestartPageid = 1126608;
	public static void main(String [] args){
		//ExtractProcess();
		int threadNr = 5;
		ExtractThread myThr = new ExtractThread();
		new Thread(myThr, "thread 1").start();
		new Thread(myThr, "thread 2").start();
		new Thread(myThr, "thread 3").start();
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
