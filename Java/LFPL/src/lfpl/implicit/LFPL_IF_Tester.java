package lfpl.implicit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lfpl.implicit.io.LFPL_ImplicitFeedback_IO;
import utilities.MyUtils;

/**
 * See MF_experiments/LFPL_IF_Movies/IMPL_CHECK_TOYDATA.m
 * @author ds
 *
 */
public class LFPL_IF_Tester {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// Check Create Eval Data Routine
		LFPLIFDataStruct ds = LFPL_ImplicitFeedback_IO.importMatlabInteractionFile("data/K100LFPLdata.mat");
		MyUtils.printPrimitiveIntMatrix(ds.data);
	
		LFPL_IF method = new LFPL_IF();
		System.out.println("#Users: "+ds.nUsers);
		System.out.println("#Items: "+ds.nItems);
		method.init(ds.nUsers, ds.nItems, 10);
		method.trainLFPL_IF(700, 1000, ds);
		
	}
	
	public static void checkEvalRoutine() throws FileNotFoundException, IOException {
		// Check Create Eval Data Routine
		LFPLIFDataStruct ds = LFPL_ImplicitFeedback_IO.importMatlabInteractionFile("data/toy.mat");
		MyUtils.printPrimitiveIntMatrix(ds.data);
		
		System.out.println("--");
		LFPLIFDataStruct split = LFPL_IF_ContextualDataHandling.IFContextualDataSplit(ds.data,ds.user_starts, ds.user_sizes, ds.nUsers, ds.nItems);
		System.out.println("trdata");
		MyUtils.printPrimitiveIntMatrix(split.data);
		System.out.println("tedata");
		MyUtils.printPrimitiveIntMatrix(split.teData);
		//MyUtils.printPrimitiveIntArray(split.user_starts);
		
		System.out.println("-- eval data");
		ArrayList<int [][]> evalData = LFPL_IF_ContextualDataHandling.IFCreateContextualEvaluationData(ds.data,ds.user_starts, ds.user_sizes, ds.nItems, split.teData);
		Iterator<int [][]> it = evalData.iterator();
		while (it.hasNext()) {
			MyUtils.printPrimitiveIntMatrix(it.next());
		}
		
		LFPL_IF method = new LFPL_IF();
		method.init(5,5,10);
		
		method.U = new double[][] {
				{0.5377,-1.3077,-1.3499,-0.2050,0.6715},
				{1.8339,-0.4336,3.0349,-0.1241,-1.2075},
				{-2.2588,0.3426,0.7254,1.4897,0.7172},
				{0.8622,3.5784,-0.0631,1.4090,1.6302},
				{0.3188,2.7694,0.7147,1.4172,0.4889}
				};
		method.V = new double[][] {
				{1.0347,-1.0689,-1.7115,-0.1649,-1.1135},
				{0.7269,-0.8095,-0.1022,0.6277,-0.0068},
				{-0.3034,-2.9443,-0.2414,1.0933,1.5326},
				{0.2939,1.4384,0.3192,1.1093,-0.7697},
				{-0.7873,0.3252,0.3129,-0.8637,0.3714},
				{0.8884,-0.7549,-0.8649,0.0774,-0.2256},
				{-1.1471,1.3703,-0.0301,-1.2141,1.1174}
				};
		
		System.out.println("AUC = "+method.evaluate_AUC(evalData));
	}
	
	public static void checkSplitRoutine() throws FileNotFoundException, IOException {
		LFPLIFDataStruct ds = LFPL_ImplicitFeedback_IO.importMatlabInteractionFile("data/toy.mat");
		MyUtils.printPrimitiveIntMatrix(ds.data);
		
		System.out.println("--");
		LFPLIFDataStruct split = LFPL_IF_ContextualDataHandling.IFContextualDataSplit(ds.data,ds.user_starts, ds.user_sizes, ds.nUsers, ds.nItems);
		MyUtils.printPrimitiveIntMatrix(split.data);
		System.out.println("tedata");
		MyUtils.printPrimitiveIntMatrix(split.teData);
		MyUtils.printPrimitiveIntArray(split.user_starts);
	}
	
	public static void checkMatLoadRoutine() throws FileNotFoundException, IOException {
		LFPLIFDataStruct ds = LFPL_ImplicitFeedback_IO.importMatlabInteractionFile("data/toy.mat");
		int [][] sample = LFPL_IF_ContextualDataHandling.IFContextualDataSampling(ds.data, ds.user_starts, ds.user_sizes, ds.nUsers, ds.nItems, 100);
		MyUtils.printPrimitiveIntMatrix(sample);
	}
	
	public static void checkContextualDRSamplingRoutine() {
		int [][] interactions = new int[][] {
				{1,2},
				{1,3},
				{1,7},
				{2,1},
				{2,3},
				{2,4},
				{2,7},
				{3,1},
				{3,6},
				{4,3},
				{4,4},
				{4,5},
				{5,1},
				{5,2},
				{5,6},
				{5,7}
				};
		
		int [] user_starts = new int[]{1,4,8,10,13};
		for (int i = 0; i< user_starts.length; i++) { // adopt, because within java we better work with indices starting at zero.
			user_starts[i] = user_starts[i] - 1;
		}
		int [] user_sizes = new int[]{3,4,2,3,4};
		int nUsers = 5;
		int nItems = 7;
		int [][] sample = LFPL_IF_ContextualDataHandling.IFContextualDataSampling(interactions, user_starts, user_sizes, nUsers, nItems, 100);
		MyUtils.printPrimitiveIntMatrix(sample);
		//System.out.println(LFPL_IF_DataHandling.IFContextualDataContains(interactions, user_starts, user_sizes, 1,3));
		// OK
	}
	
	
	public static void prelimLoadDev() {
		int [][] toyData = new int[][] {
				{1,3},
				{1,2},
				{1,7},
				{2,1},
				{2,3},
				{2,7},
				{2,4},
				{3,1},
				{3,6},
				//{4,3},
				//{4,5},
				//{4,4},
				{5,2},
				{5,6},
				{5,7},
				{5,1}
				};
		int [] users = MyUtils.getNthIntColumnFromMatrix(toyData,0);
		List<Integer> list = Arrays.stream(users).boxed().collect(Collectors.toList());

		HashSet hsU = new HashSet();
        hsU.addAll(list); 
        System.out.println(hsU.size());
        System.out.println(hsU);
        int nUsers = hsU.size();
        
        int [] items = MyUtils.getNthIntColumnFromMatrix(toyData,1);
        HashSet hsI= new HashSet();
        hsI.addAll( Arrays.stream(items).boxed().collect(Collectors.toList())); 
        System.out.println(hsI.size());
        System.out.println(hsI);
        int nItems = hsI.size();
	}
	
	public static void checkGradRoutine() {
		LFPL_IF method = new LFPL_IF();
		method.init(5,5,10);
		
		method.U = new double[][] {
				{1.8090,-0.7226,0.7009,-0.6035,0.2380},
				{-0.6161,1.4182,0.1413,0.1742,0.8268},
				{0.6840,0.6858,-0.6295,-0.4296,-1.6027},
				{-1.2161,0.3587,-1.0816,-0.3542,0.5677},
				{0.3839,-0.3872,-2.4632,-0.4227,-1.5174}
				};
		method.V = new double[][] {
				{1.1421,-0.5993,-1.8267,-0.3024,0.9431},
				{-0.5229,-0.6982,-2.2327,-0.2830,-0.8045},
				{-0.1919,-1.2808,-0.8351,-0.1705,-1.4457},
				{-2.0075,-1.5540,0.0086,-1.8730,1.0233},
				{0.2686,0.0668,1.7853,0.0292,0.6852},
				{-0.0340,0.8740,1.6758,-2.0678,-2.1161},
				{0.1964,0.5632,0.2170,-1.1748,-0.6646}
				};
		
		int [][] data = new int[][] {
				{3,6,2},
				{2,1,5},
				{3,6,1},
				{1,3,4},
				{1,3,5},
				{2,3,5},
				{5,2,1},
				{3,6,4},
				{3,6,7},
				{2,3,2}
				};
		
		double L = method.getLogLikelihood(data);
		System.out.println("Log-likelihood : " + L);
	
		method.calculateContextualGradient(data,1);
		MyUtils.printPrimitiveDoubleMatrix(method.gradU);
		// OK
		
		//method.getContextualGradient(data,2);
		//MyUtils.printPrimitiveDoubleMatrix(method.gradV);
		// OK
		
		double [][] rweights = MyUtils.sampleGaussianPrimitiveDoubleMatrix(3,5);
		MyUtils.printPrimitiveDoubleMatrix(rweights);
	}
	

}
