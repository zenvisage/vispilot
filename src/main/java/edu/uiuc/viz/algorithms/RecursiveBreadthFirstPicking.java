package edu.uiuc.viz.algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.lattice.Lattice;

public class RecursiveBreadthFirstPicking extends LookAheadPicking
{
	private Integer numSteps;
	
	public RecursiveBreadthFirstPicking(Integer numSteps) {
		super("Recursive Breadth First Picking (N="+numSteps+")");
		this.numSteps = numSteps;
	}

	@Override
	protected HashMap<Integer, Float> updateExternal(Experiment exp,ArrayList<Integer> localMaxSubgraph,
			HashMap<Integer, Float> currentFrontier, Integer parentNodeId, Integer k) 
	{
		currentFrontier.remove(parentNodeId);
		for(Integer childId : lattice.nodeList.get(parentNodeId).get_child_list())
		{	
			if(localMaxSubgraph.contains(childId)) continue;
			double edgeUtility = super.calculateDistance(parentNodeId, childId, exp);
			BreadthFirstPicking bfp = new BreadthFirstPicking();
			HashMap<Integer, Float> lookAheadSubgraph = bfp.pickVisualizations(exp,Math.min(numSteps, k-localMaxSubgraph.size()), childId);
			double bfsUtility = Traversal.sumMapByValue(lookAheadSubgraph);
			double utility = edgeUtility + bfsUtility;
			
			if(currentFrontier.containsKey(childId))
				currentFrontier.put(childId, (float) Math.max(currentFrontier.get(childId), utility));
			else
				currentFrontier.put(childId, (float) utility);
		}
		return currentFrontier;
	}
}
