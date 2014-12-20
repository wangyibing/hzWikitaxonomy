package tools;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinSim {
	private static HanyuPinyinOutputFormat PY = 
			new HanyuPinyinOutputFormat();
	private static boolean inited = false;
	private static void init(){
		if(inited) return;
		
		 PY.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		 PY.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		 PY.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
		 inited = true;
	}
	public static String Get(String w1)
	{
		init();
		StringBuffer sb = new StringBuffer();
		for(char c : w1.toCharArray()){
			try {
				String [] output = PinyinHelper.toHanyuPinyinStringArray(c, PY);
				if(output != null)
				{
					sb.append(output[0]);
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	public static double GetSim(String w1, String w2)
	{
		init();
		String [][] py1 = GetPinyinWhole( w1);
		String [][] py2 = GetPinyinWhole( w2);
		double result = 0;
		for(int i = 0 ; i < py1.length; i ++)
			for(int j = 0 ; j < py2.length; j ++){
				if(py1[i] == null || py2[j] == null)
					continue;
				boolean sim = false;
				for(int ii = 0 ; ii < py1[i].length; ii ++)
				{
					if(sim) break;
					for(int jj = 0; jj < py2[j].length; jj ++)
						if(py1[i][ii].equals(py2[j][jj]))
						{
							sim = true;
							result ++;
							break;
						}
				}
			}
		result /= Math.max(w1.length(), w2.length());
		return result;
		
	}

	private static String[][] GetPinyinWhole(String w1) {
		// TODO Auto-generated method stub
		if(w1 == null || w1.length() < 1)
			return null;
		String [][] result = new String[w1.length()][];
		char [] cs = w1.toCharArray();
		for(int i = 0 ; i < cs.length; i ++)
		{
			try {
				result[i] = PinyinHelper.toHanyuPinyinStringArray(cs[i], PY);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result[i] = null;
			}
		}
		return result;
	}
}
