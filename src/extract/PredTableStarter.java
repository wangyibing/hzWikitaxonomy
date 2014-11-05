package extract;

import extract.predicatetable.PredAvgExtraction;
import extract.predicatetable.PredNorm;
import extract.predicatetable.PredicateIdSort;
import extract.predicatetable.WikitextPredicate;


public class PredTableStarter {

	public static void main(String [] args)
	{
		String folder = "/home/hanzhe/Public/result_hz/wiki_count2/predicate/";
		String predicate = folder + "predicateId";
		String predicateSorted = folder + "predicate.sorted";
		String predicate2 = folder + "predicateId2";
		String predicateNormed = folder + "predicateId.Normed";
		String dumpsTriple = folder + "dumpsTriples";
		String predicateAvg = folder + "PredicateAvg";
		
		// 1. generate "predicateId" file along with "web's triple" file
		Extract.main(null);
		// 2. sort predicateId table to generate "predicateId.sorted"
		PredicateIdSort.Sort(predicate, predicateSorted);
		// 3. fill the predicate info with dumpsinfo, generate "predicate2"
		// file
		WikitextPredicate.Extract(predicateSorted, dumpsTriple, predicate2);
		// 4. replace some predicates' content with their UpperTitles
		PredNorm.Normalize(predicate2, predicateNormed);
		// 5. Generate myPredicateAvg from myPredicates
		PredAvgExtraction.Extract(predicateNormed, predicateAvg);
		// 6.
		//GenerateTestCase.start();
	}
}
