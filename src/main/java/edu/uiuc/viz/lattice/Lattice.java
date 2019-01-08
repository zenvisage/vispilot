package edu.uiuc.viz.lattice;
import java.util.*;

public class Lattice 
{
	/*
	 * id: string made up of filters 
	 */
	public ArrayList<Node> nodeList;
	
	public HashMap<String, ArrayList<Double>>  id2MetricMap;
	public HashMap<ArrayList<Double>, String>  Metric2idMap;
	
	public HashMap<String, Integer> id2IDMap;
	public HashMap<Integer, String> ID2idMap;

	public Lattice(HashMap<String, ArrayList<Double>> id2MetricMap,  
			ArrayList<Node> nodeList, HashMap<String, Integer> id2IDMap) 
	{
		this.id2MetricMap = id2MetricMap;
		this.nodeList = nodeList;
		this.id2IDMap = id2IDMap;
		
		this.ID2idMap = new HashMap<Integer, String>();
		for (Map.Entry<String, Integer> entry : id2IDMap.entrySet()) 
		{
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    ID2idMap.put(value, key);
		}
		
		this.Metric2idMap = new HashMap<ArrayList<Double>, String>();
		for (Map.Entry<String, ArrayList<Double>> entry : id2MetricMap.entrySet()) 
		{
			String key = entry.getKey();
			ArrayList<Double> value = entry.getValue();
			Metric2idMap.put(value, key);
		}
	}
	
	public Lattice() 
	{
		this.id2MetricMap= new HashMap<String, ArrayList<Double>>();
		this.nodeList = new ArrayList<Node>();       
		this.id2IDMap = new HashMap<String, Integer>();
		
		this.ID2idMap = new HashMap<Integer, String>();
		this.Metric2idMap = new HashMap<ArrayList<Double>, String>();
	}
	
	public void add2Lattice(Node node, ArrayList<Double> measure_values, Integer ID) 
	{
		nodeList.add(node);
		id2MetricMap.put(node.id, measure_values);
        id2IDMap.put(node.id, ID);
        
        ID2idMap.put(ID, node.id);
        Metric2idMap.put(measure_values, node.id);
	}
}
