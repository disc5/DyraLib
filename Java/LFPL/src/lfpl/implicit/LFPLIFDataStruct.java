package lfpl.implicit;

public class LFPLIFDataStruct {
	public int [][] data;
	public int [] user_starts;
	public int [] user_sizes;
	public int nUsers;
	public int nItems;
	
	/* teData differs from data in that there is only one single interaction between a row and an item */
	public int [][] teData; 
}
