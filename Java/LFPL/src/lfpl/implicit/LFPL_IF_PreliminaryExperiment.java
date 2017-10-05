package lfpl.implicit;

import java.io.FileNotFoundException;
import java.io.IOException;

import lfpl.implicit.io.LFPL_ImplicitFeedback_IO;
import utilities.MyIoUtils;
import utilities.MyUtils;

public class LFPL_IF_PreliminaryExperiment {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		//LFPLIFDataStruct ds = MatlabInteractionFileImporter.importMatlabInteractionFile("data/K100LFPLdata.mat");
		LFPLIFDataStruct ds = LFPL_ImplicitFeedback_IO.importMatlabInteractionFile("data/M1LFPLdata.mat");
		MyUtils.printPrimitiveIntMatrix(ds.data);
	
		LFPL_IF method = new LFPL_IF();
		System.out.println("#Users: "+ds.nUsers);
		System.out.println("#Items: "+ds.nItems);
		method.init(ds.nUsers, ds.nItems, 10);
		method.regU = 0.01;
		method.regV = 0.01;
		int numIter = 1200;
		int batchSize = 2000;//1000;
		method.trainLFPL_IF(numIter, batchSize, ds);
		//MyIoUtils.writeDoubleArray("results/LFPL_1M_U.dat", method.U);
		//MyIoUtils.writeDoubleArray("results/LFPL_1M_V.dat", method.V);
		
		// iter / btachsize
		// 1200/1000 - 0.83
		// 1200/2000 - .8573
		// 1200/1000 - regU/V = 0.01 -> 0.90 -> exported to results and used for unfolding!
		// 1200/2000 - regU/V = 0.01 -> 0.90 already at 950/1000 iterations, final: 0.9065
		// 1200,2000 - reguU/V = 0.1 -> UC = 0.874974503004997 
	}

}
