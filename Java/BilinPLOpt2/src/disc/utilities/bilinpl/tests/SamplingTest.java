package disc.utilities.bilinpl.tests;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import disc.utilities.MyUtils;

public class SamplingTest {

	public static void main(String[] args) {
		int [] ids = new int[]{10,20,40,100};
		double [] probabilities = new double []{0.7,0.1,0.05,0.05};
		EnumeratedIntegerDistribution sampler = new EnumeratedIntegerDistribution(ids,probabilities);
		//int sample = sampler.sample();
		//System.out.println("Ct Sample: "+sample);
		MyUtils.printIntArray(sampler.sample(100));
		// OK
	}

}
