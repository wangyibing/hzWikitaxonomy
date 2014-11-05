package zzzzzz;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import tools.Mysql;
import database.Entity;

public class Xml2Ids {

	public static String XMLFile = 
			"/home/hanzhe/Public/result_hz/XinhuaNews/match.xml";
	
	public static void main(String [] args)
	{
		Mysql m = new Mysql("hzWikiCount2", null);
		String sql = "select count(*) from hzTriple where SubId = ? ";
		try {
			m.Query = m.conn.prepareStatement(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		SAXBuilder  builder = new SAXBuilder();
		try {
			Document doc = builder.build(new File(XMLFile));
			Element topics = doc.getRootElement();
			for(Element topic : topics.getChildren()){
				for(Element hotWord : topic.getChildren()){
					if(hotWord.getName().equals("hotWord"))
					{
						String oneLine = hotWord.getText();
						System.out.println(hotWord.getText());
						String [] ss = oneLine.split("\t");
						int n  = 0;
						for(int i = 0 ; i < ss.length; i ++){
							String [] sss = ss[i].split("/");
							int pageid = Entity.getId(sss[0]);
							int times = Integer.parseInt(sss[1]);
							if(pageid > 0){
								m.Query.setInt(1, pageid);
								m.Query.addBatch();
								ResultSet r = m.Query.executeQuery();
								if(r.next())
									times = r.getInt(1);
								if(times <= 0)
									continue;
								n++;
								if(n > 10)
									break;
								System.out.println(sss[0] + "\t" + pageid);
							}
						}
						System.out.println();
					}
						
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Mysql m2 = new Mysql("hzWikiCount2", null);
	public static int getKeywordPageid(String cont)
	{

		try {
			ResultSet result = m2.Query.executeQuery(
					"select entityList, FreqList from Hz_KPP where keyword='" + cont + "'");
			if(result.next())
			{
				String []entities = result.getString(1).split(",");
				String []freqs = result.getString(2).split(",");
				int max = 0;
				int maxEntity = 0;
				for(int i = 0 ; i < entities.length; i ++)
				{
					int freq = Integer.parseInt(freqs[i]);
					if(freq > max)
					{
						max = freq;
						maxEntity = Integer.parseInt(entities[i]);
					}
				}
				System.out.println(maxEntity);
				return maxEntity;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return 0;
	}
}
