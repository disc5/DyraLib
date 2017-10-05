package disc.utilities.bilinpl;

public class KronUtils {

	public static double [] getKroneckerVecProduct(double [] x, double [] y) {
		int dimx = x.length;
		int dimy = y.length;
		double [] kronVec = new double[dimx*dimy];
		int cnt = 0;
		for (int i=0;i<dimx;i++) {
			for (int j=0;j<dimy;j++) {
				kronVec[cnt] = x[i]*y[j];
				cnt = cnt + 1;
			}
		}
		return kronVec;
	}
	
	/**
	 * Calculates a matrix of ordered Kronecker-Vectors for a data set.
	 * @param instances (N instances of dimensionality r)
	 * @param labelFeatures (M label instances of dimensionality c)
	 * @param ordering (N orderings of lenght M, where "1" refers to the first label.
	 * @return
	 */
	public static double [][][] getOrderedKronJointFeatureTensor(double [][] instances, double [][] labelFeatures, int [][] ordering) {
		int N = instances.length;
		int M = labelFeatures.length;
		int R = instances[0].length;
		int C = labelFeatures[0].length;
		double [][][] kronTensor = new double[N][M][R*C];
		for (int n=0;n<N;n++) {
			for (int m=0;m<M;m++) {
				kronTensor[n][m] = getKroneckerVecProduct(instances[n], labelFeatures[ordering[n][m]-1]);
			}
		}
		return kronTensor;
	}
	
	public static double [][][] getKronJointFeatureTensor(double [][] instances, double [][] labelFeatures) {
		int N = instances.length;
		int M = labelFeatures.length;
		int R = instances[0].length;
		int C = labelFeatures[0].length;
		double [][][] kronTensor = new double[N][M][R*C];
		for (int n=0;n<N;n++) {
			for (int m=0;m<M;m++) {
				kronTensor[n][m] = getKroneckerVecProduct(instances[n], labelFeatures[m]);
				
			}
		}
		return kronTensor;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
