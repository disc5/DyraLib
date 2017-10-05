package utilities;

import java.util.ArrayList;

public class MyUtils {

	public static void printPrimitiveDoubleArrayList(ArrayList<double []> data, int maxNumEntries) {
		int cnt = 0;
		for (int i=0;i<data.size();i++) {
			System.out.print("Item "+(i+1)+ "|");
			printDoubleArray(data.get(i));
			cnt = cnt + 1;
			
			if (cnt >= maxNumEntries) break;
		}
		//System.out.println();
	}
	
	public static void printPrimitiveDoubleArrayList(ArrayList<double []> data) {
		for (int i=0;i<data.size();i++) {
			System.out.print("Item "+i+"| ");
			printDoubleArray(data.get(i));
		}
		//System.out.println();
	}
	
	public static void printDoubleArrayList(ArrayList<Double> data) {
		for (int i=0;i<data.size();i++) {
			System.out.print(data.get(i)+ " ");
		}
		System.out.println();
	}
	
	public static void printDoubleArray(double [] data) {
		for (int i=0; i<data.length; i++) {
			System.out.print(data[i]+" ");
		}
		System.out.println();
	}
	
	public static void printPrimitiveDoubleMatrix(double [][] data) {
		for (int i=0; i<data.length; i++) {
			for (int i2 =0; i2 < data[0].length; i2++) {
				System.out.print(data[i][i2]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void printPrimitiveIntMatrix(int [][] data) {
		for (int i=0; i<data.length; i++) {
			for (int i2 =0; i2 < data[0].length; i2++) {
				System.out.print(data[i][i2]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void printPrimitiveIntArray(int [] data) {
		for (int i=0; i<data.length; i++) {		
			System.out.print(data[i]+" ");
		}
		System.out.println();
	}
	
	public static double [][] sampleGaussianPrimitiveDoubleMatrix(int N, int M) {
		double [][] X = new double[N][M];
		for (int i1 = 0; i1 < N; i1++) {
			for (int i2 = 0; i2 < M; i2++) {
				X[i1][i2] = StdRandom.gaussian(0d,0.1);
			}
		}
		return X;
	}
	
	public static double [][] sampleGaussianPrimitiveDoubleMatrix(int N, int M, long seed) {
		StdRandom.setSeed(seed);
		double [][] X = new double[N][M];
		for (int i1 = 0; i1 < N; i1++) {
			for (int i2 = 0; i2 < M; i2++) {
				X[i1][i2] = StdRandom.gaussian();
			}
		}
		return X;
	}
	
	public static double [][] createZeroDoubleMatrix(int rows, int columns) {
		double [][] X = new double[rows][columns];
		for (int i1 = 0; i1 < rows; i1++) {
			for (int i2 = 0; i2 < columns; i2++) {
				X[i1][i2] = 0d;
			}
		}
		return X;
	}
	
	/**
	 * Returns the n-th column of a primitive int matrix
	 * @param Mat
	 * @param colId - 0-indexed
	 * @return
	 */
	public static int[] getNthIntColumnFromMatrix(int [][] Mat, int colId) {
		int N = Mat.length;
		//int p = Mat[0].length;
		int [] result = new int[N];
		for (int i= 0; i< N; i++) {
			result[i] = Mat[i][colId];
		}
		return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
