package edu.uiuc.viz.distance;

import java.util.ArrayList;

public class KLDivergence implements Distance {
	private double kldiv;
	String distName = "kldiv";
	public KLDivergence() {
		kldiv = 0;
	}
	public String getDistName() {
		return distName;
	}
	public void setDistName(String distName) {
		this.distName = distName;
	}
	@Override
	public double computeDistance(ArrayList<Double> viz1, ArrayList<Double> viz2) {
		// Computing the KL divergence from Q to reference P
		assert viz1.size()==viz2.size();
		for (int i=0; i<viz1.size();i++) {
			if (viz1.get(i)!=0 & viz2.get(i)!=0) {
				kldiv+=viz1.get(i)*(Math.log(viz1.get(i)/viz2.get(i))/Math.log(2));
			}
		}
		return kldiv;
	}
	public double computeNormalizedDistance(ArrayList<Double> viz1, ArrayList<Double> viz2, Double parentSize, Double childSize) {
		return (double) childSize/(double) parentSize * computeDistance(viz1, viz2);
	}
}