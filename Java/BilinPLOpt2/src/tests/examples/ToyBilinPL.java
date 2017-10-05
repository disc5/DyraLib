package tests.examples;

import java.util.Map;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

import disc.bilinpl.BilinPLTrainer;
import disc.utilities.Evaluation;
import disc.utilities.MyUtils;
import disc.utilities.bilinpl.BilinPLUtils;
import disc.utilities.bilinpl.KronUtils;
import disc.utilities.bilinpl.Permutation;

/**
 * This tests 3 different formulations of the BilinPL model
 * - BilinPL (with pure label attributes/descriptions)
 * - LinPL (without any label attributes/descriptions)
 * - LinSidePL (with labels and label attributes)
 * @author ds
 *
 */
public class ToyBilinPL {
	
	public static void trainAndTestBilinPL(double [][] labelFeatures, double [][] trInstances, double [][] teInstances, int [][] trOrderings, double [][] d_teRankings) {
		double [][][] jf = KronUtils.getOrderedKronJointFeatureTensor(trInstances, labelFeatures, trOrderings);
		// --
		BilinPLTrainer trainer = new BilinPLTrainer();
		trainer.init(jf);
		long millis_start = System.currentTimeMillis();// % 1000;
		double[] theta_prime = trainer.train();
		long millis_end = System.currentTimeMillis();// % 1000;
		System.out.println("Took "+(millis_end-millis_start)+" ms");
		
		System.out.println("Solution:");
		MyUtils.printDoubleArray(theta_prime);
		
		// Predictions
		double [][][] jf_test = KronUtils.getKronJointFeatureTensor(teInstances, labelFeatures);
		double [][] pred_orderings =  trainer.predictSet(jf_test, theta_prime);
		
		// Evaluate
		double ktau = 0d;
		for (int i=0;i<d_teRankings.length;i++) {
			double [] pred_ranking = Permutation.switchOrderingRanking(pred_orderings[i]);
			ktau = ktau + Evaluation.kendall(d_teRankings[i], pred_ranking);
		}
		ktau = ktau/(double)d_teRankings.length;
		System.out.println("Ktau Test : "+ktau);
	}
	
	public static void trainAndTestLinPL(double [][] labelFeatures, double [][] trInstances, double [][] teInstances, int [][] trOrderings, double [][] d_teRankings) {
		double [][] linplLabelFeatures = BilinPLUtils.constructFeaturesForLinPL(labelFeatures.length);
		
		double [][][] jf = KronUtils.getOrderedKronJointFeatureTensor(trInstances, linplLabelFeatures, trOrderings);
		// --
		BilinPLTrainer trainer = new BilinPLTrainer();
		trainer.init(jf);
		long millis_start = System.currentTimeMillis();// % 1000;
		double[] theta_prime = trainer.train();
		long millis_end = System.currentTimeMillis();// % 1000;
		System.out.println("Took "+(millis_end-millis_start)+" ms");
		
		System.out.println("Solution:");
		MyUtils.printDoubleArray(theta_prime);
		
		// Predictions
		double [][][] jf_test = KronUtils.getKronJointFeatureTensor(teInstances, linplLabelFeatures);
		double [][] pred_orderings =  trainer.predictSet(jf_test, theta_prime);
		
		// Evaluate
		double ktau = 0d;
		for (int i=0;i<d_teRankings.length;i++) {
			double [] pred_ranking = Permutation.switchOrderingRanking(pred_orderings[i]);
			ktau = ktau + Evaluation.kendall(d_teRankings[i], pred_ranking);
		}
		ktau = ktau/(double)d_teRankings.length;
		System.out.println("Ktau Test : "+ktau);
	}
	
	public static void trainAndTestLinSidePL(double [][] labelFeatures, double [][] trInstances, double [][] teInstances, int [][] trOrderings, double [][] d_teRankings) {
		double [][] linsideplLabelFeatures = BilinPLUtils.constructFeaturesForLinSidePL(labelFeatures);
		
		double [][][] jf = KronUtils.getOrderedKronJointFeatureTensor(trInstances, linsideplLabelFeatures, trOrderings);
		// --
		BilinPLTrainer trainer = new BilinPLTrainer();
		trainer.init(jf);
		long millis_start = System.currentTimeMillis();// % 1000;
		double[] theta_prime = trainer.train();
		long millis_end = System.currentTimeMillis();// % 1000;
		System.out.println("Took "+(millis_end-millis_start)+" ms");
		
		System.out.println("Solution:");
		MyUtils.printDoubleArray(theta_prime);
		
		// Predictions
		double [][][] jf_test = KronUtils.getKronJointFeatureTensor(teInstances, linsideplLabelFeatures);
		double [][] pred_orderings =  trainer.predictSet(jf_test, theta_prime);
		
		// Evaluate
		double ktau = 0d;
		for (int i=0;i<d_teRankings.length;i++) {
			double [] pred_ranking = Permutation.switchOrderingRanking(pred_orderings[i]);
			ktau = ktau + Evaluation.kendall(d_teRankings[i], pred_ranking);
		}
		ktau = ktau/(double)d_teRankings.length;
		System.out.println("Ktau Test : "+ktau);
	}

	public static void main(String[] args) throws Exception {
		// Example
				String filename = "data/bilinpl_toy.mat";
				MatFileReader mreader = new MatFileReader(filename);
				//System.out.println(mreader.toString());
				Map<String,MLArray> map = mreader.getContent();
				
				
				MLDouble mld;
				mld = (MLDouble) map.get("labelFeatures");
				double [][] labelFeatures = mld.getArray();
				MyUtils.printDoubleArray(labelFeatures[0]);
				
				mld = (MLDouble) map.get("instTrFeatures");
				double [][] trInstances = mld.getArray();
				MyUtils.printDoubleArray(trInstances[0]);
				
				mld = (MLDouble) map.get("instTeFeatures");
				double [][] teInstances = mld.getArray();
				MyUtils.printDoubleArray(teInstances[0]);
				
				mld = (MLDouble) map.get("trOrderings");
				double [][] d_trOrderings = mld.getArray();
				System.out.println("ordering");
				MyUtils.printDoubleArray(d_trOrderings[0]);
				int [][] trOrderings = MyUtils.convertDouble2dArrayToIntArray(d_trOrderings);
				MyUtils.printIntArray(trOrderings[0]);
				
				mld = (MLDouble) map.get("teRankings");
				double [][] d_teRankings = mld.getArray();
				MyUtils.printDoubleArray(d_teRankings[0]);
				//System.exit(0);
				int [][] teRankings = MyUtils.convertDouble2dArrayToIntArray(d_teRankings);
				MyUtils.printIntArray(teRankings[0]);
				
				//System.exit(0);
				// --
				// BILIN PL
				trainAndTestBilinPL(labelFeatures, trInstances,teInstances, trOrderings, d_teRankings);
				// --
				// LIN PL
				trainAndTestLinPL(labelFeatures, trInstances,teInstances, trOrderings, d_teRankings);
				// --
				// LINSIDE PL
				trainAndTestLinSidePL(labelFeatures, trInstances,teInstances, trOrderings, d_teRankings);
				
	}

}
