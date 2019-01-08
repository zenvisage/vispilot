package edu.uiuc.viz.lattice;

import java.util.ArrayList;
import java.util.HashMap;

public class Dashboard {
	public ArrayList<Integer> maxSubgraph;
	public double maxSubgraphUtility;
	public Lattice lattice;
	public Dashboard(Lattice lattice) {
		this.maxSubgraph =new ArrayList<Integer>();
		this.maxSubgraphUtility=0;
		this.lattice = lattice;
	}
	public void printMaxSubgraphSummary() {
		// Summary of maximum subgraph 
		System.out.print("Max Subgraph: [");
		for (int j =0 ; j< maxSubgraph.size();j++) {
			if (j==maxSubgraph.size()-1) {
				System.out.print(Integer.toString(maxSubgraph.get(j))+"]\n");
			}else {
				System.out.print(Integer.toString(maxSubgraph.get(j))+",");
			}
		}
		
		
		System.out.print("[");
		for (int j =0 ; j<  maxSubgraph.size();j++) {
			Node n = lattice.nodeList.get(maxSubgraph.get(j));
			ArrayList<Double> dist = lattice.id2MetricMap.get(n.get_id());
			/*
			// Printing Value of Each Node
			if (j==maxSubgraph.size()-1) {
				System.out.print(lattice.nodeList.get(maxSubgraph.get(j)).get_id()+ dist + "]\n");
			}else {
				System.out.print(lattice.nodeList.get(maxSubgraph.get(j)).get_id()+ dist +",");
			}*/
			if (j==maxSubgraph.size()-1) {
				System.out.print(lattice.nodeList.get(maxSubgraph.get(j)).get_id()+"]\n");
			}else {
				System.out.print(lattice.nodeList.get(maxSubgraph.get(j)).get_id()+",");
			}
		}
		updateSubGraphUtility();
		System.out.println("Total Utility:"+Double.toString(maxSubgraphUtility));
	}
	/**
	 * 
	 * Provides a unified way for computing utility of a subgraph
	 * to be used in the different algorithms. Each node might have several
	 * informative parents - in such case we add the maximal interestingness to 
	 * the overall utility.
	 * 
	 */
	public void updateSubGraphUtility() 
	{
		maxSubgraphUtility =  computeSubGraphUtility(maxSubgraph);
	}
	public double computeSubGraphUtility() {
	    ArrayList<Integer> subgraph = maxSubgraph;
		double maxSubgraphUtility = 0;
		HashMap<Integer,Float> nodeID2utility = new HashMap<>();
		for(int nodeId : subgraph)
			nodeID2utility.put(nodeId, 0f);
		
		for(int i : nodeID2utility.keySet())
		{	
			Node currentNode = lattice.nodeList.get(i);
			for(int j = 0; j < currentNode.child_list.size(); j++)
			{
				int childId = currentNode.child_list.get(j);
				if(nodeID2utility.containsKey(childId))
				{
					double edgeWeight = currentNode.dist_list.get(j);
					Float currentUtility = nodeID2utility.get(childId); 
					nodeID2utility.put(childId, (float) Math.max(currentUtility, edgeWeight));
				}
			}
		}
		
		for(int nodeId : nodeID2utility.keySet())
			maxSubgraphUtility += nodeID2utility.get(nodeId);
		return maxSubgraphUtility;
	}
	public double computeSubGraphUtility(ArrayList<Integer> subgraph) {
		double maxSubgraphUtility = 0;
		HashMap<Integer,Float> nodeID2utility = new HashMap<>();
		for(int nodeId : subgraph)
			nodeID2utility.put(nodeId, 0f);
		
		for(int i : nodeID2utility.keySet())
		{	
			Node currentNode = lattice.nodeList.get(i);
			for(int j = 0; j < currentNode.child_list.size(); j++)
			{
				int childId = currentNode.child_list.get(j);
				if(nodeID2utility.containsKey(childId))
				{
					double edgeWeight = currentNode.dist_list.get(j);
					Float currentUtility = nodeID2utility.get(childId); 
					nodeID2utility.put(childId, (float) Math.max(currentUtility, edgeWeight));
				}
			}
		}
		
		for(int nodeId : nodeID2utility.keySet())
			maxSubgraphUtility += nodeID2utility.get(nodeId);
		return maxSubgraphUtility;
	}
}
