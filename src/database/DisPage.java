package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;

public class DisPage {
	public static final String ExceptionListPath = 
			"data/DisPageExcepList";
	public static final String CanonicalPath_pagelist = 
			"data/DisPages";
	public static final String CanonicalPath_pageoutlinks = 
			"data/DisPageOutLinks";
	
	/**
	 * <291, 411122,880679,3219576>
	 */
	public static String GetOutlinks(int pageid)
	{
		if(Inited == false)
			InitDisOutlinks();
		if(DisOutLinks.containsKey(pageid))
			return DisOutLinks.get(pageid);
		else
			return null;
	}

	public static String isDisaPage(int pageid)
	{
		if(listInited == false)
			InitDisList();
		if(DispageList.containsKey(pageid))
			return DispageList.get(pageid);
		else return null;
	}
	
	private static HashMap<Integer, String> DispageList = 
			new HashMap<Integer, String>();
	private static boolean listInited = false;
	private static void InitDisList() {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(
				CanonicalPath_pagelist);
		String oneLine = "";
		try {
			int lastId = 0;
			int pageid = 0;
			String lastLinks = "";
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				pageid = Integer.parseInt(ss[0]);
				if(pageid != lastId)
				{
					if(lastLinks.equals("") == false)
					{
						DisOutLinks.put(lastId, lastLinks);
					}
					lastLinks = ss[1];
				}
				else
				{
					lastLinks += ";" + ss[1];
				}
				lastId = pageid;
			}
			if(lastLinks.equals("") == false)
			{
				DisOutLinks.put(pageid, lastLinks);
			}
			System.out.println("Disambiguation pagelist loaded, size:"
					+ DisOutLinks.size());
			listInited = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static HashMap<Integer, String> DisOutLinks = 
			new HashMap<Integer, String>();
	private static boolean Inited = false;
	private static void InitDisOutlinks() {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(
				CanonicalPath_pageoutlinks);
		String oneLine = "";
		try {
			int lastId = 0;
			int pageid = 0;
			String lastLinks = "";
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				pageid = Integer.parseInt(ss[0]);
				if(pageid != lastId)
				{
					if(lastLinks.equals("") == false)
					{
						DisOutLinks.put(lastId, lastLinks);
					}
					lastLinks = ss[1];
				}
				else
				{
					lastLinks += ";" + ss[1];
				}
				lastId = pageid;
			}
			if(lastLinks.equals("") == false)
			{
				DisOutLinks.put(pageid, lastLinks);
			}
			System.out.println("Disambiguation infos loaded, size:"
					+ DisOutLinks.size());
			Inited = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
