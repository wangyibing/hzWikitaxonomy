package extract.hztripletable;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import database.Category;
import database.Entity;
import tools.Mysql;
import tools.uFunc;

public class CateInfo2Triple {

	public static void main(String [] args)
	{
		//checkIdMutral("/home/hanzhe/Public/result_hz/wiki_count2/pageinfo/CateidCateTitlePair");
		InsertCateInfo2Mysql(3000000, 15000000, null, "hzWikiCount2", "hzTriple", 
				"/home/hanzhe/Public/result_hz/wiki_count2/pageinfo/CateidPageidPair");
	}
	
	public static boolean InsertCateInfo2Mysql(int startLineNr, int batchSize,
			String IP, String dbName, 
			String tableName, String CateidPageidPair)
	{
		
		boolean result = true;
		String oneLine = "";
		BufferedReader br = uFunc.getBufferedReader(CateidPageidPair);
		Mysql m = new Mysql(dbName, IP);
		String sql = "insert into " + tableName 
				+ "(SubId, PredId, ObjId, Subject, Predicate, Object) "
				+ "values(?, ?, ?, ?, 'Category', ?)";
		try {
			m.Query = m.conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		int cateInfoNr = 0;
		int lastPageId = 0;
		try {
			while((oneLine = br.readLine()) != null)
			{
				cateInfoNr ++;
				if(cateInfoNr < startLineNr)
					continue;
				if(cateInfoNr > startLineNr + batchSize)
					break;
				String [] ss = oneLine.split("\t");
				int cateId = Integer.parseInt(ss[0]);
				int pageId = Integer.parseInt(ss[1]);
				String entityTitle = Entity.getTitles(pageId);
				if(entityTitle == null)
				{
					if(pageId != lastPageId)
						System.out.println("entity title null:" + oneLine);
					lastPageId = pageId;
					continue;
				}
				lastPageId = pageId;
				if(entityTitle.contains("####"))
					entityTitle = entityTitle.substring(0, entityTitle.indexOf("####"));
				String cateName = Category.GetName(cateId);
				try {
					m.Query.setInt(1, pageId);
					m.Query.setInt(2, 0);
					m.Query.setInt(3, cateId);
					m.Query.setString(4, entityTitle);
					m.Query.setString(5, cateName);
					m.Query.addBatch();
					m.Query.executeBatch();
					if(cateInfoNr % 100000 == 0)
						System.out.println(cateInfoNr + " cateinfo inserted!");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(oneLine);
					
				}
			}
			System.out.println(cateInfoNr + " cateinfo inserted total!");
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public static boolean checkIdMutral(String CateidCateTitlePair)
	{
		boolean result = true;
		String oneLine = "";
		BufferedReader br = uFunc.getBufferedReader(CateidCateTitlePair);
		try {
			while((oneLine = br.readLine()) != null )
			{
				String [] ss = oneLine.split("\t");
				int cateId = Integer.parseInt(ss[0]);
				if(Entity.getTitles(cateId) != null)
				{
					System.out.println(oneLine + "\t" + Entity.getTitles(cateId));
					result = false;
					continue;
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}
