import java.util.Vector;

import com.tag.myPredicate;

class Mytest{
	int i = 0 ;
	int j = 1;
	Mytest(int a, int b){
		i = a;
		j = b;
	}
}
public class test {

	public static void main(String [] args)
	{
		System.out.println("''姚''明''".replaceAll("'", "\\\\'"));
		Vector<Mytest> PagePred =  
				new Vector<Mytest>();
		Mytest m = new Mytest(2,4);
		Mytest m1 = new Mytest(5,6);
		PagePred.add(m);
		PagePred.add(m1);
		PagePred.get(1).i = 3;
		System.out.println(PagePred.get(1).i);
	}
}
