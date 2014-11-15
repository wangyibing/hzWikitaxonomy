package extract.entityinfotables;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import tools.Mysql;
import tools.uFunc;

public class EntityIdTitleTable {
	public static void main(String [] args)
	{
		CreateEntityIdTitleTable(null, 
				"data/pageinfo/entity/EntityTitle2Id",
				"data/sql/EntityIdTitleTable.sql");
	}

	public static boolean CreateEntityIdTitleTable(String mySqlIP,
			String EntityTitle2IdFile, String EntityIdTitleTableCreationFile)
	{
		if(mySqlIP == null)
			mySqlIP = "localhost";
		Mysql mysql = new Mysql("hzWikiCount2", mySqlIP);
		String tableName = "EntityIdTitle";
		HashMap<String, Integer> title2Id = new HashMap<String, Integer>();
		HashMap<String, Integer> Simptitle2Id = new HashMap<String, Integer>();
		HashMap<String, Integer> DupSimptitle2Id = new HashMap<String, Integer>();

		LoadHashMap(EntityTitle2IdFile, title2Id, Simptitle2Id, DupSimptitle2Id);
		mysql.execute(EntityIdTitleTableCreationFile);
		System.out.println("EEE");
		return RecordCreation(mysql, tableName, title2Id);
	}

	private static void LoadHashMap(String entityTitle2IdFile, 
			HashMap<String, Integer> title2Id,
			HashMap<String, Integer> simptitle2Id,
			HashMap<String, Integer> dupSimptitle2Id) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(entityTitle2IdFile);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null )
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 2)
				{
					System.out.println("wrong line:" + oneLine);
					continue;
				}
				int pageid = Integer.parseInt(ss[1]);
				String title = ss[0];
				String simTitle = uFunc.Simplify(ss[0]);
				if(title2Id.containsKey(title))
				{
					System.out.println("title2Id table dup:" + title + ":" + 
							pageid + "; " +title2Id.get(title));
					continue;
				}
				title2Id.put(title, pageid);
				if(simptitle2Id.containsKey(simTitle))
				{
					dupSimptitle2Id.put(simTitle, 0);
				}
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("original title recordNr:" + title2Id.size());
		title2Id.clear();

		br = uFunc.getBufferedReader(entityTitle2IdFile);
		try {
			while((oneLine = br.readLine()) != null )
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 2)
					continue;
				int pageid = Integer.parseInt(ss[1]);
				String title = ss[0];
				String simTitle = uFunc.Simplify(ss[0]);
				if(title2Id.containsKey(title))
					continue;
				title2Id.put(title, pageid);
				if(dupSimptitle2Id.containsKey(simTitle) == false
						&& title2Id.containsKey(simTitle) == false)
				{
					title2Id.put(simTitle, pageid);
				}
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("total title nr:" + title2Id.size());
	}

	private static boolean RecordCreation(Mysql mysql, String tableName, 
			HashMap<String, Integer> title2Id) {
		// TODO Auto-generated method stub
		String sql = "insert into " + tableName + " (title, EntityId) "
				+ " values(?, ?) ";
		try {
			mysql.Query = mysql.conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("table creation failed!");
			e.printStackTrace();
			return false;
		}
		Iterator<Entry<String, Integer>> it = 
				title2Id.entrySet().iterator();
		int recordNr = 0;
		long t1 = System.currentTimeMillis();
		while(it.hasNext())
		{
			Entry<String, Integer> next = it.next();
			try {
				mysql.Query.setString(1, "'" + 
						next.getKey().replaceAll("'", "\\\\'") + "'");
				mysql.Query.setInt(2, next.getValue());
				mysql.Query.addBatch();
				recordNr ++;
				try{
					if(recordNr % 1 == 0)
					{
						mysql.Query.executeBatch();
						if(recordNr % 100000 == 0){
							String info = recordNr + " entities inited, cost:" +
									uFunc.GetTime(System.currentTimeMillis() - t1);
							t1 = System.currentTimeMillis();
							System.out.println(info);
						}
					}
				}catch(Exception e){
					System.out.println( next.getKey() + "\t" + next.getValue());
					//e.printStackTrace();
					continue;
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println( next.getKey() + "\t" + next.getValue());
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
