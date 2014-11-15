package extract.entityinfotables;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import database.Entity;
import tools.Mysql;
import tools.uFunc;

public class EntityInfo {

	static String info = ""; 
	public static void main(String [] args)
	{
		CreateEntityInfoTable(null,
				"/home/hanzhe/Public/result_hz/zhwiki/data2/FirPara",
				"/home/hanzhe/Public/result_hz/wiki_count2/pageinfo/entity/WikiIMG",
				"data/sql/CreateEntityInfo.sql");
	}
	public static void CreateEntityInfoTable(String mySqlIP,
			String firParaFile, String ImgFile, String EntityInfoTableCreationFile)
	{
		if(mySqlIP == null)
			mySqlIP = "localhost";
		Mysql mysql = new Mysql("hzWikiCount2", mySqlIP);
		mysql.execute(EntityInfoTableCreationFile);
		String tableName = "EntityInfo";
		boolean created = RecordCreation(mysql, tableName);
		if(created == false)
			return;
		UpdateIMG(ImgFile, mysql, tableName);
		UpdateFirPara(firParaFile, mysql, tableName);
	}
	private static boolean UpdateFirPara(String firParaFile, Mysql mysql,
			String tableName) {
		// TODO Auto-generated method stub
		String sql = " update " + tableName + " set FirPara = ?"
				+" where EntityId= ?;";
		try {
			mysql.Query = mysql.conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		String oneLine = "";
		BufferedReader br = uFunc.getBufferedReader(firParaFile);
		try {
			int nr = 0;
			long t1 = System.currentTimeMillis();
			while((oneLine = br.readLine())!= null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 2){
					info = "col less than 2:" + oneLine;
					continue;
				}
				int pageid = Integer.parseInt(ss[0]);
				try {
					ss[1] = "'" + ss[1].replaceAll("'", "\\\\'") + "'";
					mysql.Query.setString(1, ss[1]);
					mysql.Query.setInt(2, pageid);
					mysql.Query.addBatch();
					nr ++;
					if(nr % 1 == 0)
					{
						mysql.Query.executeBatch();
						if(nr % 50000 == 0){
							info = nr + "nr FirPara info inserted, cost:" + 
									uFunc.GetTime(System.currentTimeMillis() - t1);
							t1 = System.currentTimeMillis();
							System.out.println(info);
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println(oneLine);
					//e.printStackTrace();
					continue;
				}
				
			}
			System.out.println("total FirPara info nr:" + nr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private static boolean RecordCreation(Mysql mysql, String tableName) {
		// TODO Auto-generated method stub
		String oneLine = "";
		BufferedReader br = uFunc.
				getBufferedReader(Entity.id2titFile);
		String sql = "insert into " + tableName + " (EntityId, title) "
				+ " values(?, ?);";
		try {
			mysql.Query = mysql.conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("query error");
			e.printStackTrace();
			return false;
		}
		
		try {
			int nr = 0;
			long t1 = System.currentTimeMillis();
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 3)
					continue;
				int pageid = Integer.parseInt(ss[0]);

				try {
					mysql.Query.setInt(1, pageid);
					mysql.Query.setString(2, ss[1]);
					mysql.Query.addBatch();
					nr ++;
					if(nr % 1000 == 0){
						mysql.Query.executeBatch();
						if(nr % 100000 == 0){
							info = nr + " entities inited, cost:" +
									uFunc.GetTime(System.currentTimeMillis() - t1);
							t1 = System.currentTimeMillis();
							System.out.println(info);
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			info = "total entities nr:" + nr;
			System.out.println(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return true;
	}
	private static boolean UpdateIMG(String imgFile, Mysql mysql,
			String tableName) {
		// TODO Auto-generated method stub
		String sql = " update " + tableName + " set IMGUrl = ?,"
				+ "IMGWidth = ?, IMGHeight = ? "
				+" where EntityId= ? ;";
		try {
			mysql.Query = mysql.conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		String oneLine = "";
		BufferedReader br = uFunc.getBufferedReader(imgFile);
		try {
			int nr = 0;
			long t1 = System.currentTimeMillis();
			while((oneLine = br.readLine())!= null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 5){
					info = "col less than 5:" + oneLine;
					continue;
				}
				int pageid = Integer.parseInt(ss[0]);
				int w = Integer.parseInt(ss[3]);
				int h = Integer.parseInt(ss[4]);
				try {
					mysql.Query.setString(1, ss[2]);
					mysql.Query.setInt(2, w);
					mysql.Query.setInt(3, h);
					mysql.Query.setInt(4, pageid);
					mysql.Query.addBatch();
					nr ++;
					if(nr % 1000 == 0)
					{
						mysql.Query.executeBatch();
						if(nr % 50000 == 0){
							info = nr + "nr IMG info inserted, cost:" + 
									uFunc.GetTime(System.currentTimeMillis() - t1);
							t1 = System.currentTimeMillis();
							System.out.println(info);
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				
			}
			System.out.println("total IMG info nr:" + nr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
