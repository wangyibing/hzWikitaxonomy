package tools;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Tag;

import com.spreada.utils.chinese.ZHConverter;

/**
 * -Xms1024m -Xmx4096m
 * @author Administrator
 *
 */
public class uFunc {

	public static String WikitextTriplePath = 
			"/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Triple.txt";
	public static String WebPagesFolder = 
			"/home/hanzhe/Public/result_hz/zhwiki/ZhwikiWebPages/FileSplit/";
	public static String testFolder = 
			"/home/hanzhe/Public/result_hz/zhwiki/testInfo";
	public static String InfoFolder = 
			"/home/hanzhe/Public/result_hz/zhwiki/info";
	public static void OutputTagInfo(Tag tag, String errorInfo) {
		System.out.println("\n" + errorInfo + "\t");
		System.out.println("***begin***");
		System.out.println(tag.toHtml());
		System.out.println("*** end ***\n");
		
	}
	
	
	public static String RemovePunctuations(String string)
	{
		return string.replaceAll("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……"
				+ "&*（）——|{}【】‘；：”“'。，、？]", "")
				.replaceAll("(?m)^[•_-]+", "");
	}
	public static boolean isPunctuations(String string)
	{
		if(string == null)
			return false;
		if(string.equals(""))
			return true;
		return string.matches("[`~!@#$^&*()=|{}';',\\[\\].<>/?~！@#￥……"
				+ "&*（）——|{}【】《》，。？‘；”“'。，、？\n (and)和等_]{1,}");
	}
	/**
	 * full2half inside
	 * @param string
	 * @return
	 */
	public static boolean ContainPunctuation(String string)
	{
		if(string == null)
			return false;
		Pattern pattern=Pattern.compile(
				"[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……"
				+ "&*（）——|{}【】《》，。？‘；：”“'。，、？]");
		Matcher matcher = pattern.matcher(uFunc.full2HalfChange(string));
		if(matcher.find()){
			return true;
		}
		return false;
	}
	public static String ReplaceBoundSpace(String string){
		return string
				//replace the space at the beginning
				.replaceAll("(?m)^[‡\\*\\- _]+", "")
				// replace the space at the end
				.replaceAll("(?m)[ _:：‡\\*]+$", "")
				// replace two or more spaces with a single space
				.replaceAll(" {2,}", " ")
				//replace the space at the beginning
				.replaceAll("(?m)^_+", "")
				// replace the space at the end
				.replaceAll("(?m)_+$", "")
				// replace two or more spaces with a single space
				.replaceAll("_{2,}", " ");
	}

	public static boolean hasChineseCharactor(String string){
		Pattern pattern=Pattern.compile("[\\u4e00-\\u9fa5]");
		Matcher matcher=pattern.matcher(string);
		if(matcher.find()){
			return true;
		}
		return false;
	}
	
	public static void AddOneFile(String filePath, String targetPath){
		if(filePath == null || filePath.equals("")||
				targetPath == null || targetPath.equals(""))
			return;
		try{
			BufferedReader br = getBufferedReader(filePath);
			String oneLine = "";
			String result = "";
			int lineNr = 0;
			while( (oneLine = br.readLine())!= null){
				result += oneLine +"\n";
				lineNr ++;
				if(lineNr % 1000 == 0){
					addFile(result, targetPath);
					result = "";
				}
			}
			addFile(result, targetPath);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	private static ZHConverter SimConverter = 
			ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
	public static ZHConverter TraConverter = 
			ZHConverter.getInstance(ZHConverter.TRADITIONAL);
	
	public static boolean isURL(String str) {
		return str.matches("\\[http([^]]{1,})\\]");
	}
	public static boolean isNumeric(String str) {
		return str.replaceAll(",", "").matches("[\\+-]?[0-9]+((.)[0-9])*[0-9]*");
	}
	
	public static String PunctuationZh2En(String string){
		String s1 = string
				.replaceAll("，", ",").replaceAll("？", "?").replaceAll("！", "!")
				.replaceAll("；", ";").replaceAll("：", ":").replaceAll("”", "\"")
				.replaceAll("“", "\"").replaceAll("‘", "'").replaceAll("’", "'")
				.replaceAll("『", "\"").replaceAll("』", "\"").replaceAll("﹄", "\"")
				.replaceAll("﹃", "\"").replaceAll("「", "'").replaceAll("」", "'")
				.replaceAll("﹂", "'").replaceAll("﹁", "'").replaceAll("（", "(")
				.replaceAll("）", ")").replaceAll("——", "--").replaceAll("──", "--")
				.replaceAll("—", "--").replaceAll("《 ", "<<").replaceAll("》", ">>")
				.replaceAll("〈 ", "<").replaceAll("〉", ">").replaceAll("【", "[")
				.replaceAll("】", "]").replaceAll("～", "~");
		String result = s1;
		result = full2HalfChange(s1);
		return result;
	}
	
	// 全角转半角的 转换函数
	public static String full2HalfChange(String QJstr) {
		StringBuffer outStrBuf = new StringBuffer("");
		String Tstr = "";
		byte[] b = null;
		for (int i = 0; i < QJstr.length(); i++) {
			Tstr = QJstr.substring(i, i + 1);
			// 全角空格转换成半角空格
			if (Tstr.equals("　")) {
				outStrBuf.append(" ");
				continue;
			}
			try {
				b = Tstr.getBytes("unicode");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 得到 unicode 字节数据
			if (b[2] == -1) {
				// 表示全角？
				b[3] = (byte) (b[3] + 32);
				b[2] = 0;
				try {
					outStrBuf.append(new String(b, "unicode"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				outStrBuf.append(Tstr);
			}
		} // end for.
		return outStrBuf.toString();
	}
	
	public static String UnifiedSentence(String string){
		String s1 = string
				.replaceAll("，", ",").replaceAll("？", "?")
				.replaceAll("！", "!").replaceAll("；", ";")
				.replaceAll("：", ":").replaceAll("”", "\"")
				.replaceAll("“", "\"").replaceAll("‘", "'")
				.replaceAll("’", "'").replaceAll("『", "\"")
				.replaceAll("』", "\"").replaceAll("﹄", "\"")
				.replaceAll("﹃", "\"").replaceAll("「", "'")
				.replaceAll("」", "'").replaceAll("﹂", "'")
				.replaceAll("﹁", "'").replaceAll("（", "(")
				.replaceAll("）", ")").replaceAll("——", "--")
				.replaceAll("──", "--").replaceAll("—", "--")
				.replaceAll("《 ", "<<").replaceAll("》", ">>")
				.replaceAll("〈 ", "<").replaceAll("〉", ">")
				.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("～", "~");
		String result = s1;
		result = full2HalfChange(s1);
		return result;
	}
	
	public static void addFile(String string, String path) {
		if(string == null || string.equals(""))
			return;
		try{
			File file=new File(path);
			if(!file.exists()){
				if(file.createNewFile() == false)
					System.out.println("path not exist: " + path);
			}
			OutputStreamWriter osw=
					new OutputStreamWriter(new FileOutputStream(file,true),"utf-8");
			osw.append(string);
			osw.close();
		}catch(Exception e){
			System.out.println(path);
			e.printStackTrace();
		}		
	}
	public static void deleteFile(String path) {
		
		try{
			File file=new File(path);
			if(!file.exists()){
				System.out.println("file " + file.getName() + " not exist, can't delete!");
			}
			else if (file.isFile() == true){
				String name = file.getName();
				file.delete();
				System.out.println("delete " + name + " successfully!");
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public static BufferedReader getBufferedReader(String path) {
		try{
			File file = new File(path);
			if(file.exists() == false){
				System.out.println("read file not exist:"+
						path);
				return null;
			}
			BufferedReader reader = new BufferedReader(
					new FileReader(file));
			return reader;
		}catch( Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static boolean containNumber(String string) {
		for(char c : string.toCharArray()){
			if(c >= '0' && c <= '9')
				return true;
		}
		return false;
	}
	
	private static int fileNr;
	private static int CodelineNr;
	
	public static String OutputProjectInfo()
	{
		String result = "";
		File src = new File("/home/hanzhe/github/hzWikitaxonomy");
		uFunc.fileNr = 0;
		uFunc.CodelineNr = 0;
		
		if(src.isDirectory() == false)
		{
			result += "src folder error!\n";
		}
		else
		{
			for(File file : src.listFiles())
			{
				uFunc.CountCodelines(file);
			}
		}
		result += "filNr:" + fileNr + "\n";
		result += "codelineNr:" + CodelineNr + "\n";
		return result;
	}

	private static void CountCodelines(File file) {
		if(file.isFile() == true && file.getName().endsWith(".java"))
		{
			fileNr ++;
			System.out.println(file.getName());
			BufferedReader br = uFunc.getBufferedReader(
					file.getAbsolutePath());
			try {
				while((br.readLine() != null))
					uFunc.CodelineNr ++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(file.isDirectory() == true)
		{
			if(file.getName().equals("com"))
				return;
			for(File file1 : file.listFiles())
			{
				uFunc.CountCodelines(file1);
			}
		}
		else
		{
			//System.out.println("file error:" + file.getAbsolutePath());
		}
	}

	public static void SaveHashMap(HashMap<String, Integer> map, String path){
		uFunc.deleteFile(path);
		List<Entry<String, Integer>> infoIds =
			    new ArrayList<Entry<String, Integer>>(map.entrySet());
		Collections.sort(infoIds, new Comparator<Entry<String, Integer>>() {   
		    public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {      
		        //return (o2.getValue() - o1.getValue()); 
		        return (o2.getValue() - o1.getValue());
		    }
		});
		int size = 0;
		String output = "";
		for(int i = 0 ; i < infoIds.size() ;  i ++)
		{
			output += infoIds.get(i).getKey() + "\t" + 
					infoIds.get(i).getValue() + "\n";
			size ++;
			if(size % 1000 == 0){
				uFunc.addFile(output, path);
				output = "";
			}
		}
		uFunc.addFile(output, path);
	}

	public static void CalcuFreqDistr(HashMap<String, Integer> map2, String path){
		uFunc.deleteFile(path);
		HashMap<Integer, Integer> map = 
				new HashMap<Integer, Integer>();
		
		Iterator<Entry<String, Integer>> itSI = 
				map2.entrySet().iterator();
		while(itSI.hasNext())
		{
			Entry<String, Integer> next = 
					itSI.next();
			int freq = 1;
			if(map.containsKey(next.getValue()))
			{
				freq += map.remove(next.getValue());
			}
			map.put(next.getValue(), freq);
		}
		
		List<Entry<Integer, Integer>> infoIds =
			    new ArrayList<Entry<Integer, Integer>>(map.entrySet());
		Collections.sort(infoIds, new Comparator<Entry<Integer, Integer>>() {   
		    public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {      
		        //return (o2.getValue() - o1.getValue()); 
		        return (o1.getKey() - o2.getKey());
		    }
		});
		int size = 0;
		String output = "出现次数\t出现当前次数的个数\n";
		for(int i = 0 ; i < infoIds.size() ;  i ++)
		{
			output += infoIds.get(i).getKey() + "\t" + 
					infoIds.get(i).getValue() + "\n";
			size ++;
			if(size % 1000 == 0){
				uFunc.addFile(output, path);
				output = "";
			}
		}
		uFunc.addFile(output, path);
	}


	public static String Simplify(String plainTextString) {
		// TODO Auto-generated method stub
		if(plainTextString == null)
			return null;
		return SimConverter.convert(plainTextString);
	}


	public static boolean isPeriod(String contP) {
		// TODO Auto-generated method stub
		if(contP == null)
			return false;
		// 200x年
		return contP.matches("[0-9\\-–−_– —年月日\\.x\\?]{1,}");
	}

	private static HashMap<String, Integer> tmp = new HashMap<String, Integer>();
	public static void countStrings(String string)
	{
		int freq = 1;
		if(tmp.containsKey(string))
			freq += tmp.remove(string);
		else if(tmp.size() % 100000 == 0)
			System.out.println("ufunc:tmp size:" +tmp.size());
		tmp.put(string, freq);
	}
	public static void outputCountStrings(String path)
	{
		uFunc.SaveHashMap(tmp, path);
	}

	public static String AlertPath;
	public static int AlertNr = 0;
	public static String AlertOutput = "";
	public static void Alert(String className, String info) {
		// TODO Auto-generated method stub
		System.out.println(className + ":" + info);
		AlertNr ++;
		AlertOutput += className + ":" + info + "\n";
		if(AlertNr % 10 == 0)
		{
			uFunc.addFile(AlertOutput, AlertPath);
			AlertOutput = "";
		}
			
	}

}
