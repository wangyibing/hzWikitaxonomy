package word2vec.tripleFile;

import java.sql.ResultSet;
import java.sql.SQLException;

import tools.Mysql;
import tools.uFunc;

public class GenerateSrc {
	public static String splitedTagedMerge = "/home/hanzhe/Public/result_hz/wiki_count2/pageinfo/segment/Segment_wikitext";

	public static void main(String [] args)
	{
		Do("/home/hanzhe/Public/result_hz/wiki_count2/word2vec/tripleData");
	}

	private static void Do(String path) {
		// TODO Auto-generated method stub
		Mysql m = new Mysql("hzWikiCount2", null);
		String sql = "select SUbject, Predicate, Object from hzTriple";
		m.SetLargeQuery(sql);
		ResultSet rs = null;
		try {
			rs = m.Query.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		StringBuffer out = new StringBuffer();
		uFunc.deleteFile(path);
		int outNr = 0;
		String lastSub = "";
		try {
			while(rs.next()){
				if(rs.getString(1).equals(lastSub) == false)
				{
					out.append("\n\n");
					lastSub = rs.getString(1);
				}
				
				out.append(rs.getString(1) + " " + rs.getString(2) + " " +rs.getString(3) + "\n");
				outNr ++;
				if(outNr % 1000 == 0){
					uFunc.addFile(out.toString(), path);
					out.setLength(0);
				}
			}
			uFunc.addFile(out.toString(), path);
			System.out.println("total triple NR : " + outNr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
