package tools;

import java.io.File;

public class MoveFile {

	public static void main(String []args)
	{
		String srcPath = "/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/ZhWebPages";
		String targetPath = "/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/ZhWebPages";
		for(int i = 1; i < 4; i ++)
			RemoveAll(srcPath + i, targetPath);
	}

	private static void RemoveAll(String srcPath, String targetPath) {
		// TODO Auto-generated method stub
		File srcFolder = new File(srcPath);
		File tgtFolder = new File(targetPath);
		if(srcFolder.isDirectory() == false)
		{
			System.out.println(srcPath + " is not a folder");
			return;
		}
		if(tgtFolder.isDirectory() == false)
		{
			System.out.println(tgtFolder + " is not a folder");
			return;
		}
		int Nr = 0;
		for(File file : srcFolder.listFiles())
		{
			file.renameTo(new File(tgtFolder, file.getName()));
			Nr ++;
			if(Nr % 2000 == 0)
				System.out.println(Nr + " moved");
		}
	}
}
