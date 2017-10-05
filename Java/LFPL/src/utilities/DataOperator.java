package utilities;

import java.util.ArrayList;

public class DataOperator {
	/**
	 * Update (2015-01) - My initial guess has been:	
	 * " 
	 * I assume in Weiweis original Impl, he deleted labels according to the missingrate by relabel them as -1. Furthermore
	 * I guess the term "toTopK rankings" should state, that the residual ranks are renumbered.
	 * "
	 * 
	 * Now I think, according to the paper "Labelwise versus Pairwise Decomposition in Label Ranking" (2013) p.6 - this is just another missing label scenario, where
	 * "labelReduce" is the classic variant and labelDeleteToTopKRankings. Simply put: only the J-top rankings are kept, where J has a binomial distribution with parameters
	 * K and 1-p.
	 * 
	 * @param features
	 * @param labels
	 * @param missingrate
	 */
	public static void labelDeleteToTopKRankings(ArrayList<double[]> features, ArrayList<double[]> labels, double missingrate) {
		_labelReduceTopRank(labels, missingrate);
		deleteUselessTuples(features, labels);
		//_renameMissingLabelsToNans(labels);
	}
	
	/**
	 * Similar to labelDeleteToTopKRankings but applicable to orderings.
	 * @param features
	 * @param labels
	 * @param missingrate
	 */
	public static void labelDeleteToTopKOrderings(ArrayList<double[]> features, ArrayList<double[]> labels, double missingrate) {
		_orderingReduceTopK(labels,missingrate);
		_deleteCellsFromOrderings(labels);
	}
	/**
	 * Update(2015-01) OnlineCCLR main suggests that this method should be applied on orderings instead of rankings (if iris_dense_cv is in ranking format). 
	 * 
	 * This method removes labels randomly from rankings. The amount of deletion is specified by missingrate.
	 * In addition to the primitive _labelReduce function, observations (instances+associated rankings) are deleted which contain less than 2 alternatives.
	 * This method is known as "missing-at-random" in LR literature.
	 * 
	 * @param features
	 * @param labels
	 * @param missingrate
	 */
	public static void labelDelete(ArrayList<double[]> features, ArrayList<double[]> labels, double missingrate) {
		labelReduce(labels, missingrate);
		//deleteUselessTuples(features, labels);
		fixIncompleteOrderings(labels);
	}
	
	/**
	 * - Deletes instances whose rankings have less than 2 alternatives
	 * - Renames missing labels from -1  to Nan
	 * Revision 2015/1 
	 * @param features
	 * @param labels
	 */
	public static void deleteUselessTuples(ArrayList<double[]> features, ArrayList<double[]> labels) {
		//Delete the useless tuples
		
        for(int i=0; i<labels.size(); i++){
            int nancount = 0; 
            for(int j=0; j<labels.get(i).length; j++){
                if(!(labels.get(i)[j]>-1)){
                    nancount++;
                    labels.get(i)[j] = Double.NaN; // this is important
                }
            }
            double [] repl = new double[labels.get(i).length];
 //           labels.set(i, element)
            if(labels.get(i).length-nancount<2){
                features.remove(i);
                labels.remove(i);
                i--;
            }
        }
	}
	
	/**
	 * This method deletes labels randomly from the rankings specified by labels. 
	 * @param labels
	 * @param missingrate
	 */
	public static void labelReduce(ArrayList<double[]> labels, double missingrate) {
		for (int i=0;i<labels.size();i++) {
			int ct_ranking_length = labels.get(i).length;
			int rem_cnt = 0;
			for (int j=0;j<ct_ranking_length;j++) {
				if (Math.random()<missingrate) {
					if (rem_cnt<ct_ranking_length-2){
						labels.get(i)[j] = -1d;
						rem_cnt++;
					}
				}
			}
		}
	}
	
	/**
	 * This method can be applied on orderings which contain "-1"s due to a deletion process.
	 * The alternatives labeled as "-1" will be padded to the right of an ordering respectively.
	 * @param labels
	 */
	public static void fixIncompleteOrderings(ArrayList<double[]> labels) {
		for (int i=0;i<labels.size();i++) {
			_fixIncompleteOrdering(labels.get(i));
		}
	}
	
	/**
	 * Helper for fixIncompleteOrderings.
	 * 
	 * @param ordering
	 */
	public static void _fixIncompleteOrdering(double [] ordering) {
		double [] temp = new double[ordering.length];

		int cnt = 0;
		for (int i=0;i<ordering.length;i++) {
			temp[i] = -1d;
			if (ordering[i]!=-1d) {
				cnt = cnt + 1;
				temp[cnt-1] = ordering[i];
			}
		}	
		// overwrite original 
		for (int i=0;i<ordering.length;i++) {
			ordering[i] = temp[i];
		}
	}
	
	
	/*public static ArrayList<double[]> labelDelete(ArrayList<double[]> labels, double missingrate){
		ArrayList<double[]> incompleteLabels = new ArrayList<double[]>();
		
		return incompleteLabels;
	}*/
	/**
	 * This is the low-level implemention of the "top-rank" missing label scenario. 
	 * It is used by labelDeleteToTopKRankings.
	 * @param labels
	 * @param missingrate
	 */
	private static void _labelReduceTopRank(ArrayList<double[]> labels, double missingrate) {
		int K = labels.get(0).length;
		for (int i=0;i<labels.size();i++) {
			int keepTopJ = getBinomial(K, 1d-missingrate);
			for (int j=0;j<labels.get(i).length;j++) {
				if (labels.get(i)[j]>keepTopJ) { 
					labels.get(i)[j] = -1d;
				}
			}
		}
	}
	
	/**
	 * Similarly to _labelReduceTopRank this function removes only alternatives from the last ranking places.
	 * @param labels
	 * @param missingrate
	 */
	private static void _orderingReduceTopK(ArrayList<double[]> labels, double missingrate) {
		int K = labels.get(0).length;
		for (int i=0;i<labels.size();i++) {
			int keepTopJ = getBinomial(K, 1d-missingrate);
			for (int j=0;j<labels.get(i).length;j++) {
				if (j>=keepTopJ) { 
					labels.get(i)[j] = -1d;
				}
			}
		}
	}
	
	
	
	/**
	 * This function adapts the array length (of the orderings) within the ArrayList. It removes alternatives from orderings, which are labeled with "-1".
	 * @param labels
	 */
	public static void _deleteCellsFromOrderings(ArrayList<double []> labels) {
		for (int i=0;i<labels.size();i++) {
	 	 	  labels.set(i, _getTruncatedArray(labels.get(i)));
		}
	}
	
	/**
	 * This is a helper function for _deleteCellsFromOrderings.
	 * @param arr
	 * @return
	 */
	private static double[] _getTruncatedArray(double [] arr) {
		int cntPos = 0;
		for (int i=0;i<arr.length;i++) {
			if (arr[i] != -1d) {
				cntPos++;
			}
		}
		double [] truncatedArray = new double[cntPos];
		int ctPosition = 0;
		for (int i=0;i<arr.length;i++) {
			if (arr[i] != -1d) {
				truncatedArray[ctPosition] = arr[i];
				ctPosition++;
			}
		}
		return truncatedArray;
	}
	/**
	 * In case the label rankings have been modified, e.g. with method labelReduce,
	 * this method renames the ranks s.t. labels obtain ranks from 1 to num(labels having rank>0)
	 * @param labels
	 */
	public static void renameLabels(ArrayList<double[]> labels) {
		for (int i=0;i<labels.size();i++) {
			fixRanking(labels.get(i));
		}
	}
	
	public static double [] fixRanking(double [] a) {
		//double [] b = new double[a.length];
		int len_missing = 0;
		for (int i=0; i< a.length; i++) {
			//b[i] = a[i];
			if (a[i] == 0) {
				len_missing++;
			}
		}
		
		for (int rank=1;rank<=a.length-len_missing;rank++) {
			//System.out.println("Try assigning rank "+rank);
			int rs = rank; // begin search
			boolean found = false;
			while (found==false) {
				//System.out.println("Searching array for rs="+rs);
				for (int j=0;j<a.length;j++) {
					if (rs == a[j]) {
						
						a[j] = rank;
					//	b[j] = rank;
						found = true;
						break;
					}
				}
				rs++; // try next round
			}
		}
		return a;
	}
	
	public static void _renameMissingLabelsToNans(ArrayList<double []> labels) {
		for (int i=0;i<labels.size();i++) {
			for (int j=0;j<labels.get(i).length;j++) {
				if (labels.get(i)[j] == -1d) { 
					labels.get(i)[j] = Double.NaN;
				}
			}
		}
	}
	
	/**
	 *  Generates a random number from the binomial distribution with parameters specified by n and p.
	 * 	// Taken from http://stackoverflow.com/questions/1241555/algorithm-to-generate-poisson-and-binomial-random-numbers
	 * This is used in labelDeleteToTopKRankings.
	 * @param n
	 * @param p
	 * @return
	 */
	public static int getBinomial(int n, double p) {
		  int x = 0;
		  for(int i = 0; i < n; i++) {
		    if(Math.random() < p)
		      x++;
		  }
		  return x;
	}

}
