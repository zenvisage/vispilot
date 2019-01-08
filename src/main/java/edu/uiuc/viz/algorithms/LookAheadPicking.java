package edu.uiuc.viz.algorithms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.lattice.Lattice;


/**
 * In each iteration we pick a node with the "highest utility" from a set of "frontier" nodes.
 * The "highest utility" should be determined by a specific instantiation
 * 
 * @param k
 * @author saarkuzi
 */
public abstract class LookAheadPicking extends Traversal{

	public LookAheadPicking(String algoName) 
	{
		super(algoName);
	}

	@Override
	public void pickVisualizations(Experiment exp) 
	{
		this.exp = exp;
		this.lattice=exp.lattice;
		super.printAlgoName();
		Integer rootId = exp.lattice.id2IDMap.get("#");
		HashMap<Integer, Float> localMaxSubgraphUtils = pickVisualizations(exp,exp.k, rootId);
		ArrayList<Integer> localMaxSubgraph = super.getKeysList(localMaxSubgraphUtils);
		
		// improve the current solution by doing local changes
		LocalGraphImprove lgi = new LocalGraphImprove(exp);
		localMaxSubgraph = lgi.improveSubgraphLocally(localMaxSubgraph);
		
		// updated the final solution
		for(int nodeId : localMaxSubgraph)
			exp.dashboard.maxSubgraph.add(nodeId);
		
		exp.dashboard.updateSubGraphUtility();
		exp.dashboard.printMaxSubgraphSummary();	
	}
	
	public HashMap<Integer, Float> pickVisualizations(Experiment exp,Integer k, Integer rootId)
	{
		//a map in which keys are node IDs, and values are utilities (=interestingness)
		ArrayList<Integer> localMaxSubgraph = new ArrayList<>();

		// first, we add the root
		localMaxSubgraph.add(rootId);
		HashMap<Integer,Float> externalNodesUtility = updateExternal(exp, localMaxSubgraph, new HashMap<>(), rootId, k);
		
		// In each iteration: choose node from frontier and then expand the frontier
		for(int i = 0 ; i < k - 1 ; i++)
		{
			if(externalNodesUtility.size() == 0) break;
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
	 * a given parent node. A specific algorithm needs to decide what is the utility of
	 * the frontier node
	 * 
	 * @param currentFrontier, parentNodeId
	 */
	protected abstract HashMap<Integer, Float> updateExternal(Experiment exp,ArrayList<Integer> localMaxSubgraph, HashMap<Integer, Float> currentFrontier, Integer parentNodeId, Integer k);
}
