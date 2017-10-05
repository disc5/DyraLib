package lfpl.implicit.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import lfpl.implicit.LFPLIFDataStruct;
import utilities.MyIoUtils;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLNumericArray;

public class LFPL_ImplicitFeedback_IO {

	public static LFPLIFDataStruct importMatlabInteractionFile(String filename) throws FileNotFoundException, IOException {
		LFPLIFDataStruct ds = new LFPLIFDataStruct();
		
		
		MatFileReader mfr = new MatFileReader( filename );
		Map<String, MLArray> content = mfr.getContent();
		//System.out.println(content.keySet());
		MLNumericArray data = (MLNumericArray) content.get("data");
		int N = data.getN();
		int M = data.getM();
		int [][] data2 = new int[M][N];
		for (int i=0;i<M;i++) {
			data2[i][0] = data.get(i,0).intValue();
			data2[i][1] = data.get(i,1).intValue();
		}
		ds.data = data2;
		
		MLNumericArray tmp = (MLNumericArray) content.get("nItems");
		ds.nItems = tmp.get(0).intValue();
		//System.out.println(ds.nItems);
		tmp = (MLNumericArray) content.get("nUsers");
		ds.nUsers = tmp.get(0).intValue();
		
		tmp = (MLNumericArray) content.get("user_sizes");
		N = tmp.getN();
		M = tmp.getM(); // rows
		int [] user_sizes = new int[M];
		for (int i=0;i<M;i++) {
			user_sizes[i] = tmp.get(i).intValue();
		}
		ds.user_sizes = user_sizes;
		//MyUtils.printPrimitiveIntArray(user_sizes);
		
		tmp = (MLNumericArray) content.get("user_starts");
		N = tmp.getN();
		M = tmp.getM();
		int [] user_starts = new int[M];
		for (int i=0;i<M;i++) {
			user_starts[i] = tmp.get(i).intValue() - 1; // Converted for java 0-indexed array structures
		}
		ds.user_starts = user_starts;
		//MyUtils.printPrimitiveIntArray(user_starts);
		return ds;
	}
	
	/**
	 * Writes all fields from LFPLIF to the file system.
	 * This operation is required, e.g., to allow other methods to train and evaluate on the exact training and test set splits.
	 * 
	 * @param pathStr
	 * @param ds
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void exportInteraction2CSV(String pathStr, LFPLIFDataStruct ds) throws FileNotFoundException, IOException {
		System.out.println("Exporting LFPL/IF Data Structure ...");
		String trDataFileStr = "data.csv";
		String teDataFileStr = "te_data.csv";
		String user_starts_FileStr = "user_starts.csv";
		String user_sizes_FileStr = "user_sizes.csv";
		String numberOfEntitiesFileStr = "num_entities.csv";
		// Note1: user_starts, user_sizes are required for trData only. For teData it its clear that is has as many rows 
		// as there are users.
		// Note2: If you want to measure the performance of other methods, you need create an evaluation set using trData and teData as realized in function
		// IFCreateContextualEvaluationData
		
		if (!pathStr.endsWith("/")) {
			pathStr = pathStr + "/";
		}
		


		File theDir = new File(pathStr);
		
		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + pathStr);
		    boolean result = false;
		
		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("DIR created");  
		    }
		}


		
		
		MyIoUtils.writeIntArray(pathStr+trDataFileStr, ds.data);
		
		if (ds.teData != null) {
			MyIoUtils.writeIntArray(pathStr+teDataFileStr, ds.teData);
		}
		
		MyIoUtils.writeSingleIntArray(pathStr + user_starts_FileStr, ds.user_starts);
		
		MyIoUtils.writeSingleIntArray(pathStr + user_sizes_FileStr, ds.user_sizes);
		
		int [] nEntities = new int[]{ds.nUsers, ds.nItems};
		MyIoUtils.writeSingleIntArray(pathStr + numberOfEntitiesFileStr, nEntities);
		
		/*
		public int [][] data;
		public int [] user_starts;
		public int [] user_sizes;
		public int nUsers;
		public int nItems;
		*/
		/* teData differs from data in that there is only one single interaction between a row and an item */
		//public int [][] teData; 
		System.out.println("done");
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			LFPLIFDataStruct ds = LFPL_ImplicitFeedback_IO.importMatlabInteractionFile("data/toy.mat");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
