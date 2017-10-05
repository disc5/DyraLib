package lfpl.implicit;

import java.util.ArrayList;

import utilities.MyUtils;
import utilities.StdRandom;

public class LFPL_IF_ContextualDataHandling {
	
	/**
	 * Creates a random sample from the contextual interaction data. The sample is in the format of a contextual dyad ranking (triplet).
	 * 
	 * @param data N x 2 matrix, contains interactions <userLabel, itemLabel>
	 * @param user_starts - 0-indexed (!)
	 * @param user_sizes
	 * @param nUsers
	 * @param nItems
	 * @param sampleSize
	 * @return
	 */
	public static int [][] IFContextualDataSampling(int [][] data, int [] user_starts, int [] user_sizes, int nUsers, int nItems, int sampleSize) {
		int [][] subset = new int [sampleSize][3];
		int userId, userLabel = 0;
		int itemA, itemB, itemRelDataId, itemAbsDataId = 0;
		
		for (int i1 = 0; i1 < sampleSize; i1++ ) {
			userId = StdRandom.uniform(nUsers);
			itemRelDataId = StdRandom.uniform(user_sizes[userId]);
			itemAbsDataId = user_starts[userId] + itemRelDataId;
			userLabel = data[itemAbsDataId][0];
			itemA = data[itemAbsDataId][1];
			itemB = StdRandom.uniform(nItems) + 1;
			
			while (IFContextualDataContains(data, user_starts, user_sizes, userId, itemB) == true) {
				itemB = StdRandom.uniform(nItems) + 1;
			}
			subset[i1][0] = userLabel;
			subset[i1][1] = itemA;
			subset[i1][2] = itemB;
			
		}
		return subset;
	}
	
	/**
	 * Checks if an item (1-indexed) is contained in the interaction set of a user (0-indexed).
	 * 
	 * @param data N x 2 matrix, contains interactions <userLabel, itemLabel>
	 * @param user_starts (0-indexed)
	 * @param user_sizes
	 * @param userId (0-indexed)
	 * @param itemB (1-index)
	 * @return
	 */
	public static boolean IFContextualDataContains(int [][] data, int [] user_starts, int [] user_sizes, int userId, int itemLabel) {
		boolean isContained = false;
		int uStart = user_starts[userId];
		int uSize = user_sizes[userId];
		int ctItem = 0;
		for (int i1 = 0; i1 < uSize; i1++) {
			 ctItem = data[uStart+i1][1];
			 if (ctItem > itemLabel) {
				 break;
			 } else if (ctItem == itemLabel) {
				 isContained = true;
			 }
		}
		return isContained;
	}
	
	/**
	 * Splits a data set into training and test sets for LOOCV.
	 * @param data
	 * @param user_starts
	 * @param user_sizes
	 * @param nUsers
	 * @return
	 */
	public static LFPLIFDataStruct IFContextualDataSplit(int [][] data, int [] user_starts, int [] user_sizes, int nUsers, int nItems) {
		LFPLIFDataStruct ds = new LFPLIFDataStruct();
		int [][] teData = new int[nUsers][2];
		int [] rmIndices = new int[nUsers];
		int [][] trData = new int[data.length-nUsers][2];

		int userId, userLabel = 0;
		int itemA, itemB, itemRelDataId, itemAbsDataId = 0;
		
		for (int i=0; i< nUsers; i++) {
			userId = i;
			itemRelDataId = StdRandom.uniform(user_sizes[userId]);
			itemAbsDataId = user_starts[userId] + itemRelDataId;
			userLabel = data[itemAbsDataId][0];
			itemA = data[itemAbsDataId][1];
			teData[i][0] = userLabel;
			teData[i][1] = itemA;
			rmIndices[i] = itemAbsDataId;
		}
		
		//System.out.println("Rm Indices");
		//MyUtils.printPrimitiveIntArray(rmIndices);
		
		int cnt1 = 0;
		int cnt2 = 0;
		int ctIdx = 0;
		ctIdx = rmIndices[cnt2];
		for (int i=0; i< data.length; i++) {
			//System.out.println("i = " + i + " ctIdx = "+ ctIdx + " (cnt2 = "+cnt2+" )");
			if (i!=ctIdx) {
				//System.out.println("assign data[i][0] = "+data[i][0]+" to trData[cnt1][0] with cnt1 = "+
					//	cnt1);
				trData[cnt1][0] = data[i][0];
				trData[cnt1][1] = data[i][1];
				cnt1++;
			} else if (i==ctIdx){
				// skip
				cnt2++;
				if (cnt2 < rmIndices.length) {
					ctIdx = rmIndices[cnt2];
				}
			}
		}
		ds.user_sizes = new int[nUsers];
		ds.user_starts = new int[nUsers];
		for (int i=0; i< nUsers; i++) {
			ds.user_sizes[i] = user_sizes[i] -1;
			ds.user_starts[i] = user_starts[i] - i;
		}
		
		ds.data = trData;
		ds.teData = teData;
		ds.nUsers = nUsers;
		ds.nItems = nItems;
		return ds;
	}
	
	/**
	 * Creates evaluation data for LOOCV on the test data created with the split routine.
	 * 
	 * @param data - this are the overall data not the training data
	 * @param user_starts
	 * @param user_sizes
	 * @param nItems
	 * @param teData
	 * @return
	 */
	public static ArrayList<int [][]> IFCreateContextualEvaluationData(int [][] data, int [] user_starts, int [] user_sizes,  int nItems, int [][] teData) {
		ArrayList<int[][]> evalData = new ArrayList<int[][]>();
		int N = teData.length;
		int [][] triplets;
		int userLabel, testItemLabel = 0;
		int cnt = 0;
		for (int i1 = 0; i1 < N ; i1++) {
			cnt = 0;
			userLabel = teData[i1][0]; // should correspond to (i1+1)
			testItemLabel = teData[i1][1];		
			triplets = new int[nItems-user_sizes[i1]][3];
			for (int i2 = 0; i2 < nItems; i2++) {
				if (IFContextualDataContains(data, user_starts, user_sizes, i1, (i2+1)) == false) {
					triplets[cnt][0] = userLabel;
					triplets[cnt][1] = testItemLabel;
					triplets[cnt][2] = (i2+1);
					cnt = cnt + 1;
				}
			}
			evalData.add(triplets);
		}
		return evalData;
	}
	
	/**
	 * Takes an unsorted matrix of interaction data. 
	 * Creates unique labels for users and items (beginning at 1) without gaps.
	 * Sort both columns lexicographically.
	 * Evaluates index position for fast access.
	 * All fields are stored in the support data structure LFPLIFDataStruct.
	 * 
	 * Resulting fields:
	 * int [][] data : <user,item> - 1-indexed
	 * 
	 * @param source - interaction data <user, item>, e.g. first two columns of a CF rating matrix
	 * @return
	 */
	public static LFPLIFDataStruct prepareContextualIFData (int [][] source) {
		LFPLIFDataStruct ds = new LFPLIFDataStruct();
		// TODO
		return ds;
	}
	
	
}
