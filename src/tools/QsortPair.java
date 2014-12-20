package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

class SIpair{
	public String s;
	public long i;
}
class DescComp implements Comparator<SIpair>{
	public final int compare(SIpair p1, SIpair p2){
		if(p1.i > p2.i)
			return -1;
		else if(p1.i < p2.i)
			return 1;
		return 0;
	}
}
class AscComp implements Comparator<SIpair>{
	public final int compare(SIpair p1, SIpair p2){
		if(p1.i < p2.i)
			return -1;
		else if(p1.i > p2.i)
			return 1;
		return 0;
	}
}

public class QsortPair {

	public static void SortPair(String srcPath,
			String targetPath, boolean SecColComp,
			boolean Low2High, int Capacity) {
		String path = srcPath;
		uFunc.deleteFile(targetPath);
		Capacity = uFunc.GetLineNr(path) + 5;
		System.out.println("QsortPair: pair Nr:" + (Capacity-5));
		SIpair [] pairs = new SIpair[Capacity];
		for(int i = 0; i < Capacity;i++){
			pairs[i] = new SIpair();
		}
		BufferedReader br = 
				uFunc.getBufferedReader(path);
		String s = "";
		int index = 0;
		try {
			System.out.println("QsortPair: begin pair loading...");
			while(( s = br.readLine())!= null){
				String [] ss = s.split("\t");
				try{
					if(ss.length < 2){
						System.out.println("split error:" + s);
						continue;
					}
					else{
						if(SecColComp == true)
						{
							pairs[index].s = ss[0];
							pairs[index].i = Long.parseLong(ss[1]);
						}
						else{
							pairs[index].s = ss[1];
							pairs[index].i = Long.parseLong(ss[0]);
						}
						index++;
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			System.out.println("QsortPair:pair loaded, size:" + index);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("\""+s+"\"");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("1\""+s+"\"");
			e.printStackTrace();
		}
		if(Low2High == true)
			Arrays.sort(pairs, 0, index, new AscComp());
		else
			Arrays.sort(pairs, 0, index, new DescComp());
		System.out.println("QsortPair: qsort finished!");
		StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i < index; i++){
			if(SecColComp == true)
				sb.append(pairs[i].s + "\t" + pairs[i].i + "\n");
			else
				sb.append(pairs[i].i + "\t" + pairs[i].s + "\n");
			if(i % 1000 == 0){
				uFunc.addFile(sb.toString(), targetPath);
				sb.setLength(0);
			}
		}
		uFunc.addFile(sb.toString(), targetPath);
		System.out.println("QsortPair: finished!");
	}
}
