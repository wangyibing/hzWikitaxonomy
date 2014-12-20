package extract.predicate.predicateInfo;

import java.io.BufferedReader;
import java.sql.ResultSet;
import java.util.HashMap;

import database.Predicate;
import tools.Mysql;
import tools.QsortPair;
import tools.uFunc;

public class Update_PredicateInfo {
	private static String folder = 
			"/home/hanzhe/Public/result_hz/zhwiki/predicate/";
	private static String PidUPairFile = folder + "PidUPair";
	public static void Update(){
		Mysql m = new Mysql("hzWikiCount2", null);
		
	}

	public static void UpperTitle(Mysql db, String tripleTableName,
			String predTableName, String utName){
		db.SetLargeQuery("select Predicate, " + utName + " from " + tripleTableName);
		ResultSet rs = null;

		StringBuffer outPidUPair = new StringBuffer();
		try {
			rs = db.Query.executeQuery();
			int outNr = 0;
			while(rs.next())
			{
				String pred = rs.getString(1);
				int predId = Predicate.GetId(pred);
				String upper = rs.getString(2);
				if(upper != null && upper.toLowerCase().equals("null"))
					upper = null;
				if(upper != null){
					outPidUPair.append(predId + "\t" + upper + "\n");
					outNr ++;
				}
				
				if(outNr % 1000 == 0){
					uFunc.addFile(outPidUPair, PidUPairFile);
				}
			}
			uFunc.addFile(outPidUPair, PidUPairFile);
			
			// sor pair
			QsortPair.SortPair(PidUPairFile, PidUPairFile+ ".sorted", false, true, 0);
			uFunc.deleteFile(PidUPairFile);
			
			// update
			String sql = "update " + predTableName + " set " + utName + 
					"= ? where id= ?";
			db.Query = db.conn.prepareStatement(sql);
			
			HashMap<String, Integer> upFreq = new HashMap<String, Integer>();
			BufferedReader br = uFunc.getBufferedReader(PidUPairFile);
			String oneLine = "";
			int lastId = -1;
			int rNr = 0;
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine .split("\t");
				int pid = Integer.parseInt(ss[0]);
				String up = ss[1];
				if(pid != lastId){
					if(lastId != -1){
						String upCol = uFunc.Map2Id(upFreq, ",", ";");
						db.Query.setInt(2, lastId);
						db.Query.setString(1, upCol);
						db.Query.addBatch();
						db.Query.executeBatch();
						rNr ++;
					}
					upFreq.clear();
					lastId = pid;
					upFreq.put(up, 1);
				}
				int freq = 1;
				if(upFreq.containsKey(up))
					freq += upFreq.remove(up);
				upFreq.put(up, freq);
			}
			String upCol = uFunc.Map2Id(upFreq, ",", ";");
			db.Query.setInt(2, lastId);
			db.Query.setString(1, upCol);
			db.Query.addBatch();
			db.Query.executeBatch();
			rNr ++;
			System.out.println("total " + utName + " record Nr:" + rNr);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
