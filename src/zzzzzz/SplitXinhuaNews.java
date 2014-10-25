package zzzzzz;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import normalization.segment;
import tools.uFunc;

public class SplitXinhuaNews {

	public static void main(String [] args)
	{
		String folder = "/home/hanzhe/Public/result_hz/XinhuaNews/outcome/";
		Split(folder);
	}
	public static void Split(String folder)
	{
		File folderFile = new File(folder);
		for(File file : folderFile.listFiles())
		{
			BufferedReader br = 
					uFunc.getBufferedReader(file.getAbsolutePath());
			String oneLine = "";
			StringBuffer sb = new StringBuffer();
			int outNr = 0;
			uFunc.deleteFile(file.getAbsolutePath() + ".seg");
			try {
				while((oneLine = br.readLine()) != null)
				{
					if(oneLine.matches("=+"))
					{
						sb.append(oneLine + "\n");
						continue;
					}
					String out = segment.ANSJsegmentSeg(oneLine);
					sb.append(out + "\n");
					outNr ++;
					if(outNr % 1000 == 0)
					{
						uFunc.addFile(sb.toString(), file.getAbsolutePath() + ".seg");
						sb.setLength(0);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			uFunc.addFile(sb.toString(), file.getAbsolutePath() + ".seg");
		}
	}
}
