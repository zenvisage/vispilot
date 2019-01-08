package edu.uiuc.viz.distance;

import java.util.ArrayList;

public class MaxDiff implements Distance{
	private double mdiff;
	String distName = "maxdiff";
	public MaxDiff() {
		mdiff = 0;
	}
	public String getDistName() {
		return distName;
	}
	public void setDistName(String distName) {
		this.distName = distName;
	}
	@Override
	public double computeDistance(ArrayList<Double> viz1, ArrayList<Double> viz2) {
		assert viz1.size()==viz2.size();
		for (int i =0; i<viz1.size();i++) {
			double diff = Math.abs(viz1.get(i)-viz2.get(i));
			if (diff >mdiff){
				mdiff = diff;
			}
		}
		return mdiff;
	}
	public double computeNormalizedDistance(ArrayList<Double> viz1, ArrayList<Double> viz2, Double parentSize, Double childSize) {
		return (double) childSize/(double) parentSize * computeDistance(viz1, viz2);
	}
}
