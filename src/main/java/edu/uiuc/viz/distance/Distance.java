package edu.uiuc.viz.distance;

import java.util.ArrayList;

public interface Distance {
	public String getDistName();
	public void setDistName(String distName);
	//	Let viz1 be the parent and viz2 be the child 
	// (Order matters for certain metrics such as KL divergence and EMD)
	public double computeDistance(ArrayList<Double> viz1, ArrayList<Double> viz2);
	public double computeNormalizedDistance(ArrayList<Double> viz1, ArrayList<Double> viz2, Double parentSize, Double childSize);
}
