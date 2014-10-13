package extract.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import tools.Mysql;
import tools.uFunc;

public class Langlinks {
	static String langlinkPath =
			"/home/hanzhe/Public/result_hz/wiki_count2/pageinfo/lang/EnidEngtitle";

	public static void GetEntityIdEnTitlePair(String tar)
	{
		if(tar == null)
			tar = langlinkPath;
		Mysql langlinks = new Mysql();
		langlinks.Connect2DB("wikipedia", null);
		String sql = "select ll_from, ll_title from langlinks where ll_lang='en' ";
		try {
			langlinks.Query = langlinks.conn.prepareStatement(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		try {
			uFunc.deleteFile(tar);
			ResultSet rs = langlinks.Query.executeQuery();
			int Num = 0;
			String output = "";
			while( rs.next()){
				byte[] buff = rs.getBytes(1); 
				byte[] buff2 = rs.getBytes(2); 
            	String entityId = new String(buff,"UTF-8");
            	String entityEnTitle = new String(buff2,"UTF-8");
            	//System.out.println("\t"+oneline);
				if(entityEnTitle.equals(""))
					continue;
				Num++;
            	output += entityId +"\t"+ entityEnTitle +"\n";
				if(Num % 1000 == 0){
					uFunc.addFile(output, tar);
					output ="";
					if(Num % 50000 == 0)
						System.out.println(Num +" records geted!");
				}
			}
			uFunc.addFile(output, tar);
			System.out.println("total num:" + Num +" geted!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		langlinks.disconnectToMysql();
		
	}
}
