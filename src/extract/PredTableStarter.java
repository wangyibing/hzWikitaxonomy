package extract;

import tools.Mysql;
import extract.predicate.PredAvgExtraction;
import extract.predicate.PredNorm;
import extract.predicate.WikitextPredicate_old;
import extract.predicate.predicateInfo.PredicateId;
import extract.predicate.predicateInfo.Update_PredicateInfo;


public class PredTableStarter {

	public static void main(String [] args)
	{
		String tableName = "PredicateInfo";
		
		String folder = "/home/hanzhe/Public/result_hz/zhwiki/predicate/";
		String predicateId = folder + "predicateId";
		String predicateSorted = folder + "predicate.sorted";
		String predicate2 = folder + "predicateId2";
		String predicateNormed = folder + "predicateId.Normed";
		String dumpsTriple = folder + "dumpsTriples";
		String predicateAvg = folder + "PredicateAvg";

		Mysql m = new Mysql("hzWikiCount2", null);
		// create table
		m.execute("data/sql/PredicateInfo.sql");
		
		// 0. generate triple info to mysql.hzTriple
		//Extract.main(null);
		
		// 1. generate "predicateId" file along with "web's triple" file
		//PredicateId.Generate(m, tableName, predicateId);
		// 2. sort predicateId table to generate "predicateId.sorted"
		//PredicateId.Sort_old(predicateId, predicateSorted);
		//Update_PredicateInfo.Update();
		// 3. fill the predicate info with dumpsinfo, generate "predicate2"
		// file
		//WikitextPredicate_old.Extract(predicateSorted, dumpsTriple, predicate2);
		// 4. replace some predicates' content with their UpperTitles
		//PredNorm.Normalize(predicate2, predicateNormed);
		// 5. Generate myPredicateAvg from myPredicates
		//PredAvgExtraction.Extract(predicateNormed, predicateAvg);
		// 6.
		//GenerateTestCase.start();
	}
}
