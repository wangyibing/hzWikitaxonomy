package word2vec;

import java.util.List;
import java.util.Set;

import com.ansj.vec.Word2VEC;
import com.ansj.vec.domain.WordEntry;

public class word2vec {
	public static final String modePath = 
			"/home/hanzhe/Public/result_hz/zhwiki/data2/QA_vectors.bin";

	private static Word2VEC model = new Word2VEC(modePath);
	

	public static Set<WordEntry> distance(String word)
	{
		return model.distance(word);
	}

	public static Set<WordEntry> distance(List<String> words)
	{
		return model.distance(words);
	}
	
	public static float[] getVector(String word)
	{
		return model.getWordVector(word);
	}
	
}
