package tools;

import java.io.File;

public class MoveFile {

	public static void main(String []args)
	{
		//for(int i = 1; i < 4; i ++)
		//	RemoveAll(srcPath + i, targetPath);
		FileRename("/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/FileSplit");
	}

	public static void RemoveAll(String srcPath, String targetPath) {
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
	
	private static void FileRename(String folderPath)
	{
		File folder = new File(folderPath);
		if(folder.exists()== false || folder.isDirectory() == false)
		{
			System.out.println(folderPath + " is not a folder");
		}
		int folderNr = 0;
		for(File subFolder : folder.listFiles())
		{
			folderNr ++;
			if(folderNr % 100 == 0)
				System.out.println(folderNr + " folders stdd");
			for(File file : subFolder.listFiles())
			{
				String fileName = file.getName().replaceAll(
						"[`~!@#$^&\\*=\\|{}':;',\\[\\].<>/?￥%]", "_");
				if(fileName.equals(file.getName()) == false)
				{
					//System.out.println(fileName + "\t" + file.getName());
					file.renameTo(new File(subFolder.getAbsolutePath(), fileName));
				}
				
			}
		}
		System.out.println(folderNr + " folders stdd");
	}
}
