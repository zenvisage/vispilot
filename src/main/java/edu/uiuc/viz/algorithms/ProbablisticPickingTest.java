package edu.uiuc.viz.algorithms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

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
public class ProbablisticPickingTest extends LookAheadPicking{
	Experiment exp;
	public ProbablisticPickingTest() {
		super("Probablistic Picking");
	}
	
	@Override
	public HashMap<Integer, Float> pickVisualizations(Experiment exp,Integer k, Integer rootId)
	{
		Random r = new Random(System.currentTimeMillis());
		System.out.println("new pick Viz");
		//a map in which keys are node IDs, and values are utilities (=interestingness)
		ArrayList<Integer> localMaxSubgraph = new ArrayList<>();

		// first, we add the root
		localMaxSubgraph.add(rootId);
		HashMap<Integer,Float> externalNodesUtility = updateExternal(exp, localMaxSubgraph, new HashMap<>(), rootId, k);
		
		// In each iteration: choose node from frontier and then expand the frontier
		for(int i = 0 ; i < k - 1 ; i++)
		{
			if(externalNodesUtility.size() == 0) break;
			Iterator it = externalNodesUtility.entrySet().iterator();
			float rollSum =0;
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
		        rollSum += (float) pair.getValue();
		    }
		    System.out.println("rollSum:"+rollSum);
		    
		    float myRandomNumber = r.nextFloat();
		    System.out.println(myRandomNumber);
		    float normVal =0;
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
		        normVal = (float) pair.getValue()/rollSum;
		    }
			Integer selectedNodeID = Collections.max(externalNodesUtility.entrySet(), Map.Entry.comparingByValue()).getKey();
			localMaxSubgraph.add(selectedNodeID);
			externalNodesUtility = updateExternal(exp,localMaxSubgraph, externalNodesUtility, selectedNodeID, k);
		}	
		
		LocalGraphImprove lgi = new LocalGraphImprove(exp);
		HashMap<Integer, Float> subgraphWithUtilities = lgi.getSubgraphWithUtilities(localMaxSubgraph);
		
		return subgraphWithUtilities;
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