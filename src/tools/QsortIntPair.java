package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

class IntPair{
	public long fir;
	public long sec;
	public double dNote;
}
class IntDAComp implements Comparator<IntPair>{
	public final int compare(IntPair p1, IntPair p2){
		if(p1.fir > p2.fir)
			return -1;
		else if(p1.fir < p2.fir)
			return 1;
		else{
			if(p1.sec > p2.sec)
				return 1;
			else if(p1.sec < p2.sec)
				return -1;
			return 0;
		}
	}
}
class IntAAComp implements Comparator<IntPair>{
	public final int compare(IntPair p1, IntPair p2){
		if(p1.fir < p2.fir)
			return -1;
		else if(p1.fir > p2.fir)
			return 1;
		else{
			if(p1.sec > p2.sec)
				return 1;
			else if(p1.sec < p2.sec)
				return -1;
			return 0;
		}
	}
}
class IntDDComp implements Comparator<IntPair>{
	public final int compare(IntPair p1, IntPair p2){
		if(p1.fir > p2.fir)
			return -1;
		else if(p1.fir < p2.fir)
			return 1;
		else{
			if(p1.sec < p2.sec)
				return 1;
			else if(p1.sec > p2.sec)
				return -1;
			return 0;
		}
	}
}
class IntADComp implements Comparator<IntPair>{
	public final int compare(IntPair p1, IntPair p2){
		if(p1.fir < p2.fir)
			return -1;
		else if(p1.fir > p2.fir)
			return 1;
		else{
			if(p1.sec < p2.sec)
				return 1;
			else if(p1.sec > p2.sec)
				return -1;
			return 0;
		}
	}
}
public class QsortIntPair {

	public static void SortPair(String srcPath,
			String targetPath, boolean SecColComp,
			boolean Low2High1, boolean Low2High2) {
		String path = srcPath;
		uFunc.deleteFile(targetPath);
		int Capacity = uFunc.GetLineNr(path) + 5;
		System.out.println("QsortPair: pair Nr:" + (Capacity-5));
		IntPair [] pairs = new IntPair[Capacity];
		for(int i = 0; i < Capacity;i++){
			pairs[i] = new IntPair();
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
						if(SecColComp == false)
						{
							pairs[index].fir = Long.parseLong(ss[0]);
							pairs[index].sec = Long.parseLong(ss[1]);
						}
						else{
							pairs[index].fir = Long.parseLong(ss[1]);
							pairs[index].sec = Long.parseLong(ss[0]);
						}
						index++;
						if(ss.length > 2)
							pairs[index].dNote = Double.parseDouble(ss[2]);
						else pairs[index].dNote = 0;
					}
					
				}catch(Exception e){
					e.printStackTrace();
					return;
				}
			}
			System.out.println("QsortPair:pair loaded, size:" + index);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("\""+s+"\"");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("1\""+s+"\"");
			e.printStackTrace();
			return;
		}
		if(SecColComp)
		{
			boolean btmp = Low2High1;
			Low2High1 = Low2High2;
			Low2High2 = btmp;
		}
		int mode = Low2High1 ? 2:0;
		mode += Low2High2 ? 1:0;
		switch(mode){
		case 0:
			Arrays.sort(pairs, 0, index, new IntDDComp());
		case 1:
			Arrays.sort(pairs, 0, index, new IntDAComp());
		case 2:
			Arrays.sort(pairs, 0, index, new IntADComp());
		case 3:
			Arrays.sort(pairs, 0, index, new IntAAComp());
		}
		System.out.println("QsortPair: qsort finished!");
		StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i < index; i++){
			if(SecColComp == false)
				sb.append(pairs[i].fir + "\t" + pairs[i].sec + "\t" +
						pairs[i].dNote + "\t" + "\n");
			else
				sb.append(pairs[i].sec + "\t" + pairs[i].fir + "\t" +
						pairs[i].dNote + "\t" + "\n");
			if(i % 1000 == 0){
				uFunc.addFile(sb.toString(), targetPath);
				sb.setLength(0);
			}
		}
		uFunc.addFile(sb.toString(), targetPath);
		System.out.println("QsortPair: finished!");
	}
}
