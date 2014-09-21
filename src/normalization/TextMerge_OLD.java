package normalization;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import tools.uFunc;

public class TextMerge_OLD {

	public static String Data2Folder = 
			"/home/hanzhe/Public/result_hz/zhwiki/data2";
	public static String Data2TextPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/data2/text";
	
	public static void main(String [] args)
	{
		TextCollection(Data2Folder, Data2TextPath);
	}
	
	private static void TextCollection(String path, String loadPath)
	{
		File folder = new File(path);
		if(folder.isDirectory() == false)
		{
			System.out.println(path + " not directory!");
			return;
		}
		uFunc.deleteFile(loadPath);
		String oneLine = "";
		BufferedReader br;
		String output = "";
		int outNr = 0;
		int PageNr = 0;
		for(File file : folder.listFiles())
		{
			if(file.isFile())
			{
				if(file.getName().toLowerCase().endsWith("txt.txt") == false)
				{
					continue;
				}
				br = uFunc.getBufferedReader(file.getAbsolutePath());
				try {
					while((oneLine = br.readLine()) != null)
					{
						if(oneLine.startsWith("# PageId") || oneLine.startsWith("#PageId"))
						{
							oneLine = "";
							PageNr ++;
							if(PageNr % 10000 == 0)
							{
								System.out.println("PageNr : " + PageNr);
							}
						}
						output += oneLine + "\n";
						outNr ++;
						if(outNr % 500 == 0)
						{
							uFunc.addFile(output, loadPath);
							output = "";
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				uFunc.addFile(output, loadPath);
				System.out.println(file.getName() + "\t" + outNr);
				outNr = 0;
			}
		}
	}
}
