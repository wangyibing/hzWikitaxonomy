package extract.web;

import java.net.URL;
import java.net.URLConnection;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import tools.uFunc;
public class ExtractAPI {
	public static String i = "ExtractAPI";

	public static int GetPageId(String title)
	{
		int pageid = 0;
		try {
			SAXBuilder  builder = new SAXBuilder();
			title = title.replaceAll("\\s*\\(", "_\\(")
					.replaceAll("\\s+", "_");
			URL url = new URL("http://zh.wikipedia.org/w/api.php?action=query"
					+ "&titles=" + title + "&format=xml");
			System.out.println(url);
			URLConnection connection = url.openConnection();
			Document doc = builder.build(connection.getInputStream());
			Element root = doc.getRootElement();
			Element ele = root.getChild("query");
			ele = ele.getChild("pages");
			ele = ele.getChildren().get(0);
			String id = ele.getAttributeValue("pageid");
			if(id == null)
				return 0;
			pageid = Integer.parseInt(id);
			uFunc.Alert(i, "targetId: " + pageid + "\t" + title);
		} catch (Exception e1) {
			uFunc.Alert(i, "error:" + title);
			e1.printStackTrace();
		}
		return pageid;
	}
}
