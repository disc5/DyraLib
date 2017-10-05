package lfpl.implicit;

import java.io.FileNotFoundException;
import java.io.IOException;

import lfpl.implicit.io.LFPL_ImplicitFeedback_IO;
import utilities.MyIoUtils;
import utilities.MyUtils;

public class IOTest {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		LFPLIFDataStruct ds = LFPL_ImplicitFeedback_IO.importMatlabInteractionFile("data/toy.mat");
		MyUtils.printPrimitiveIntMatrix(ds.data);
		//MyIoUtils.writeIntArray("D:/temp2/test1.csv", ds.data);
		LFPL_ImplicitFeedback_IO.exportInteraction2CSV("D:/temp2/lauf1",  ds);
		// Okay. Hinweis: erstellt nur den LETZTEN fehlenden unterordner automatisch.
	}

}
