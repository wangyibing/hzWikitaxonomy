

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import database.InfoboxNameList;
import extract.Extract;
import tools.uFunc;

public class defaule {

	public static void main(String [] args){
		String re = uFunc.OutputProjectInfo();
		System.out.println(re);
		System.out.println(("$12").replaceAll("\\$", "\\\\\\$"));
		StringBuffer sb = new StringBuffer();
		sb.append('c');
		sb.append("wre");
		sb.append('.');
		sb.append(' ');
		System.out.println(("1\n23\nN2\n\n3\nsdf").replaceAll("\n([a-z])", "$1"));
		
		
		String folder = "/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Web/";
		BufferedReader br = uFunc.getBufferedReader(folder + "Triple2");
		String oneLine = "";
		HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		HashMap<String, Integer> map2 = new HashMap<String, Integer>();
		try {
			while((oneLine = br.readLine()) != null)
			{
				String pre = oneLine.split("\t")[1];
				map2.put(pre, 0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		br = uFunc.getBufferedReader(folder + "Triple");
		try {
			while((oneLine = br.readLine()) != null)
			{
				String pre = oneLine.split("\t")[1];
				if(map1.containsKey(pre) == false)
				{
					map1.put(pre, 0);
					if(map2.containsKey(pre) == false)
						System.out.println(oneLine);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
