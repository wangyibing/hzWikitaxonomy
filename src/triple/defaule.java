package triple;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import database.InfoboxNameList;
import extract.Extract;
import tools.URL2UTF8;
import tools.uFunc;
import triple.extract.TripleGenerator;

public class defaule {

	public static void main(String [] args){
		String re = uFunc.OutputProjectInfo();
		System.out.println(re);
		OutputTripleNr();
		
		String s2 = "http://zh.wikipedia.org/wiki/%E4%B8%AD%E5%9B%BD";
		System.out.println("chinese:" + URL2UTF8.unescape(s2));
		
		String ssss01 = "网站：[中国重庆武隆网%20%20http://www.cqwulong.cn/ www.cqwulong.cn]";
		System.out.println((TripleGenerator.getTripleFromSgl(ssss01, 100)));
	}
	private static void OutputTripleNr() {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(Extract.TriplePath);
		String oneline = "";
		int lastId = 0;
		int Nr = 0;
		int lineNr = 0;
		try {
			while((oneline = br.readLine()) != null)
			{
				lineNr ++;
				int pageid = 0;
				try{
					pageid = Integer.parseInt(oneline.split("\t")[0]);
				}catch(Exception e1){
					System.out.println("error line:" + oneline);
				}
				
				if(pageid != 0 && pageid != lastId)
				{
					lastId = pageid;
					Nr ++;
				}
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("infoboxNr:" + Nr);
		System.out.println("lineNr:" + lineNr);
		System.out.println("average:" + (double)(1.0*lineNr/Nr) + "\n");
		
	}
}
