package getuserdata;

import java.util.Comparator;

public class MassFeatureComparator implements Comparator<MassFeature> {

	public int compare(MassFeature o1, MassFeature o2) {
		// TODO Auto-generated method stub
		if(Math.abs(o1.getStatistic())>Math.abs(o2.getStatistic()))
			return 1;
		else
			return -1;
	}
}
