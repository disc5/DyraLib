package lfpl.implicit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import lfpl.implicit.io.LFPL_ImplicitFeedback_IO;
import utilities.MyIoUtils;
import utilities.MyUtils;

public class LFPL_IF_ML1M_experiment_10folds {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		LFPLIFDataStruct ds = LFPL_ImplicitFeedback_IO.importMatlabInteractionFile("data/M1LFPLdata.mat");
		MyUtils.printPrimitiveIntMatrix(ds.data);
	
		LFPL_IF method = new LFPL_IF();
		System.out.println("#Users: "+ds.nUsers);
		System.out.println("#Items: "+ds.nItems);
		
		String baseFolder = "D:/LFPLResults/";
		String aucResultFileStr = "ML1M_LFPL_auc_results.csv";
		int numFolds = 10;
		double [] AUC_results = new double[numFolds];
		
	
		for (int i1 = 0; i1 < numFolds; i1++) {
			System.out.println("--\nProcessing fold: "+(i1+1));
			method.init(ds.nUsers, ds.nItems, 10);
			method.regU = 0.01;    //0.01; Commented params result in avg AUC of 0.90
			method.regV = 0.01;    //0.01;
			int numIter = 5000;        //1000;
			int batchSize = 2000; //1000    //2000;
			
			LFPLIFDataStruct split = LFPL_IF_ContextualDataHandling.IFContextualDataSplit(ds.data,ds.user_starts, ds.user_sizes, ds.nUsers, ds.nItems);
			ArrayList<int [][]> evalData = LFPL_IF_ContextualDataHandling.IFCreateContextualEvaluationData(ds.data,ds.user_starts, ds.user_sizes, ds.nItems, split.teData);
		
			method.trainLFPL_IF2(numIter, batchSize, split, evalData);
			double AUC = method.evaluate_AUC(evalData);
			AUC_results[i1] = AUC;
			System.out.println("Fold AUC = "+ AUC);
			String subFolder = "fold_"+(i1+1)+"/";
			LFPL_ImplicitFeedback_IO.exportInteraction2CSV(baseFolder + subFolder,  split);
			MyIoUtils.writeDoubleArray(baseFolder + subFolder + "LFPL_1M_U.dat", method.U);
			MyIoUtils.writeDoubleArray(baseFolder + subFolder + "LFPL_1M_V.dat", method.V);
		}
		
		MyIoUtils.writeSingleArray(baseFolder + aucResultFileStr, AUC_results);
		
		
	}

}
