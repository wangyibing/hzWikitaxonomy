package com.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import tools.uFunc;

public class myPredicate {
	public static String i = "myPredicate";
	public static String info = "";

	//**********Base info**************
	public long id; /*id in mysql */
	public long Predid;
	public String Content;
	public String Link;
	public int LinkId;
	public String UpperTitle;
	public int Pageid;
	public Vector<String> Objs;
	//**********Additional info**************
	// infobox's name of wikidumps
	public String InfoboxName;
	public String WikitextContent;
	
	public myPredicate(long predid, int pageid)
	{
		Predid = predid;
		Content = null;
		Link = null;
		UpperTitle = null;
		Pageid = pageid;
		Objs = new Vector<String>();
	}
	public myPredicate(int pId, String cont, String link, String upper,
			int pageid, Vector<String> objs)
	{
		Predid = pId;
		Content = cont;
		Link = link;
		UpperTitle = upper;
		Pageid = pageid;
		Objs = new Vector<String>();
		for(String o : objs)
			Objs.add(o);
	}
	public void CompleteInfo_Triple(BufferedReader brP) {
		// TODO Auto-generated method stub
		String oneLine = "";
		try {
			while((oneLine = brP.readLine()) != null)
			{
				if(oneLine.equals(""))
					break;
				if(oneLine.startsWith("UpperTitle:"))
				{
					if(oneLine.equals("UpperTitle:null"))
						UpperTitle = null;
					else UpperTitle = oneLine.substring(11);
				}
				else if((oneLine.charAt(0) + "").matches("[0-9]"))
				{
					String [] ss = oneLine.split("\t");
					if(ss.length < 2)
						continue;
					if(ss.length < 3)
					{
						//System.out.println(oneLine);
						continue;
					}
					String link = null;
					String cont = null;
					if(ss[1].contains("->"))
					{
						link = ss[1].substring(ss[1].indexOf("->") + 2);
						cont = ss[1].substring(0, ss[1].indexOf("->"));
					}
					else
						cont = ss[1];
					if(Content == null)
						Content = cont;
					else if(Content.equals(cont) == false)
					{
						info = "pred cont not unified:" + Predid + "\t" +
								Content + ";" + cont;
						uFunc.Alert(true, i, info);
					}
					if(Link == null)
						Link = link;
					else if(link !=null &&Link.equals(link) == false)
					{
						info = "pred link not unified:" + Predid + "\t" +
								Link + ";" + link;
						uFunc.Alert(true, i, info);
					}
					Objs.add(ss[2]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString(){
		String info = "";
		info = Predid + "\n" + 
				"Contnt:" + Content + "\n" +
				"Link:" + Link + "\n" + 
				"UpperTitle:" + UpperTitle + "\n";
		info += "Objcs:";
		for(String tr : Objs)
			info += "####" + tr;
		info += "\n";
		info += "InfoboxName:" + InfoboxName + "\n";
		info += "WikitextCont:" + WikitextContent + "\n";
		info += "\n";
		return info;
	}
	public void CompleteInfo(BufferedReader br) {
		// TODO Auto-generated method stub
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				if(oneLine.equals(""))
					break;
				if(oneLine.startsWith("UpperTitle:"))
				{
					if(oneLine.equals("UpperTitle:null"))
						UpperTitle = null;
					else UpperTitle = oneLine.substring(11);
				}
				else if(oneLine.startsWith("Contnt:"))
				{
					Content = oneLine.substring(7);
					if(Content.equals("null"))
						Content = null;
				}
				else if(oneLine.startsWith("Link:"))
				{
					Link = oneLine.substring(5);
					if(Link.equals("null"))
						Link = null;
				}
				else if(oneLine.startsWith("Objcs:"))
				{
					for(String obj :oneLine.substring(6).split("####"))
					{
						if(obj.equals(""))
							continue;
						Objs.add(obj);
					}
				}
				else if(oneLine.startsWith("Link:"))
				{
					Link = oneLine.substring(5);
					if(Link.equals("null"))
						Link = null;
				}
				else if(oneLine.startsWith("InfoboxName:"))
				{
					InfoboxName = oneLine.substring(12);
					if(InfoboxName.equals("null"))
						InfoboxName = null;
				}
				else if(oneLine.startsWith("WikitextCont:"))
				{
					WikitextContent = oneLine.substring(13);
					if(WikitextContent.equals("null"))
						WikitextContent = null;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/*//**********Base info**************
	public long Predid;
	public String Content;
	public String Link;
	public String UpperTitle;
	public int Pageid;
	public Vector<String> Objs;
	//**********Additional info**************
	// infobox's name of wikidumps
	public String InfoboxName;
	public String WikitextContent;*/
	
	// select id, SubId, PredId, ObjId, Subject, Predicate, Object, UpperTitle, 
	// OriginalObj, Content_wikitext, Note from hzTriple where SubId = ?
	public void CompleteInfo(ResultSet rs) {
		// TODO Auto-generated method stub
		try {
			id = rs.getLong("id");
			Content = rs.getString("Predicate");
			LinkId = rs.getInt("PredId");
			UpperTitle = rs.getString("UpperTitle");
			Objs = new Vector<String>();
			Objs.add(rs.getString("Object"));
			/*WikitextContent = rs.getString("Content_wikitext");
			if(WikitextContent != null && WikitextContent.equals("null"))
				WikitextContent = null;*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
