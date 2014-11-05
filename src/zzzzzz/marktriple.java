package zzzzzz;

import java.io.BufferedReader;
import java.io.IOException;

import tools.uFunc;
import database.Entity;

public class marktriple {
	public static void main(String [] args)
	{
		String output = "";
		int nr = 0;
		String oneLine = "";
		String input = "/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Web/Triple";
		uFunc.deleteFile(input + ".out");
		BufferedReader br = uFunc.getBufferedReader(input);
		try {
			while((oneLine = br.readLine() )!= null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 3)
					continue;
				String out = "";
				int pageid = Entity.getId(ss[2]);
				if(pageid > 0)
				{
					out = ss[0] + "\t" + ss[1]+ "\t" + ss[2] + "->[" + pageid + "]";
				}
				else out = oneLine;
				output += out + "\n";
				nr ++;
				if(nr % 1000 == 0){
					uFunc.addFile(output, input + ".out");
					output = "";
				}
			}
			uFunc.addFile(output, input + ".out");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
