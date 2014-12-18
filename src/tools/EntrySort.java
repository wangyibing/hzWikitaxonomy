package tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

public class EntrySort {

	public void SortDes(List<Entry<String, Double>> list){
		Collections.sort(list, new Comparator<Entry<String, Double>>(){
			public int compare(Entry<String, Double> p1, Entry<String, Double> p2){
				if(p2.getValue() > p1.getValue())
					return 1;
				if(p2.getValue() < p1.getValue())
					return -1;
				return 0;
			}
		});
	}
	public void SortIntDes(List<Entry<Integer, Double>> list){
		Collections.sort(list, new Comparator<Entry<Integer, Double>>(){
			public int compare(Entry<Integer, Double> p1, Entry<Integer, Double> p2){
				if(p2.getValue() > p1.getValue())
					return 1;
				if(p2.getValue() < p1.getValue())
					return -1;
				return 0;
			}
		});
	}
	public void SortStrIntDes(List<Entry<String, Integer>> list){
		Collections.sort(list, new Comparator<Entry<String, Integer>>(){
			public int compare(Entry<String, Integer> p1, Entry<String, Integer> p2){
				if(p2.getValue() > p1.getValue())
					return 1;
				if(p2.getValue() < p1.getValue())
					return -1;
				return 0;
			}
		});
	}	
	
	public void SortArrayStrIntDes(ArrayList<Entry<String, Double>> list){
		Collections.sort(list, new Comparator<Entry<String, Double>>(){
			public int compare(Entry<String, Double> p1, Entry<String, Double> p2){
				if(p2.getValue() > p1.getValue())
					return 1;
				if(p2.getValue() < p1.getValue())
					return -1;
				return 0;
			}
		});
	}
}
