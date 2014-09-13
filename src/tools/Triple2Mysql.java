package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import database.Entity;
import database.Zhwiki;

public class Triple2Mysql {
	public static void main(String [] args)
	{
		AddTriples2Mysql("/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Web/triple",
				"hzwikitaxonomy", "hzTriple", "data/sql/CraetehzTriple.sql");
	}

	public static void AddTriples2Mysql(String triplePath,
			String dbName, String tableName, String CreateSQLFile)
	{
		Mysql db = new Mysql();
		db.Connect2DB(dbName);
		db.execute(CreateSQLFile);
		
		String SQL = "insert into " + tableName 
				+ "(id, SubId, PredId, ObjId, Subject, Predicate, Object) values"
				+ "(?, ?, ?, ?, ?, ?, ?)";
		try {
			db.Query = db.conn.prepareStatement(SQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		BufferedReader br = uFunc.getBufferedReader(triplePath);
		String oneLine = "";
		int TripleId = 1;
		int longestObj = 0;
		int wrongNr = 0;
		try {
			while((oneLine = br.readLine()) != null)
			{
				while(oneLine.startsWith("null"))
					oneLine = oneLine.substring(4);
				String [] ss = oneLine.split("\t");
				int subId = Integer.parseInt(ss[0]);
				String subCont = Entity.getTitle(subId);
				if(subCont == null)
					subCont = Zhwiki.getTitle(subId);
				if(subCont == null)
				{
					//System.out.println("subid null:" + oneLine);
					wrongNr ++;
					continue;
				}
				//System.out.println(subCont);
				int []Id = new int[2];
				String [] cont = new String[2];
				for(int i = 0 ; i < 2; i ++)
				{
					if(ss[i + 1].contains("->"))
					{
						cont[i] = ss[i + 1].substring(0, ss[i + 1].indexOf("->"));
						Id[i] = Entity.getId(ss[i + 1].substring(
								ss[i+1].indexOf("->") + 3, ss[i+1].length() - 1));
					}
					else
					{
						cont[i] = ss[i+1];
						Id[i] = 0;
					}
				}
				if(cont[1].length() > longestObj)
					longestObj = cont[1].length();
				try {
					db.Query.setInt(1, TripleId);
					db.Query.setInt(2, subId);
					db.Query.setInt(3, Id[0]);
					db.Query.setInt(4, Id[1]);
					db.Query.setString(5, subCont);
					db.Query.setString(6, cont[0]);
					db.Query.setString(7, cont[1]);
					db.Query.addBatch();
					if(TripleId % 1000 == 0)
					{
						db.Query.executeBatch();
						if(TripleId % 100000 == 0)
						{
							String info = "wrongNr:" + wrongNr + "\t" + 
									"totalTriples:" + TripleId + "\n" + 
									"longest obj:" + longestObj; 
							System.out.println(info);
						}
					}
					TripleId ++;
					//System.out.println(TripleId);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			try {
				db.Query.executeBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String info = "wrongNr:" + wrongNr + "\t" + 
					"totalTriples:" + TripleId + "\n" + 
					"longest obj:" + longestObj; 
			System.out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
