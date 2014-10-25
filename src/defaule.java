

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import normalization.segment;

import org.ansj.splitWord.analysis.NlpAnalysis;

import database.InfoboxNameList;
import extract.Extract;
import tools.uFunc;

public class defaule {

	public static void main(String [] args){
		String re = uFunc.OutputProjectInfo();
		System.out.println("sdfd ::：".replaceAll("(:$)|(：$)", ""));
		//System.out.println(segment.ANSJsegmentSeg("人民网北京10月20日电据央视``焦点访谈''报道，长达的非法``占中''所带来的损失正在逐步扩大，越来越多的反占中人士和普通市民开始站出来谴责非法集会者。有香港市民含泪谴责外部势力在幕后支持``占中''，``我有个朋友，老板要他必须去占中，因为（老板）收了钱，他们去`占中'钱（港币）。''香港素有东方明珠之称，我们心中的香港环境优美、经济发达、是世界著名的购物天堂。可是最近，就在香港最繁华的地方，交通被堵塞、商家没生意，就连普通市民的正常生活都受到严重干扰。弥敦道是香港著名的购物大道，商铺大部分都是卖珠宝首饰和保健品的，9月28开始，这里的道路占，商家无法经营。大部分的商家关上了大门，记者注意到只有一家店把卷帘门落下了一半，还在营业。落下一半的卷帘门正好容人通过，门外人流不断，但是却没有人进来买东西，甚至连往里张望一下的人都没有。胖哥是这个店的财务，他给记者算了一笔账：这个店原来每天的营业额有3万多元，现在平均每天只有1万元，而店铺的租金一个月要，加上给员工发工资，月的经营成本至少，这个月恐怕要亏本了。自从香港发生了占中事件后，很多商铺的生意就开始受到影响，但他们怎么也没想到这个事件会一直延续到现在。10月17日上午，胖哥他们来店铺上班时，门前的路口能通车了，他们本以为可以像往常一样做生意了，但是中午又涌来了很多人，希望一下子又没有了。下午多，胖哥打出了今天的流水，只有13000多元，不到平时的一半，其中有一个大单还是朋友帮忙过来买的。下午五点了，还是一直没有什么客人，胖哥他们只好早早关门收工。已经持续的占中，让香港零售业遭受巨大损失。 "));
		System.out.println(("23").substring(0, 0).equals(""));
		StringBuffer sb = new StringBuffer();
		sb.append('c');
		sb.append("wre");
		sb.append('.');
		sb.append(' ');
		System.out.println(("1\n23\nN2\n\n3\nsdf").replaceAll("\n([a-z])", "$1"));
		
		
		String folder = "/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Web/";
		BufferedReader br = uFunc.getBufferedReader(folder + "Triple2");
		String oneLine = "";
		HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		HashMap<String, Integer> map2 = new HashMap<String, Integer>();
		try {
			while((oneLine = br.readLine()) != null)
			{
				String pre = oneLine.split("\t")[1];
				map2.put(pre, 0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		br = uFunc.getBufferedReader(folder + "Triple");
		try {
			while((oneLine = br.readLine()) != null)
			{
				String pre = oneLine.split("\t")[1];
				if(map1.containsKey(pre) == false)
				{
					map1.put(pre, 0);
					if(map2.containsKey(pre) == false)
						System.out.println(oneLine);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
