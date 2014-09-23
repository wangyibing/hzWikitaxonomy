package normalization;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

import tools.uFunc;


public class ExtractPlainText {
	static String info = "";

	public static void main(String [] args)
	{
		/*
		extractFromWeb(
				"/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/FileSplit/",
				1003,
				"/home/hanzhe/Public/result_hz/zhwiki/data2/text");
			*/	
		TextCleaning("/home/hanzhe/Public/result_hz/zhwiki/data2/tmp");
	}
	
	private static void TextCleaning(String path) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(path);
		String oneLine = "";
		Vector<String> chars = new Vector<String>();
		try {
			while((oneLine = br.readLine()) != null)
			{
				if((oneLine.contains("&")))
				{
					boolean existed = false;
					for(int i = 0; i < chars.size(); i ++)
					{
						if(oneLine.contains(chars.get(i)))
						{
							existed = true;
							break;
						}
					}
					if(existed == false)
					{
						//System.out.println(oneLine);
						int index = oneLine.indexOf("&");
						int end = oneLine.indexOf(";", index);
						if(end > index)
						{
							String s =oneLine.substring(index, end + 1);
							System.out.println(s);
							chars.add(s);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void extractFromWeb(String folder, int folderNr, String tar)
	{
		int pageNr = 0;
		uFunc.AlertOutput = "data/info/ExtractPlainTextInfo";
		uFunc.deleteFile(uFunc.AlertPath);
		long time = System.currentTimeMillis();
		uFunc.deleteFile(tar);
		String output = "";
		for(int i = 1 ; i <= folderNr; i ++)
		{
			File subFolder = new File(folder + i);
			if(subFolder.exists() && subFolder.isDirectory())
			{
				for(File page : subFolder.listFiles())
				{
					//System.out.println(page.getName());
					//if(page.getName().contains("1001962") == false)
					//	continue;
					int pageid = 0;
					String pName = page.getName();
					if(page.getName().contains("_"))
						pageid = Integer.parseInt(
								pName.substring(0, pName.indexOf("_")));
					
					String pageCont = GetPageContent(page.getAbsolutePath());
					if(pageCont == null || pageid == 0)
					{
						info = "page error:" + page.getName();
						uFunc.Alert(true, "ExtractPlainText", info);
					}
					String result = pageCont.replaceAll("(\n)+", "\n");
					output += "#PageId:" + pageid + "\n"
							+ result + "\n\n\n";
					pageNr ++;
					if(pageNr % 100 == 0)
					{
						uFunc.addFile(output, tar);
						output = "";
					}
					//System.exit(0);
				}
			}
			info = "folder" + i + ": " + "pageNr:" + pageNr + "\tcost:" +
					(System.currentTimeMillis() - time)/1000 + "sec ";
			time = System.currentTimeMillis();
			uFunc.Alert(true, "ExtractPlainText", info);
		}
		uFunc.addFile(output, tar);
		uFunc.AlertClose();
	}
	private static String GetPageContent(String absolutePath) {
		// TODO Auto-generated method stub
		Parser parser;
		try {
			parser = new Parser(absolutePath);
			parser.setEncoding("UTF-8");
			// get DIV:mw-content-text
			NodeList context = parser.parse(contextFilter);
			//System.out.println(context.asString());
			// remove edit and Ref part
			//context.keepAllNodesThatMatch(arg0, arg1);
			context.keepAllNodesThatMatch(
					contextFilter2, true);
			context.visitAllNodesWith(contentVisitor);
			//System.out.println(cont);
			return cont;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			uFunc.Alert(true, "error", absolutePath);
			System.out.println(absolutePath);
			return null;
		}
		
	}
	static String cont = "";
	static boolean lastTagRemove = false;
	final static NodeFilter contextFilter = 
			new HasAttributeFilter( "id", "mw-content-text");
	static final NodeFilter contextFilter2 = new NodeFilter(){
		public boolean accept(Node node){
			if(node instanceof TagNode)
			{
				TagNode tag = ((TagNode)node);
				String className = tag.getAttribute("CLASS");
				if(className == null)
					return true;
				className = className.toLowerCase();
				if(className.contains("metadata") ||
						className.contains("reference") ||
						className.contains("reflist") ||
						className.contains("infobox") ||
						className.contains("printfooter") ||
						className.contains("external") ||
						className.contains("navbox") ||
						className.equals("toc") ||
						className.contains("editsection"))
				{
					//System.out.println("###################:" + node.toHtml());
					return false;
				}
				// <a href="#cite_note-jewishAsia-10">[10]</a>
				String href = tag.getAttribute("HREF"); 
				if(href != null && href.startsWith("#"))
				{
					//System.out.println("###################:" + node.toHtml());
					return false;
				}
				//if(tag.getTagName().toLowerCase().equals("A"))
				//	System.out.println("###################:" + node.toHtml());
					
			}
			return true;
		}
	};
	
	static String lastTag = null;
	static String lastTag2 = null;
	static Vector<String> THs = new Vector<String>();
	static int ThIdx = 0;
	static boolean inTH = false;
	static boolean outTH = true;
	static int TdIdx = 0;
	static boolean inTD = false;
	static boolean outTD = true;
	static boolean TdEmpty = true;
	static boolean newTR = false;
	
	
	
	private static final NodeVisitor contentVisitor = new NodeVisitor(){

		public void visitTag(Tag tag) {
			String tagName = tag.getTagName();
			if(tagName.equals("A") && (
					tag.getAttribute("HREF") == null ||
					tag.getAttribute("HREF").startsWith("#")))
			{
				//System.out.println("###################:" + tag.toHtml());
				lastTagRemove = true;
			}
			else lastTagRemove = false;
			if(tagName.equals("TH"))
			{
				if(lastTag.equals("TH"))
					ThIdx ++;
				else{
					ThIdx = 0;
					THs.clear();
				}
				inTH = true;
				if(outTH == false)
				{
					info = "TH not standd:" + tag.toPlainTextString();
					uFunc.Alert(true, "", info);
					//System.exit(0);
				}
				outTH = false;
			}
			if(tagName.equals("TD"))
			{
				if(lastTag.equals("TD"))
					TdIdx ++;
				else
					TdIdx = 0;
				TdEmpty = true;
				inTD = true;
				if(outTD == false)
				{
					//info = "TD not standd:" + tag.toPlainTextString();
					//uFunc.Alert(true, "", info);
				}
				outTD = false;
			}
			if(tagName.equals("TABLE"))
			{
				THs.clear();
				ThIdx = 0;
			}
			if(tagName.equals("TR"))
			{
				TdIdx = 0;
				TdEmpty = true;
				newTR = false;
			}
			if(tagName.equals("BR") == false)
			{
				lastTag2 = lastTag;
				lastTag = tagName;
			}
			
		}
		public void visitStringNode (Text string)
		{
			String text = string.getText().replaceAll("(&#160;)||", "")
					.replaceAll("(&amp;)", "&").replaceAll("&lt;", "<")
					.replaceAll("&gt;", ">");
			if(inTH = true && outTH == false)
			{
				text = text.replaceAll("(\n)|(\r\n)", "");
				if(ThIdx == THs.size())
					THs.add(text);
				else if(ThIdx == THs.size() - 1)
				{
					text = THs.remove(ThIdx) + text;
					THs.add(text);
				}
				/*
				else{
					info = "TH error:" + ThIdx + ";" + THs.size() + "\t" + text;
					uFunc.Alert(true, "", info);
					System.exit(0);
				}
				System.out.println("###########" + ThIdx + "\"\t\"" + THs.get(ThIdx) + "\"");
				*/
			}
			else if(lastTag.equals("TD"))
			{
				if(TdIdx >= THs.size() && THs.size() > 0)
				{
					//info = "TD error:" + TdIdx + "\t" + THs.size() + "\t" + text;
					//uFunc.Alert(true, "", info);
					THs.clear();
					cont += "\n";
					TdIdx = 0;
					TdEmpty = true;
					//System.exit(0);
				}
				if(TdEmpty == true)
				{
					if(THs.size() > 0)
						cont += "##" + THs.get(TdIdx) + "：" + text.replaceAll("\n", "");
					else cont += "##" + text.replaceAll("\n", "");
					TdEmpty = false;
				}
				else{
					cont += text.replaceAll("\n", "");
				}
				
			}
			//System.out.println("string###################:" + text);
			else{
				if(lastTagRemove == false){
					//System.out.println("in###################:" + text);
					cont += text;
				}
				TdIdx = 0;
			}
			
			//else System.out.println("string###################:" + text);
		}
		public void visitRemarkNode (Remark remark){}
		public void beginParsing(){
			cont = "";
			inTH = false;
			outTH = true;
			inTD = false;
			outTD = true;
			TdEmpty = true;
			newTR = false;
		}
		public void visitEndTag (Tag tag){
			lastTagRemove = false;
			String tagName = tag.getTagName();
			if(tagName.equals("TH"))
			{
				outTH = true;
			}
			if(tagName.equals("TD"))
				outTD = true;
		}
		public void finishedParsing(){}
	};
}
