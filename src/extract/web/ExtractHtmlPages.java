package extract.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.htmlparser.Parser;

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
	static int RestartPageid = 1126608;
	public static void main(String [] args){
		ExtractProcess();
	}
	static long t1,t2,t3,t4;
	static long cost1 = 0, cost2 = 0, cost3 = 0;
	private static void ExtractProcess() {
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
			if(PageId <= RestartPageid)
				continue;
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
}
