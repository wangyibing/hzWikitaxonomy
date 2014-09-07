

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import database.InfoboxNameList;
import extract.Extract;
import tools.uFunc;

public class defaule {

	public static void main(String [] args){
		String re = uFunc.OutputProjectInfo();
		System.out.println(re);
		OutputTripleNr();
		System.out.println(uFunc.isPeriod("20x1981 - 2003年"));
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
