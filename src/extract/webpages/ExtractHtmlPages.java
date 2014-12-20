package extract.webpages;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

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
				Entity.id2titFile);
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
			String info = "PageNrSize:" + PageNrSize + "\n" + 
					"newExtractPageNr:" + newPageNr;
			System.out.println(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean ExtractOnePage(String URLPath, String tgPath)
	{
		URL url;
		try {
			url = new URL(URLPath);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		BufferedReader br2;
		try {
			br2 = new BufferedReader(
					new InputStreamReader(url.openStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		String output = "";
		int outNr = 0;
		String tmp = "";
		uFunc.deleteFile(tgPath);
		try {
			while((tmp = br2.readLine()) != null)
			{
				output += tmp + "\n";
				outNr ++;
				if(outNr % 1000 == 0)
				{
					uFunc.addFile(output, tgPath);
					output = "";
				}
			}
			uFunc.addFile(output, tgPath);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
