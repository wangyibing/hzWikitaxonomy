package extract.predicate.predicateInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import tools.Mysql;
import tools.PinyinSim;
import tools.QsortPair;
import tools.uFunc;
import word2vec.word2vec;

public class PredicateId {
	/*public static void main(String [] args)
	{
		Sort_old("/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId",
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId.sorted");
	}*/

	public static void Sort_old(String pIdFile, String tarFile)
	{
		uFunc.deleteFile(tarFile);
		uFunc.deleteFile(tarFile + ".tmp");
		uFunc.deleteFile(tarFile + ".tmp2");
		BufferedReader br = uFunc.getBufferedReader(pIdFile);
		String oneLine = "";
		String output = "";
		int RcdNr = 0;
		try {
			while((oneLine = br.readLine()) != null)
			{
				if(oneLine.equals(""))
					break;
				
				long pId = Long.parseLong(oneLine);
				
				String info = "";
				while((oneLine = br.readLine()) != null)
				{
					if(oneLine.equals(""))
						break;
					while(oneLine.endsWith("##"))
						oneLine = oneLine.substring(0, oneLine.length() - 2);
					info += oneLine.replaceAll("\t", "\\$\\$\\$") + "####";
				}
				output += pId + "\t" + info + "\n";
				RcdNr ++;
				if(RcdNr % 1000 == 0)
				{
					uFunc.addFile(output, tarFile + ".tmp");
					output = "";
				}
			}
			uFunc.addFile(output, tarFile + ".tmp");
			System.out.println("PreId format finided");
			
			QsortPair.SortPair(tarFile + ".tmp", tarFile + ".tmp2", false,
					true, RcdNr + 100);
			System.out.println("Qsort format finided");
			
			br = uFunc.getBufferedReader(tarFile + ".tmp2");
			output = "";
			RcdNr = 0;
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				long pId = Long.parseLong(ss[0]);
				output += pId + "\n";
				for(String triple : ss[1].split("####"))
				{
					// col may have single or 3 eles
					// "UpperTitle:****" or "pageid\tpredicate\tobject"
					String [] col = triple.split("\\$\\$\\$");
					output += col[0];
					for(int i = 1; i < col.length; i ++)
						output += "\t" + col[i];
					output += "\n";
				}
				output += "\n";
				RcdNr ++;
				if(RcdNr % 1000 == 0)
				{
					uFunc.addFile(output, tarFile);
					output = "";
				}
			}
			uFunc.addFile(output, tarFile);
			uFunc.deleteFile(tarFile + ".tmp");
			uFunc.deleteFile(tarFile + ".tmp2");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void Generate(Mysql m, String tableName,
			String tgPredIdFile) {
		// TODO Auto-generated method stub
		String sql = "insert into " + tableName + "(id, Content, Pinyin, word2vec, Freq)"
				+ " values (?, ?, ?, ?, ?)";
		try {
			m.Query = m.conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Mysql tripleQuery = new Mysql("hzWikiCount2", null);
		String query = "select Predicate from hzTriple";
		tripleQuery.SetLargeQuery(query);
		ResultSet rs;
		HashMap<String, Integer> preds = new HashMap<String, Integer>();
		HashMap<String, Integer> predId = new HashMap<String, Integer>();
		
		try {
			int Nr = 1;
			rs = tripleQuery.Query.executeQuery();
			while(rs.next())
			{
				String predicate = uFunc.Simplify(rs.getString(1));
				preds.put(predicate, 0);
			}
			String info = "predicate nr:" + preds.size() + "\nbegin insert...";
			System.out.println(info);
			
			Iterator<Entry<String, Integer>> it = preds.entrySet().iterator();
			while(it.hasNext())
			{
				Entry<String, Integer> next = it.next();
				String word = next.getKey();
				String pinyin = PinyinSim.Get(word);
				StringBuffer w2v = new StringBuffer();
				float[] w2vs = word2vec.getVector(word);
				if(w2vs != null){
					for(float f : w2vs)
						w2v.append(f + ",");
				}
				String w = w2v.length() > 0 ? w2v.toString(): null;
				
				predId.put(word, Nr);
				m.Query.setInt(1, Nr);
				m.Query.setString(2, word);
				m.Query.setString(3, pinyin);
				m.Query.setString(4, w);
				m.Query.setInt(5, 0);
				m.Query.addBatch();
				m.Query.executeBatch();
				Nr ++;
				
			}
			info = "predicat record inserted!";
			uFunc.SaveHashMap(predId, tgPredIdFile);
			System.out.println(info);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
