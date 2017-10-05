package disc.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

public class MyIoUtils {
	public static void writeDoubleArray(String filename, double [][] data) {
		System.out.println("Writing double array to "+filename);
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename);
			for (int i = 0; i<data.length; i++){
				for(int j = 0; j<data[i].length; j++){
					writer.print(data[i][j]);
					if (j<data[i].length-1) {
						writer.print(",");
					}	
				}
				writer.println();
			}	
			writer.close();
		} catch (FileNotFoundException e){
		System.out.println("Error: " + e.getMessage());	
		}
	}
	
	public static void writeSingleArray(String filename, double [] data) {
		System.out.println("Writing double array to "+filename);
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename);
			for (int i = 0; i<data.length; i++){
				
					writer.print(data[i]);
					/*if (j<data[i].length-1) {
						writer.print(",");
					}*/	
				
				writer.println();
			}	
			writer.close();
		} catch (FileNotFoundException e){
		System.out.println("Error: " + e.getMessage());	
		}
	}
	
	/**
	 * Reads the number of features or "attributes" of a file in XXL format.
	 * Those are characterized by having the "A" flag in the header. 
	 * @param filename
	 * @return
	 */
	public static int getNumberOfXXLFeatures(String filename) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			br.close();
			String [] parts = line.split("A");
			return parts.length-1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return 0;
	}
	// todo: add method that evaluates how many labels are within an xxl file
}
