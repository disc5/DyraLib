package lfpl.implicit;

import java.util.ArrayList;

import utilities.MyUtils;

/**
 * Implementation of LFPL/IF for contextual dyadic preferences.
 * 
 * @author Dirk Schaefer , 2016
 *
 */
public class LFPL_IF {
	
	/* Basic model variables */
	public double [][] U;
	public double [][] V;
	public boolean withBias = false;
	public double regU = 0d;
	public double regV = 0d;
	
	/* Gradients */
	double [][] gradU;
	double [][] gradV;
	
	/* AdaGrad variables */
	double [][] stepSizeU;
	double [][] stepSizeV;
	double [][] gradHistoryU;
	double [][] gradHistoryV;
	double fudge_factor = 1e-6;
	double gamma = 1d;
	
	/**
	 * Initializes the model with random weights (from N(0,1)).
	 * @param N - number of row entities (or users)
	 * @param M - number of column entities (or items)
	 * @param K - number of latent factors
	 */
	public void init(int N, int M, int K) {
		this.U = MyUtils.sampleGaussianPrimitiveDoubleMatrix(N,K);
		this.V = MyUtils.sampleGaussianPrimitiveDoubleMatrix(M,K);
		this.gradHistoryU = MyUtils.createZeroDoubleMatrix(N, K);
		this.gradHistoryV = MyUtils.createZeroDoubleMatrix(M, K);
		this.stepSizeU = MyUtils.createZeroDoubleMatrix(N, K);
		this.stepSizeV = MyUtils.createZeroDoubleMatrix(M, K);
	}
	
	/**
	 * LFPL/IF Trainer
	 * 
	 * @param maxIter
	 * @param batchSize
	 * @param ds
	 */
	public void trainLFPL_IF(int maxIter, int batchSize, LFPLIFDataStruct ds) {
		LFPLIFDataStruct split = LFPL_IF_ContextualDataHandling.IFContextualDataSplit(ds.data,ds.user_starts, ds.user_sizes, ds.nUsers, ds.nItems);
		ArrayList<int [][]> evalData = LFPL_IF_ContextualDataHandling.IFCreateContextualEvaluationData(ds.data,ds.user_starts, ds.user_sizes, ds.nItems, split.teData);
		double [] AUC_hist = new double[maxIter];
		for (int i1 = 0; i1 < maxIter; i1++) {
			System.out.print(".");
			int [][] sample = LFPL_IF_ContextualDataHandling.IFContextualDataSampling(split.data, split.user_starts, split.user_sizes, split.nUsers, split.nItems, batchSize);
			updateStep(sample);
			AUC_hist[i1] = this.evaluate_AUC(evalData);
			if (i1 % 50 == 0) {
				System.out.println("\nIt: "+ (i1+1) + " AUC = "+ AUC_hist[i1]);
			}
		}
		System.out.println("AUC = "+ AUC_hist[maxIter-1]);
	}
	
	/**
	 * Trains an LFPL/IF model.
	 * V2: In contrast to v1 the training/test split and the evaluation data must created outside of the method.
	 * 
	 * @param maxIter
	 * @param batchSize
	 * @param train
	 * @param evalData
	 */
	public void trainLFPL_IF2(int maxIter, int batchSize, LFPLIFDataStruct train, ArrayList<int [][]> evalData) {
		double [] AUC_hist = new double[maxIter];
		for (int i1 = 0; i1 < maxIter; i1++) {
			System.out.print(".");
			int [][] sample = LFPL_IF_ContextualDataHandling.IFContextualDataSampling(train.data, train.user_starts, train.user_sizes, train.nUsers, train.nItems, batchSize);
			updateStep(sample);
			AUC_hist[i1] = this.evaluate_AUC(evalData);
			if (i1 % 50 == 0) {
				System.out.println("\nIt: "+ (i1+1) + " AUC = "+ AUC_hist[i1]);
			}
		}
		System.out.println("AUC = "+ AUC_hist[maxIter-1]);
	}
	
	/**
	 * Performs one model update step with AdaGrad on a batch of data.
	 * @param data
	 */
	public void updateStep(int [][] data) {
		
		// Update U
		calculateContextualGradient(data, 1);
		int N = this.U.length;
		int K = this.U[0].length;

		for (int i1 = 0; i1 < N; i1++) {
			for (int k = 0; k < K; k++) {
				this.gradHistoryU[i1][k] = this.gradHistoryU[i1][k] + this.gradU[i1][k] * this.gradU[i1][k];
				this.stepSizeU[i1][k] = this.gamma / (this.fudge_factor + Math.sqrt(this.gradHistoryU[i1][k]));
				this.U[i1][k] = this.U[i1][k] - this.stepSizeU[i1][k] * this.gradU[i1][k];
			}
		}
		
		if (this.withBias == true) {
			for (int i1 = 0; i1 < N; i1++) {
				this.U[i1][0] = 1d;
				this.U[i1][K-1] = 1d;
			}
		}
		
		// Update V
		calculateContextualGradient(data, 2);
		int M = this.V.length;
		
		for (int i1 = 0; i1 < M; i1++) {
			for (int k = 0; k < K; k++) {
				this.gradHistoryV[i1][k] = this.gradHistoryV[i1][k] + this.gradV[i1][k] * this.gradV[i1][k];
				this.stepSizeV[i1][k] = this.gamma / (this.fudge_factor + Math.sqrt(this.gradHistoryV[i1][k]));
				this.V[i1][k] = this.V[i1][k] - this.stepSizeV[i1][k] * this.gradV[i1][k];
			}
		}
		
		if (this.withBias == true) {
			for (int i1 = 0; i1 < N; i1++) {
				this.U[i1][0] = 1d;
				this.U[i1][K-1] = 1d;
			}
		}
		
		
	}
	
	/**
	 * Updates the current gradients with the current model parameters.
	 * 
	 * @param data: triplets - row, col1, col2 ,e.g., user item_a, item_b
	 * @param gradType: 1 = Gradient U (V fixed), 2 = Gradient V (U fixed)
	 */
	public void calculateContextualGradient(int [][] data, int gradType) {
		int nSamples = data.length;
		double [][] W = multiplyFactors(this.U, this.V);
		
		if (gradType == 1) { // Calculate gradient with regard to U (V fixed)
			int N = U.length;
			int K = U[0].length;
			gradU = new double[N][K];
			for (int i1 = 0; i1 < nSamples; i1++) {
				int idx = data[i1][0] - 1;
				int idy1 = data[i1][1] - 1;
				int idy2 = data[i1][2] - 1;
				
				double [] numerator = new double[K];
				for (int k = 0; k < K; k++) {
					numerator[k] = V[idy1][k] * Math.exp(W[idx][idy1]) + V[idy2][k] * Math.exp(W[idx][idy2]);
				}
				double denominator = Math.exp(W[idx][idy1]) + Math.exp(W[idx][idy2]);
				for (int k = 0; k < K; k++) {
					gradU[idx][k] = gradU[idx][k] + (numerator[k] / denominator) - V[idy1][k];
				}
			}
			
			// Regularization
			if (this.regU > 0d) {
				if (this.withBias == true) {
					for (int i1 = 0; i1 < N; i1++) {
						for (int i2 = 1; i2 < K-1; i2++) { // exclude first and last elements of U,V
							gradU[i1][i2] = gradU[i1][i2] + this.regU * this.U[i1][i2];
						}
					}
				} else {
					for (int i1 = 0; i1 < N; i1++) {
						for (int i2 = 0; i2 < K; i2++) {
							gradU[i1][i2] = gradU[i1][i2] + this.regU * this.U[i1][i2];
						}
					}
				}
			}
			
		} else { // Calculate gradient with regard to V (U fixed)
			int M = V.length;
			int K = V[0].length;
			gradV = new double[M][K];
			
			for (int i1 = 0; i1 < nSamples; i1++) {
				int idx = data[i1][0] - 1;
				int idy1 = data[i1][1] - 1;
				int idy2 = data[i1][2] - 1;
				
				double denominator = Math.exp(W[idx][idy1]) + Math.exp(W[idx][idy2]);
			
				
				for (int k = 0; k < K; k++) {
					gradV[idy1][k] = gradV[idy1][k] + (U[idx][k] * Math.exp(W[idx][idy1]))/denominator - U[idx][k];
				}
				
				for (int k = 0; k < K; k++) {
					gradV[idy2][k] = gradV[idy2][k] + (U[idx][k] * Math.exp(W[idx][idy2]))/denominator;
				}
				
			}
			
			// Regularization
			if (this.regV > 0d) {
				if (this.withBias == true) {
					for (int i1 = 0; i1 < M; i1++) {
						for (int i2 = 1; i2 < K-1; i2++) { // exclude first and last elements of U,V
							gradV[i1][i2] = gradV[i1][i2] + this.regV * this.V[i1][i2];
						}
					}
				} else {
					for (int i1 = 0; i1 < M; i1++) {
						for (int i2 = 0; i2 < K; i2++) {
							gradV[i1][i2] = gradV[i1][i2] + this.regV * this.V[i1][i2];
						}
					}
				}
			}
		}
	}
	
	/**
	 * Calculates the data log-likelihood of LFPL/FI
	 * @param data
	 * @return log-likelihood
	 */
	public double getLogLikelihood(int [][] data) {
		double L = 0d;
		int nSamples = data.length;
		double [][] W = multiplyFactors(this.U, this.V);
		MyUtils.printPrimitiveDoubleMatrix(W);
		for (int i1 = 0; i1 < nSamples; i1++) {
			int idx = data[i1][0] - 1;
			int idy1 = data[i1][1] - 1;
			int idy2 = data[i1][2] - 1;
			
			L = L + ( Math.log( Math.exp(W[idx][idy1]) + Math.exp(W[idx][idy2]) ) - W[idx][idy1]  );
		}
		L = L/(double)nSamples;
		return L;
	}
	
	/**
	 * Multiplies matrix U times transpose matrix V
	 * @param U: K x N matrix
	 * @param V: K x M matrix
	 * @return N x M matrix
	 */
	public double [][] multiplyFactors(double[][] U, double[][] V) {
		int N = U.length;
		int K = U[0].length;
		int M = V.length;
		double [][] W = new double[N][M];
		for (int i1 = 0; i1 < N; i1++) {
			for (int i2 = 0; i2 < M ; i2++) {
				double dotProd = 0d;
				for (int i3 = 0; i3 < K; i3++) {
					dotProd = dotProd + U[i1][i3]*V[i2][i3];
				}
				W[i1][i2] = dotProd;
			}
		}
		return W;
	}
	
	public double evaluate_AUC(ArrayList<int [][]> evalTriplets) {
		double AUC = 0d;
		int nUsers = evalTriplets.size();
		double [][] W = multiplyFactors(this.U, this.V);
	
		int [][] ctTriplets;
		int nEu, cntPos = 0;
		double scoreA, scoreB = 0d;
		for (int i1=0; i1 < nUsers; i1++) {
			ctTriplets = evalTriplets.get(i1);
			nEu = ctTriplets.length;
			cntPos = 0;
			for (int i2 = 0; i2 < nEu; i2++) {
				scoreA = W[ctTriplets[i2][0]-1][ctTriplets[i2][1]-1];
				scoreB = W[ctTriplets[i2][0]-1][ctTriplets[i2][2]-1];
				if (scoreA > scoreB) {
					cntPos++;
				}
			}
			AUC = AUC + ((double)cntPos/(double)nEu);
		}
		AUC = AUC / (double) nUsers;
		return AUC;
	}
}
