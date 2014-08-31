package normalization;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.ansj.vec.Word2VEC;
import com.ansj.vec.domain.WordEntry;

import tools.uFunc;


public class word2vec {
	static Word2VEC w1;
	static boolean ModelInited = false;
	static String ModelPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/word2vec/ANSJ_vectors.bin";
	static String DictPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/word2vec/Dictionary";
	
	public static void main(String [] args)
	{
		LoadWord2VecDic();
	}
	
	private static void LoadWord2VecDic() {
		// TODO Auto-generated method stub
		if(Init() == false)
			return;
		HashMap<String, float[]> dict = 
				w1.getWordMap();
		
		System.out.println("dic size:" + dict.size());
		uFunc.deleteFile(DictPath);
		Iterator<Entry<String, float[]>> it = 
				dict.entrySet().iterator();
		String output = "";
		int Nr = 0;
		while(it.hasNext())
		{
			output += it.next().getKey() + "\n";
			Nr ++;
			if(Nr % 1000 == 0)
			{
				uFunc.addFile(output, DictPath);
				output = "";
				if(Nr % 50000 == 0)
					System.out.println(Nr + " words saved!");
			}
		}
		uFunc.addFile(output, DictPath);
	}
	
	public static Set<WordEntry> GetSimWords(String content)
	{
		if(Init() == false)
			return null;
		Set<WordEntry> result = new HashSet<WordEntry>();
		
		
		return result;
	}
	
	private static boolean Init() {
		// TODO Auto-generated method stub
		if(ModelInited == false)
		{
			w1 = new Word2VEC();
			try {
				w1.loadGoogleModel(ModelPath);
			} catch (IOException e) {
				System.out.println("word2vec model init failed!");
				e.printStackTrace();
				return false;
			}
			ModelInited = true;
			System.out.println("word2vec model init!");
		}
		return true;
	}

}
