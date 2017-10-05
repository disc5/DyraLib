package disc.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	
	public static void printLongArray(long [] data) {
		for (int i=0; i<data.length; i++) {
			System.out.print(data[i]+" ");
		}
		System.out.println();
	}
	
	public static void printIntArray(int [] data) {
		for (int i=0; i<data.length; i++) {
			System.out.print(data[i]+" ");
		}
		System.out.println();
	}
	
	public static int [][] convertDouble2dArrayToIntArray(double [][] darray) {
		int dim1 = darray.length;
		int dim2 = darray[0].length;
		int[][] iarray = new int[dim1][dim2];
		for (int i=0;i<dim1;i++){
			for (int j=0;j<dim2;j++) {
				iarray[i][j] = (int) darray[i][j];
			}
		}
		return iarray;
	}
	
	/**
	 * Returns an ordering given a double array of scores (arr).
	 * The ordering array contains numbers ranging from 1 to arr.length.
	 * This means that objects that have a higher score will be placed before the others.
	 * @param arr
	 * @return
	 */
	public static double [] getOrdering(double [] arr) {
		double [] ordering = new double[arr.length];
		List<Integer> indices = new ArrayList<Integer>(arr.length);
		for (int i = 0; i < arr.length; i++) {
		  indices.add(i);
		}
		Comparator<Integer> comparator = new Comparator<Integer>() {
		  public int compare(Integer i, Integer j) {
		    return Double.compare(arr[i], arr[j]);
		  }
		};
		
		Collections.sort(indices, Collections.reverseOrder(comparator));
		for (int i=0;i<arr.length;i++) {
			ordering[i] = indices.get(i) + 1;
		}
		return ordering;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
