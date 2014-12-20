package extract.triple;

import java.sql.SQLException;

import org.htmlparser.Tag;

import tools.Mysql;

import com.tag.myElement;

import database.Entity;

public class Triple2Mysql {
	public static Mysql m = new Mysql("hzWikiCount2", null);
	public static boolean inited = false;
	static int batchNr = 0; 

	public static void insert(int pageid, String contP, String contO,
			Tag objTag, String remark, myElement upperTitle, int tRtagId) {
		// TODO Auto-generated method stub
		init("hzTriple");
		try {
			m.Query.setInt(1, pageid);
			m.Query.setString(4, Entity.getTitle(pageid));
			
			if(contP.contains("->")){
				int pid = Integer.parseInt(contP.substring(
						contP.indexOf("->") + 3, contP.length() - 1));
				m.Query.setInt(2, pid);
				contP = contP.substring(0, contP.indexOf("->"));
			}
			else m.Query.setInt(2, 0);
			
			m.Query.setString(5, contP);
			
			if(contO.contains("->")){
				int pid = Integer.parseInt(contO.substring(
						contO.indexOf("->") + 3, contO.length() - 1));
				m.Query.setInt(3, pid);
				contO = contO.substring(0, contO.indexOf("->"));
			}
			else m.Query.setInt(3, 0);
			
			m.Query.setString(6, contO);
			
			if(upperTitle != null){
				String us = upperTitle.getStringFromMyelement(null, true);
				if(us.contains("->")){
					int uid = Integer.parseInt(us.substring(
							us.indexOf("->") + 3, us.length() - 1));
					m.Query.setInt(8, uid);
					us = us.substring(0, us.indexOf("->"));
				}
				else m.Query.setInt(8, 0);
				
				m.Query.setString(7, us);
			}
			else{
				m.Query.setInt(8, 0);
				m.Query.setString(7, null);
			}
			
			m.Query.setString(9, objTag.toPlainTextString());
			m.Query.setString(10, objTag.toHtml());
			m.Query.setInt(11, tRtagId);
			m.Query.setString(12, remark);
			m.Query.addBatch();
			m.Query.executeBatch();
			batchNr ++;
			if(batchNr % 10000 == 0)
				System.out.println(batchNr + " batchNr inserted into mysql");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("mysql inser error:" + pageid + "\t" + contP + "\t" + contO);
		}
	}

	private static void init(String tName) {
		// TODO Auto-generated method stub
		if(inited) return;
		m.execute("data/sql/CreatehzTriple.sql");
		String sql = "insert into " + tName + " (SubId, PredId, ObjId, Subject, "
				+ "Predicate, Object, UpperTitle, UpperTitleId, OriginalObj, OriginalTRtag, "
				+ "TRtagid, Note) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		try {
			m.Query = m.conn.prepareStatement(sql);
			System.out.println("hzTriple.table in mysql inited!");
			inited = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("hzTriple.table in mysql init failed!");
		}
	}

}
