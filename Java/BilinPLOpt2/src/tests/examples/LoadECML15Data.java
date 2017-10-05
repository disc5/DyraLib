package tests.examples;

import java.util.Map;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLDouble;

import disc.utilities.MyUtils;

public class LoadECML15Data {

	public static void main(String[] args) throws Exception{
		String filename = "data/ecml15/ga_tsp_meta_cv_10.mat";
		MatFileReader mreader = new MatFileReader(filename);
		//System.out.println(mreader.toString());
		Map<String,MLArray> map = mreader.getContent();
		System.out.println(map);
		/*
		 * {
		 * numTrInst=[1x9  double array], 
		 * numLabels=[1x1  double array], 
		 * numIt=[1x1  double array], 
		 * XTrFeatures=[9x1x10  cell array], 
		 * XTeFeatures=[9x1x10  cell array], 
		 * YFeatures=[9x1x10  cell array], 
		 * TrOrderings=[9x1x10  cell array], 
		 * TeOrderings=[9x1x10  cell array]
		 * }
		 * 
		 */
		int ct_trSetting = 0;
		int ct_repetition = 0;
		double [][] trFeat = readMatIOCell2DoubleArray(map.get("XTrFeatures"),ct_trSetting, ct_repetition);
		double [][] teFeat = readMatIOCell2DoubleArray(map.get("XTeFeatures"),ct_trSetting, ct_repetition);
		double [][] YFeat = readMatIOCell2DoubleArray(map.get("YFeatures"),ct_trSetting, ct_repetition);
		double [][] trOrderings = readMatIOCell2DoubleArray(map.get("TrOrderings"),ct_trSetting, ct_repetition);
		double [][] teOrderings = readMatIOCell2DoubleArray(map.get("TeOrderings"),ct_trSetting, ct_repetition);
	}
	
	public static double [][] readMatIOCell2DoubleArray(MLArray mla, int trSetting, int repetition) {
		MLDouble mld;
		MLCell mlc = (MLCell) mla;
		mld = (MLDouble) mlc.get(trSetting,repetition);
		double [][] a = mld.getArray();
		return a;
	}

}
