package edu.uiuc.viz.distance;

import java.util.ArrayList;

public class EarthMover implements Distance{
	private double emd;
	String distName = "emd";
	public String getDistName() {
		return distName;
	}
	public void setDistName(String distName) {
		this.distName = distName;
	}
	public EarthMover() {
		emd = 0;
	}
	@Override
	public double computeDistance(ArrayList<Double> viz1, ArrayList<Double> viz2)
    {
		// The 1-D implementation of EMD is simply the cumulative sum of how much dirt 
		// needs to be moved from viz2's pile (child) to viz1 (parent).
		assert viz1.size()==viz2.size();
		for (int i=0; i<viz1.size();i++) {
			emd+=viz2.get(i)-viz1.get(i);
		}
        return emd;
    }
	@Override
	public double computeNormalizedDistance(ArrayList<Double> viz1, ArrayList<Double> viz2, Double parentSize, Double childSize) {
		return (double) childSize/(double) parentSize * computeDistance(viz1, viz2);
	}
}
