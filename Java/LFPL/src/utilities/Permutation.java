package utilities;

/**
 * 
 * @author schaefer
 *
 */
public class Permutation {
	/**
	 * Converts an ordering into a ranking and vice versa.
	 * Assuming that orderings (and rankings) use numbers 1..M.
	 * Can deal with incomplete orderings/rankings. It assumes that missing entries are encoded by -1.
	 * @param ordering
	 */
	public static double [] switchOrderingRanking(double [] ordering){
		double [] ranking = new double[ordering.length];

		// Init
		for (int i=0;i<ordering.length;i++) ranking[i]=-1;
		for (int i=0;i<ordering.length;i++) {
			// System.out.println("i="+i+" ordering[i]= "+ordering[i]);
			int obj_at_i = (int)ordering[i]; // items in the ordering a labeled from 1 to M
			if (obj_at_i !=-1) {
				ranking[obj_at_i-1] = i+1;
			}
		}
		return ranking;
	}
}
