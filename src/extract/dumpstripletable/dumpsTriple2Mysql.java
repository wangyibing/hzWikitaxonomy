package extract.dumpstripletable;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

import database.Entity;
import tools.Mysql;
import tools.uFunc;

/**
 * recordnr:5067569	pageNr:367989
 * @author hanzhe
 *
 */
public class dumpsTriple2Mysql {
	public static void main(String [] args){
		Mysql m = new Mysql("hzWikiCount2", null);
		try {
			push(m, "/home/hanzhe/Public/result_hz/wiki_count2/predicate/dumpsTriples",
					"dumpsTriple", "data/sql/dumpsTriple.sql");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void push(Mysql db, String dumpsTriple, 
			String tableName, String sql) throws NumberFormatException, IOException{
		db.execute(sql);
		String insert = "insert into " + tableName + 
				" (SubId, ObjId, Subject, Predicate, Object, InfoboxName)"
				+ " values(?, ?, ?, ?, ?, ?)";
		try {
			db.Query = db.conn.prepareStatement(insert);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		BufferedReader br = uFunc.getBufferedReader(dumpsTriple);
		String oneLine = "";
		String infoboxName = "";
		int lastId = 0;
		String lastLine = "";
		int recordNr = 0;
		int pageNr = 0;
		while((oneLine = br.readLine()) != null)
		{
			if(oneLine.startsWith("Infobox") == false){
				while(oneLine.length() > 1 && oneLine.startsWith("[") == false)
					oneLine = oneLine.substring(1);
			}
			
			String [] ss = oneLine.split("\t");
			if(oneLine.startsWith("InfoboxName:"))
			{
				infoboxName = oneLine.substring(12);
				lastLine = oneLine;
				//System.out.println("Infobox:" + oneLine);
				continue;
			}
			if(ss.length < 3){
				//System.out.println("less than 3:" + oneLine);
				continue;
			}
			String sub = ss[0];
			int subId = Integer.parseInt(sub.substring(2, sub.length()-2));
			sub = Entity.getTitle(subId);
			if(sub == null){
				System.out.println("title missed:" + oneLine);
				lastLine = oneLine;
				lastId = subId;
				continue;
			}
			String pred = ss[1].replaceAll("_", " ");
			String obj = ss[2];
			int objId = 0;
			if(obj.startsWith("[[")){
				try{
					objId = Integer.parseInt(obj.substring(2, obj.length()-2));
				}catch(Exception e){
					System.out.println("obj format error:" + oneLine);
					continue;
				}
				
				obj = Entity.getTitle(objId);
				if(obj == null){
					System.out.println("obj missed:" + oneLine);
					lastLine = oneLine;
					lastId = subId;
					continue;
				}
			}
			if(subId != lastId && lastLine.startsWith("InfoboxName:") == false)
			{
				System.out.println("no Infobox:" + oneLine + "\t" + lastLine);
				infoboxName = null;
			}
			if(subId != lastId)
				pageNr ++;
			
			lastId = subId;
			lastLine = oneLine;
			try {
				db.Query.setInt(1, subId);
				db.Query.setInt(2, objId);
				db.Query.setString(3, sub);
				db.Query.setString(4, pred);
				db.Query.setString(5, obj);
				db.Query.setString(6, infoboxName);
				recordNr ++;
				if(recordNr % 50000 == 0)
					System.out.println(recordNr + " record passed!");
				db.Query.addBatch();
				db.Query.executeBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("mysql insert error:" + oneLine);
				continue;
			}
		}
		System.out.println("recordnr:" + recordNr + "\t" + "pageNr:" + pageNr);
	}
}
