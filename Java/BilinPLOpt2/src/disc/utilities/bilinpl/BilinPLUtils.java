package disc.utilities.bilinpl;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

public class BilinPLUtils {
	/**
	 * Constructs Features for LinPL model representation within the BilinearPL model
	 * @param nLabels
	 * @return
	 */
	public static double [][] constructFeaturesForLinPL(int nLabels) {
		double [][] features = new double[nLabels][nLabels];
		for (int i=0;i<nLabels;i++){
			for (int j=0;j<nLabels;j++) {
				if (i==j) {
					features[i][j] = 1d;
				} else {
					features[i][j] = 0d;
				}
			}
		}
		return features;
	}
	
	/**
	 * Constructs Features for LinSidePL model.
	 * @param attributes
	 * @return
	 */
	public static double [][] constructFeaturesForLinSidePL(double [][] attributes) {
		int nLabels = attributes.length;
		int nAttr = attributes[0].length;
		double [][] features = new double[nLabels][nLabels+nAttr];
		for (int i=0;i<nLabels;i++){
			for (int j=0;j<nLabels;j++) {
				if (i==j) {
					features[i][j] = 1d;
				} else {
					features[i][j] = 0d;
				}
			}
			for (int j=0;j<nAttr;j++) {
				int j_off = nLabels+j;
				features[i][j_off] = attributes[i][j];				
			}
			
		}
		return features;
	}
	
	
	/**
	 * Sample from the PL model according to the skills.
	 * It returns an ordering (int array with lowest item id starting with 1 NOT 0).
	 * @param vs
	 * @return
	 */
	public static int [] getPLSampleFromVase(double [] vs) {
		int M = vs.length;
		int [] ordering = new int[M];
		double [] remainingSkills = new double[M];
		int [] ids = new int[M];
		// Init
		for (int i=0;i<M;i++) {
			ids[i] = i;
			remainingSkills[i] = vs[i];
		}
		
		for (int i=0;i<M;i++) {
			double [] probabilities = new double [M];
			double sumRemainingSkills = 0d;
			for (int j=0;j<M;j++) {
				sumRemainingSkills = sumRemainingSkills + remainingSkills[j];
			}
			for (int j=0;j<M;j++) {
				probabilities[j] = remainingSkills[j]/sumRemainingSkills;
			}
			EnumeratedIntegerDistribution sampler = new EnumeratedIntegerDistribution(ids,probabilities);
			int id = sampler.sample();
			ordering[i] = id+1;
			remainingSkills[id] = 0d;
		}
		return ordering;
	}
	
	

}
