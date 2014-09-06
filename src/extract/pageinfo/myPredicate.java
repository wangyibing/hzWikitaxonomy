package extract.pageinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

import tools.uFunc;

public class myPredicate {
	public static String i = "myPredicate";
	public static String info = "";

	//**********Base info**************
	public long Predid;
	public String Content;
	public String Link;
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
	public void CompleteInfo(BufferedReader brP) {
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
			info += "\t" + tr;
		info += "InfoboxName:" + InfoboxName + "\n";
		info += "WikitextCont:" + WikitextContent + "\n";
		info += "\n";
		return info;
	}
}
