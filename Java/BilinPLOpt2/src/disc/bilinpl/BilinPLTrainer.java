package disc.bilinpl;

import disc.utilities.Evaluation;
import disc.utilities.MyUtils;
import disc.utilities.bilinpl.BilinPLUtils;
import edu.stanford.nlp.optimization.QNMinimizer;

public class BilinPLTrainer {

	BilinPLObjectiveFunction objFunc;
	double [] theta;
	
	public BilinPLTrainer() {
		super();
		objFunc = new BilinPLObjectiveFunction();
	}
	
	/**
	 * This function set the training set in form of ordered joint feature vectors
	 * Dim1 : number of examples
	 * Dim2 : JF-vector belonging to item 1<=m<=M
	 * Dim3 : JF-vector element
	 * @param jf
	 */
	public void init(double [][][] jf) {
		objFunc.setJointFeatures(jf);
	}
	
	/**
	 * Trains a BilinPL model using L-BFGS
	 * @return
	 */
	public double[] train() {
		QNMinimizer optimizer = new QNMinimizer();
		double [] initialPoint = new double[objFunc.domainDimension()];
		theta = optimizer.minimize(objFunc, 0.0001, initialPoint);
		return theta;
	}
	
	/**
	 * Returns the model weights.
	 * @return
	 */
	public double [] getWeights() {
		return this.theta;
	}
	
	/**
	 * Given a set of (new) joint feature vectors, this method provides a prediction in form of orderings.
	 * @param unorderedJointFeatures
	 * @param theta
	 * @return
	 */
	public double [][] predictSet(double [][][] unorderedJointFeatures, double[] theta) {
		int N = unorderedJointFeatures.length;
		int M = unorderedJointFeatures[0].length;
		double [][] pred_orderings=new double[N][M];
		for (int i=0;i<N;i++) {
			double [] ct_scores = new double[M];
			for (int j=0;j<M;j++) {
				ct_scores[j] = this.calcModelScore(unorderedJointFeatures[i][j], theta);
			}
			pred_orderings[i] = MyUtils.getOrdering(ct_scores);
		}
		return pred_orderings;
	}
	
	/**
	 * This function calculates the model score (alias "skill")
	 * @param jointFeatureVector
	 * @param theta
	 * @return
	 */
	public double calcModelScore(double [] jointFeatureVector, double[] theta) {
		return Math.exp(Evaluation.dotProduct(jointFeatureVector, theta));
	}
	
	
	public int [] singleSampleFromBilinPLDistribution(double [][] unorderedJointFeatureVector) {
		int M = unorderedJointFeatureVector.length;
		double [] skills = new double[M];
		for (int i=0;i<M;i++) {
			skills[i] = calcModelScore(unorderedJointFeatureVector[i], this.theta);
		}
		return BilinPLUtils.getPLSampleFromVase(skills);
	}
	
	
	public static void main(String[] args) throws Exception{
		// Example
		/*String filename = "data/bilinpl_toy.mat";
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
		//MyUtils.printDoubleArray(d_trOrderings[0]);
		int [][] trOrderings = MyUtils.convertDouble2dArrayToIntArray(d_trOrderings);
		MyUtils.printIntArray(trOrderings[0]);
		
		mld = (MLDouble) map.get("teRankings");
		double [][] d_teRankings = mld.getArray();
		MyUtils.printDoubleArray(d_teRankings[0]);
		//System.exit(0);
		int [][] teRankings = MyUtils.convertDouble2dArrayToIntArray(d_teRankings);
		MyUtils.printIntArray(teRankings[0]);
		
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
		System.out.println("Ktau Test : "+ktau);*/

	}

}
