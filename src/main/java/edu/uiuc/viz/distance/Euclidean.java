package edu.uiuc.viz.distance;
import java.util.ArrayList;

public class Euclidean implements Distance{
	String distName = "euclidean";
	public Euclidean() {
	}
	public String getDistName() {
		return distName;
	}
	public void setDistName(String distName) {
		this.distName = distName;
	}
	@Override
	public double computeDistance(ArrayList<Double> viz1, ArrayList<Double> viz2)
    {
        double distance = 0;
        for(int i=0; i < viz1.size() && i < viz2.size(); i++)
        {
            distance += (viz1.get(i)-viz2.get(i))*(viz1.get(i)-viz2.get(i));
        }
        return Math.sqrt(distance);
    }
	public double computeNormalizedDistance(ArrayList<Double> viz1, ArrayList<Double> viz2, Double parentSize, Double childSize) {
		return (double) childSize/(double) parentSize * computeDistance(viz1, viz2);
	}
}
