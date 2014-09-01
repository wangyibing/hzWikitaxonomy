package demo.prefuse;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import database.Entity;
import database.RediPage;
import extract.Extract;
import tools.uFunc;

public class GeneXML {

	public static String XMLFilePath = 
			"/home/hanzhe/github/hzWikitaxonomy/data/DEMO_XML_prefuse.xml";
	public static void main(String [] args)
	{
		GenerateXML(DataPurge.SelectedTriplesPath, XMLFilePath, 10);
	}
	
	

	static Document Doc=new Document();
	static Element graphml = new Element("graphml");
	static Element graph = new Element("graph");

	public static void GenerateXML(String triplePath, String xMLFilePath2, int SubjTriNrThres) {
		// TODO Auto-generated method stub
		CreateBaseXMLfile();
		AddNodeInfo(triplePath);
		MakeXML(SubjTriNrThres, triplePath);
		XMLoutput(xMLFilePath2);
	}
	
	
	private static void XMLoutput(String OutputPath) {
		// TODO Auto-generated method stub
		try{
			XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());
			outputer.output(Doc, new FileOutputStream(OutputPath));
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	private static void MakeXML(int subjTriNrThres, String path) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(path);
		String oneLine = "";
		int LineNr = 0;
		try {
			while((oneLine = br.readLine()) != null)
			{
				oneLine = uFunc.Simplify(oneLine);
				String [] ss = oneLine.split("\t");
				if(ss.length < 3)
					continue;
				if(ss[1].length() > 20)
					continue;
				LineNr ++;
				if(LineNr > 100000)
					break;
				
				boolean exit = false;
				for(int i = 0 ; i < 3; i ++)
				{
					if(ss[i].startsWith("[") && ss[i].endsWith("]"))
					{
						int id = Entity.getEntityId(ss[i].replaceAll("(?m)^\\[+", "").replaceAll("(?m)\\]+$", ""));
						if(id <= 0)
						{
							exit = true;
							break;
						}
						
					}
				}
				if(exit == true)
					continue;
				addOneTriple2XML(oneLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void AddNodeInfo(String triplePath) {
		// TODO Auto-generated method stub
		HashMap<Integer, String> EntityTits = 
				new HashMap<Integer, String>();
		BufferedReader br = uFunc.getBufferedReader(triplePath);
		String oneLine = "";
		int LineNr = 0 ; 
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 3)
					continue;
				if(ss[1].length() > 20)
					continue;
				LineNr ++;
				if(LineNr > 100000)
					break;
				
				ss[1] = ss[1].replaceAll("(?m)^\\[+", "").replaceAll("(?m)\\]+$", "");
				int pageid = Integer.parseInt(ss[0]);
				if(RediPage.getTargetPageid(pageid) > 0)
					pageid = RediPage.getTargetPageid(pageid);
				EntityTits.put(pageid, Entity.getEntityTitle(pageid));
				if(pageid == 103352)
					System.out.println(Entity.getEntityTitle(pageid));
				
				if(ss[2].matches("(?m)^\\[.+\\]$") == true)
				{
					String title = ss[2].substring(1, ss[2].length() - 1);
					int id = Entity.getEntityId(title);
					if(id > 0)
						EntityTits.put(id, title);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("total entity node Nr:" + EntityTits.size());
		
		Iterator<Entry<Integer, String>> it = 
				EntityTits.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<Integer, String> next = it.next();
			addOneNode(next.getKey(), next.getValue(), 1);
			if(next.getKey() == 103352)
				System.out.println(103352);
		}
		
	}


	/**
	 * for format definition info and node definition
	 * 
	 * 
	 */
	private static void CreateBaseXMLfile() {
		// TODO Auto-generated method stub
		graphml.setAttribute("wxmlns", "http://graphml.graphdrawing.org/xmlns");
		Doc.addContent(graphml);
		
		graph.setAttribute("edgedefault", "directed");
		graphml.addContent(graph);

		
		Element key = new Element("key");
		key.setAttribute("id", "name");
		key.setAttribute("for", "node");
		key.setAttribute("attr.name", "name");
		key.setAttribute("attr.type", "string");
		graph.addContent(key);
		
		key = new Element("key");
		key.setAttribute("id", "gender");
		key.setAttribute("for", "node");
		key.setAttribute("attr.name", "gender");
		key.setAttribute("attr.type", "integer");
		graph.addContent(key);

	}
	

	// enough for entities already
	static int nodeNr = 4000000;
	public static HashMap<String, Integer> PO2Id = 
			new HashMap<String, Integer>();
	private static boolean addOneTriple2XML(String oneLine) {
		// TODO Auto-generated method stub
		try{
			String [] ss = oneLine.split("\t");
			ss[1] = ss[1].replaceAll("(?m)^\\[+", "").replaceAll("(?m)\\]+$", "");
			if(ss[2].contains("[")){
				if(Entity.getEntityId(ss[2].replaceAll("(?m)^\\[+", "").replaceAll("(?m)\\]+$", "")) <= 0)
					return false;
			}
			//tripleIdlist.put(ss[0].substring(2, ss[0].length()-2), 0);
			// predicate
			int pId = nodeNr;
			if(PO2Id.containsKey(ss[1]+"="+ss[2]))
				pId = PO2Id.get(ss[1]+"="+ss[2]);
			else{
				addOneNode(nodeNr, ss[1], 2);
				nodeNr ++;
			}
			
			int SujId = Integer.parseInt((ss[0].replaceAll("(?m)^\\[+", "").replaceAll("(?m)\\]+$", "")));
			if(SujId == 0)
			{
				System.out.println("SujId = 0:" + ss[0]);
			}
			if(RediPage.getTargetPageid(SujId) > 0)
				SujId = RediPage.getTargetPageid(SujId);
			AddOneEdge(SujId, pId);
			
			// exist the pair: predicate-object, don't
			// need to add another line
			if(PO2Id.containsKey(ss[1]+"="+ss[2]) == true)
				return true;

			int ObjId = nodeNr;
			nodeNr ++;
			if(ss[2].contains("[") && ss[2].contains("]")){
				ObjId = Entity.getEntityId(ss[2].replaceAll("(?m)^\\[+", "")
						.replaceAll("(?m)\\]+$", ""));
				if(ObjId <= 0)
				{
					ObjId = nodeNr - 1;
					addOneNode(ObjId, ss[2], 0);
					//System.out.println("wrong obj:" + oneLine + "\t" + ss[2].replaceAll("(?m)^\\[+", "").replaceAll("(?m)\\]+$", ""));
				}
				else
				{
					nodeNr --;
				}
			}
			else
			{
				addOneNode(ObjId, ss[2], 0);
			}
			AddOneEdge(pId, ObjId);
			PO2Id.put(ss[1]+"="+ss[2], pId);
			return true;
		}catch(Exception e){
			return false;
		}
	}


	
	private static void addOneNode(int newId, String title,
			int gender) {
		// TODO Auto-generated method stub
		Element node = new Element("node");
		node.setAttribute("id", newId+"");
		Element data = new Element("data");
		data.setAttribute("key", "name");
		data.setText(title);
		node.addContent(data);
		data = new Element("data");
		data.setAttribute("key", "gender");
		data.setText(gender +"");
		node.addContent(data);
		//System.out.println("node");
		graph.addContent(node);
		
	}

	private static void AddOneEdge(int source, int target) {
		// TODO Auto-generated method stub
		Element edge = new Element("edge");
		edge.setAttribute("source", (source)+"");
		edge.setAttribute("target", target+"");
		edge.setText("");
		//System.out.println("edge");
		graph.addContent(edge);
		
	}
}
