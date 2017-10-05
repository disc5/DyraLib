package disc.utilities.bilinpl.tests;

import disc.utilities.MyUtils;
import disc.utilities.bilinpl.BilinPLUtils;


public class PLSamplingTest {

	public static void main(String[] args) {
		double [] skills = new double[]{2.0, 5.2, 1.1, 8.2};
		int [] ordering = BilinPLUtils.getPLSampleFromVase(skills);
		//MyUtils.printDoubleArray(ordering);
		MyUtils.printIntArray(ordering);
		// OK
	}

}
