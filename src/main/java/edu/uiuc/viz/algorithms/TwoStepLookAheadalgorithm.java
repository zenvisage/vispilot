package edu.uiuc.viz.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.lattice.Lattice;

public class TwoStepLookAheadalgorithm extends LookAheadPicking{

	private String heuristic;
	public static final String MAX_HEURISTIC = "max";
	public static final String SUM_HEURISTIC = "sum";
	public static final String BFS_HEURISTIC = "bfs";
	
	public TwoStepLookAheadalgorithm(String heuristic) {
		super("Two Step Look Ahead Algorithm (" + heuristic + ")");
		this.heuristic = heuristic;
	}

	@Override
	protected HashMap<Integer, Float> updateExternal(Experiment exp,ArrayList<Integer> localMaxSubgraph,
			HashMap<Integer, Float> currentFrontier, Integer parentNodeId, Integer k) 
	{

		currentFrontier.remove(parentNodeId);
		for(Integer childId : lattice.nodeList.get(parentNodeId).get_child_list())
		{	
			if(localMaxSubgraph.contains(childId)) continue;
			double edgeUtility = super.calculateDistance(parentNodeId, childId,exp);
			double maxUtility = 0;
			double sumUtility = 0;
			
			for(Integer grandChildId : lattice.nodeList.get(childId).get_child_list())
			{
				double currUtility = super.calculateDistance(childId, grandChildId, exp);
				if(localMaxSubgraph.contains(grandChildId)) continue;
				
				if(currUtility > maxUtility)
					maxUtility = currUtility;
				
				sumUtility += currUtility;
			}
			
			double utility = 0;
			
			if(heuristic.equals(MAX_HEURISTIC))
			{
				utility = maxUtility + edgeUtility;
			}
			else if(heuristic.equals(SUM_HEURISTIC))
			{
				utility = sumUtility + edgeUtility;
			}
			else
			{
				System.err.println("Illegal heuristic");
				System.exit(-1);
			}
			
			if(currentFrontier.containsKey(childId))
				currentFrontier.put(childId, (float) Math.max(currentFrontier.get(childId), utility));
			else
				currentFrontier.put(childId, (float) utility);
		}
		return currentFrontier;
	}
	
	public double getNodeUtility(Integer nodeId, ArrayList<Integer> subgraph)
	{
		double utility = 0f;
		for(Integer subgraphNodeId : subgraph)
		{
			for(Integer childId : lattice.nodeList.get(subgraphNodeId).get_child_list())
			{
				if(nodeId != childId) continue;
				double currUtility = super.calculateDistance(subgraphNodeId, nodeId, exp);
				if(currUtility > utility)
					utility = currUtility;
			}
		}
		return utility;
	}
}
