package triple.standardize;

import tools.uFunc;

public class HTMLStdz {

	public static String standardize(String string)
	{
		String result = uFunc.Simplify(uFunc.full2HalfChange(string.replaceAll("\n", "_")));
		//System.out.println("before:\t" + result);
		result = result.replaceAll("&#160;", " ");
		//System.out.println("after:\t" + result);
		result = result.replaceAll("\\[[0-9]{1,2}\\]", "")
				.replaceAll("\\((,|年|月|日|[0-9\\-–−_– —\\.x])+\\)", "");
		if(result.matches("(?m)^.+\\(英语:.+\\)"))
		{
			//System.out.println("HTMLStdz.java:" + result);
			result = result.substring(0, result.indexOf("(英语:"));
		}
		return result;
	}
}
