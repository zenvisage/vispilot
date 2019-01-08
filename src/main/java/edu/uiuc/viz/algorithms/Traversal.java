package edu.uiuc.viz.algorithms;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.lattice.Hierarchia;
import edu.uiuc.viz.lattice.Lattice;
import edu.uiuc.viz.lattice.Node;

/**
 * Traversal algorithms take in a HashMap representing the materialized graph 
 * and return a presumably maximal subgraph (list of node indices) 
 */
public abstract class Traversal {

	Experiment exp;
	Lattice lattice;
	String algoName;
	public Traversal(String algoName) 
	{
		this.algoName = algoName;
	}
	
	public String getAlgoName() {
		return algoName;
	}

	public void setAlgoName(String algoName) {
		this.algoName = algoName;
	}
	
	public void printAlgoName()
	{
		System.out.println("---------------- " + algoName +  " -----------------");
	}
	
	public static double[] ArrayList2Array(ArrayList<Double> arrList) {
		 double[] target = new double[arrList.size()];
		 for (int i = 0; i < target.length; i++) {
		    target[i] = arrList.get(i).doubleValue();  
		 }
		return target;
	}
	
	public abstract void pickVisualizations(Experiment exp);
	
	
	/**
	 * Calculate interestingness score between parent and child nodes
	 */
	public static double calculateDistance(int nodeId1, int nodeId2, Experiment exp)
	{
		Node node1 = exp.lattice.nodeList.get(nodeId1);
		Node node2 = exp.lattice.nodeList.get(nodeId2);
		double utility = exp.dist.computeDistance(exp.lattice.id2MetricMap.get(node1.get_id()), exp.lattice.id2MetricMap.get(node2.get_id()));
		return utility;
	}
	
	public static double calculateNormalizedDistance(int nodeId1, int nodeId2, Experiment exp)
	{
		Node node1 = exp.lattice.nodeList.get(nodeId1);
		Node node2 = exp.lattice.nodeList.get(nodeId2);
		double utility = exp.dist.computeNormalizedDistance(exp.lattice.id2MetricMap.get(node1.get_id()), exp.lattice.id2MetricMap.get(node2.get_id()),node1.getPopulation_size(),node2.getPopulation_size());
		return utility;
	}
	
	public static Float sumMapByValue(HashMap<Integer,Float> map)
	{
		Float sum = 0f;
		for(Float val : map.values())
		{
			sum += val;
		}
		return sum;
	}
	
	public static HashMap<Integer,Float> cloneMap(HashMap<Integer,Float> inputMap)
	{
		HashMap<Integer,Float> outputMap = new HashMap<>();
		for(Map.Entry<Integer, Float> entry : inputMap.entrySet())
		{
			outputMap.put(entry.getKey(), entry.getValue());
		}
		return outputMap;
	}
	
	public static ArrayList<Integer> getKeysList(HashMap<Integer, Float> inputMap)
	{
		ArrayList<Integer> outputList = new ArrayList<>();
		for(Integer key : inputMap.keySet())
		{
			outputList.add(key);
		}
		return outputList;
	}
	
	public static <T,S extends Comparable<S>> T getKeyWithMaxVal(HashMap<T,S> map)
	{
		HashMap.Entry<T, S> maxEntry = null;

		for (HashMap.Entry<T, S> entry : map.entrySet())
		{
		    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
		    {
		        maxEntry = entry;
		    }
		}
		return maxEntry.getKey();
	}
	
	public static ArrayList<Integer> cloneArray(ArrayList<Integer> input)
	{
		ArrayList<Integer> output = new ArrayList<>();
		for(Integer val : input)
			output.add(val);
		return output;
	}
	
 	public static void main(String[] args) throws SQLException 
    {
    }
}
