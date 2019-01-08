package edu.uiuc.viz.algorithms;
import java.util.ArrayList;
import java.util.HashMap;
import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.lattice.Lattice;


/**
 * In each iteration we pick a node with the highest utility from a set of "frontier" nodes.
 * Then, the children of the picked node are added to the frontier.
 * The utility of a frontier node is based on the edge weight with the parent
 * that "discovered" it.
 * 
 * @param k
 * @author saarkuzi
 */
public class BreadthFirstPicking extends LookAheadPicking{
	Experiment exp;
	public BreadthFirstPicking() {
		super("breadth_first_picking");
	}
	
	/**
	 * Adding nodes to a frontier; the added nodes are the children of 
	 * a given parent node.
	 * 
	 * @param currentFrontier, parentNodeId
	 */
	protected HashMap<Integer, Float> updateExternal(Experiment exp,ArrayList<Integer> localMaxSubgraph, HashMap<Integer, Float> currentFrontier, Integer parentNodeId, Integer k)
	{
		this.exp = exp;
		this.lattice = exp.lattice;
		currentFrontier.remove(parentNodeId);
		for(Integer childId : exp.lattice.nodeList.get(parentNodeId).get_child_list())
		{	
			if(localMaxSubgraph.contains(childId)) continue;
			double utility = super.calculateNormalizedDistance(parentNodeId, childId, exp);
			if(currentFrontier.containsKey(childId))
				currentFrontier.put(childId, (float) Math.max(currentFrontier.get(childId), utility));
			else
				currentFrontier.put(childId, (float) utility);
		}
		return currentFrontier;
	}

	 
}