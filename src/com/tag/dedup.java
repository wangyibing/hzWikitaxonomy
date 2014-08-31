package com.tag;

import org.htmlparser.Node;

public class dedup {

	public static boolean isFather(Node father, Node son)
	{
		if(father.equals(son))
			return true;
		Node tmp = father.getFirstChild();
		while(tmp != null)
		{
			if(isFather(tmp, son) == true)
				return true;
			tmp = tmp.getNextSibling();
		}
		return false;
	}
}
