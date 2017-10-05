package disc.bilinpl;

import edu.stanford.nlp.optimization.DiffFunction;

public class BilinPLObjectiveFunction implements DiffFunction{

	double [][][] jointFeatures;
	
	/**
	 * Sets the joint feature vectors. They need to be ordered already. 
	 * See disc.utilities.KronUtils
	 * @param jointFeatures
	 */
	public void setJointFeatures(double [][][] jointFeatures) {
		this.jointFeatures = jointFeatures;
	}
	
	@Override
	public int domainDimension() {
		return jointFeatures[0][0].length;
	}

	@Override
	public double valueAt(double[] w) {
		int N = jointFeatures.length;
		int M = jointFeatures[0].length;
		double P1 = 0d;
		for (int n=0;n<N;n++) {
			for (int m=0;m<M;m++) {
				P1 = P1 + this.calcDotProduct(w, jointFeatures[n][m]);
			}
		}
		double P2 = 0d;
		for (int n=0;n<N;n++) {
			for (int m=0;m<M;m++) {
				double innerP2 = 0d;
				for (int l=m;l<M;l++) {
					innerP2 = innerP2 + Math.exp(this.calcDotProduct(w, jointFeatures[n][l]));
				}
				P2 = P2 + Math.log(innerP2);
			}
		}
		return -P1 + P2;
	}

	@Override
	public double[] derivativeAt(double[] w) {
		int N = jointFeatures.length;
		int M = jointFeatures[0].length;
		int D = this.domainDimension();
		double [] grad = new double[D];
		for (int i=0;i<D;i++) {
			double P1 = 0d;
			for (int n=0;n<N;n++) {
				for (int m=0;m<M;m++) {
					P1 = P1 + jointFeatures[n][m][i];
				}
			}
			double P2 = 0d;
			for (int n=0;n<N;n++) {
				for (int m=0;m<M;m++) {
					double innerP2Numerator = 0d;
					double innerP2Denominator = 0d;
					for (int l=m;l<M;l++) {
						double tmp = Math.exp(this.calcDotProduct(w, jointFeatures[n][l]));
						innerP2Numerator = innerP2Numerator + jointFeatures[n][l][i] * tmp;
						innerP2Denominator = innerP2Denominator + tmp;
					}
					P2 = P2 + innerP2Numerator/innerP2Denominator;
				}
			}
			grad[i] = -P1 + P2;
		}
		return grad;
	}
	
	private double calcDotProduct(double[] x, double []y) {
		double result = 0d;
		for (int i=0;i<x.length;i++) {
			result = result + x[i]*y[i];
		}
		return result;
	}

}
