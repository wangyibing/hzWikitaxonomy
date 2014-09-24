package normalization;

import java.io.BufferedReader;
import java.io.IOException;

import tools.uFunc;

public class ExtractFirPara {
	public static void main(String [] args)
	{
		Extract("/home/hanzhe/Public/result_hz/zhwiki/data2/text",
				"/home/hanzhe/Public/result_hz/zhwiki/data2/FirPara");
	}
	public static void Extract(String src, String tar)
	{
		BufferedReader br = uFunc.getBufferedReader(src);
		
		String oneLine = "";
		String output = "";
		int pageid = 0;
		int outNr = 0;
		uFunc.deleteFile(tar);
		while(true)
		{
			while(pageid == 0)
			{
				try {
					oneLine = br.readLine();
					if(oneLine == null)
						break;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(oneLine.startsWith("#PageId:"))
					pageid = Integer.parseInt(oneLine.substring(8));
			}
			if(oneLine == null)
				break;
			try {
				while((oneLine = br.readLine())!= null)
				{
					if(oneLine.startsWith("#PageId:"))
					{
						pageid = Integer.parseInt(oneLine.substring(8));
						continue;
					}
					oneLine = uFunc.Simplify(oneLine);
					if(oneLine.contains("参见") || oneLine.contains("关于")
							|| oneLine.contains("重定向")
							|| oneLine.contains("(消歧")
							|| oneLine.contains("详见")
							|| oneLine.contains("File:")
							|| oneLine.contains("提示")
							|| oneLine.contains("本条目"))
					{
						//System.out.println(oneLine);
						continue;
					}
					if(oneLine.equals("") == false && 
							(oneLine.contains("可以指") || oneLine.length() >= 10))
						break;
				}
				if(oneLine == null)
					break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(oneLine);
			//if(oneLine.length() < 30)
			//	System.out.println(pageid + "\t" + oneLine);
			output += pageid + "\t" + oneLine.replaceAll("\\s+", " ") + "\n";
			pageid = 0;
			outNr ++;
			if(outNr % 1000 == 0)
			{
				uFunc.addFile(output, tar);
				output = "";
				if(outNr % 50000 == 0)
					System.out.println(outNr + " pages parsed");
			}
		}
		System.out.println("total " + outNr + " pages parsed");
		uFunc.addFile(output, tar);
	}

}
