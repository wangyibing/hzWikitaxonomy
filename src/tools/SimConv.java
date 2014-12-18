package tools;

import com.spreada.utils.chinese.ZHConverter;

public class SimConv {

	public static ZHConverter SimConverter = 
			ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
	public static String Convert2Simp(String string)
	{
		return SimConverter.convert(string);
	}
}
