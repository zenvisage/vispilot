package edu.uiuc.viz.algorithms;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.lattice.Database;
import edu.uiuc.viz.lattice.Hierarchia;
import edu.uiuc.viz.lattice.Lattice;
import edu.uiuc.viz.lattice.Node;
import edu.uiuc.viz.lattice.PairIntFloat;
/*
 * Probabilistic Picking algorithm
 */
public class MultipleProbablisticPicking extends Traversal{

	public MultipleProbablisticPicking() {
		super("Probabalistic Greedy Picking");
	}

	/**
	 * 
	 * Implementation of the traversal algorithm for generating a subgraph
	 * with maximal utility of k nodes
	 * 
	 * @param k
	 */
	public void pickVisualizations(Experiment exp)
	{
		super.printAlgoName();
		this.exp = exp;
		this.lattice = exp.lattice;
		exp.dashboard.maxSubgraph.clear();
		exp.dashboard.maxSubgraphUtility = 0;
	    
		//a map in which keys are node IDs, and values are utilities (interestingness)
		
		HashMap<Integer,Float> bestLocalMaxSubgraph = new HashMap<>();
		float max_total_utility = 0;

		// first, we add the root
		Integer rootId = lattice.id2IDMap.get("#");
		if(rootId == null)
		{
			System.err.println("Lattice root cannot be found in the nodes list");
			return;
		}
		HashMap<Integer,Float> frontierNodesUtility = null;
		for(int sp = 0; sp < 100; sp++)
		{
			HashMap<Integer,Float> localMaxSubgraph = new HashMap<>();
			localMaxSubgraph.put(rootId, 0f);
			frontierNodesUtility = expandFrontier(new HashMap<>(), rootId);
			
			// In each iteration: choose node from frontier and then expand the frontier
			for(int i = 0 ; i < exp.k - 1 ; i++)
			{
				if(frontierNodesUtility.size() == 0) break;
	//			Integer selectedNodeID = Collections.max(frontierNodesUtility.entrySet(), Map.Entry.comparingByValue()).getKey();
				Integer selectedNodeID = probablisticPickFromFrontier(frontierNodesUtility);
				//System.out.println(selectedNodeID+" "+ frontierNodesUtility.get(selectedNodeID));
				localMaxSubgraph.put(selectedNodeID, frontierNodesUtility.get(selectedNodeID));
				localMaxSubgraph = updateUtilities(localMaxSubgraph, selectedNodeID);
				frontierNodesUtility = expandFrontier(frontierNodesUtility, selectedNodeID);
			}
			
	//		for(Map.Entry<Integer, Float> entry : localMaxSubgraph.entrySet())
	//		{
	//			System.out.println(lattice.nodeList.get(entry.getKey()).get_id() + ":" + entry.getValue());
	//		}
	//		System.out.println("=================================================");
	//		for(Map.Entry<Integer, Float> entry : frontierNodesUtility.entrySet())
	//		{
	//			System.out.println(lattice.nodeList.get(entry.getKey()).get_id() + ":" + entry.getValue());
	//		}
			
			//System.out.println(frontierNodesUtility);
			
			//localMaxSubgraph = permuteLattice(localMaxSubgraph, frontierNodesUtility);
			System.out.println(localMaxSubgraph);
			float total_utility = 0;
			for (Float value : localMaxSubgraph.values()) 
			{
				//System.out.println("V "+value);
				total_utility += value;
			}
			System.out.println(total_utility);	
			System.out.println("M "+max_total_utility);	
			if(total_utility > max_total_utility)
			{
				bestLocalMaxSubgraph = localMaxSubgraph;
				max_total_utility = total_utility;
			}
		}
		bestLocalMaxSubgraph = permuteLattice(bestLocalMaxSubgraph, frontierNodesUtility);
		for(int nodeId : bestLocalMaxSubgraph.keySet())
		{
			exp.dashboard.maxSubgraph.add(nodeId);
			exp.dashboard.maxSubgraphUtility += bestLocalMaxSubgraph.get(nodeId);
		}
		exp.dashboard.printMaxSubgraphSummary();
	}
	
	public Integer probablisticPickFromFrontier(HashMap <Integer,Float> frontier) {
		Iterator it = frontier.entrySet().iterator();
		ArrayList<PairIntFloat> frontierList = new ArrayList<PairIntFloat>();
		Float sum =0f;
		int i=0;
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        Float x = logistic(0.0001,(Float) pair.getValue());
	        sum += x;
	        frontierList.add(new PairIntFloat((Integer) pair.getKey(),x));
	        //System.out.println(frontierList.get(i).getX()+","+frontierList.get(i).getY());
	        i++;
	    }
	    //System.out.println("SUM: "+sum);
	    for (int j=0;j<frontierList.size();j++) {
	    		frontierList.get(j).setY(frontierList.get(j).getY()/sum);
	    }
	    Float rollSum =0f;
	    for (int k=0; k<frontierList.size();k++) {
	    		rollSum+=frontierList.get(k).getY();
	    		frontierList.get(k).setY(rollSum);
	    		//System.out.println(rollSum);
	    }
	    //System.out.println("DONE");

	    Random rand = new Random();
	    Float floatThres = rand.nextFloat();
	    if (floatThres>frontierList.get(frontierList.size()-1).getY()) {
	    		floatThres=frontierList.get(frontierList.size()-1).getY()-0.000001f;
	    }
	    
	    int picked = -1;
	    
	    if(floatThres >= 0 && floatThres < frontierList.get(0).getY())
	    {
	    		picked = frontierList.get(0).getX();
	    	
	    }
	    for(int j = 1; j<frontierList.size(); j++)
	    {
	    		if(floatThres >= frontierList.get(j-1).getY() &&
	    				floatThres < frontierList.get(j).getY())
	    		{
	    			picked = frontierList.get(j).getX();
	    			break;
	    		}
	    }
	    //System.out.println("Threshold: "+floatThres);
	    //System.out.println(picked);
	    return picked;
	}
	public Float logistic (double a, Float x) {
		return (float) (1./(1+Math.exp(-a*x)));
		//return (float) Math.exp(a*x);
	}
	/**
	 * 
	 * When adding a new node to the subgraph, we should check if it already has children
	 * there (possible because node can have several informative parents).
	 * If it is the case, then the utility of the node needs to be updated by using the most 
	 * informative parent.
	 * 
	 * @param currentMaxSubgraph, nodeId
	 */
	private HashMap<Integer,Float> updateUtilities(HashMap<Integer,Float> currentMaxSubgraph, int nodeId)
	{
		Node currentNode = lattice.nodeList.get(nodeId);
		for(int childId : currentNode.child_list)
		{
			if(!currentMaxSubgraph.containsKey(childId)) continue;
			Double newUtility = Traversal.calculateNormalizedDistance(nodeId, childId, exp);
			Float currentUtility = currentMaxSubgraph.get(childId);
			currentMaxSubgraph.put(childId, (float) Math.max(currentUtility, newUtility));
		}	
		return currentMaxSubgraph;
	}
	
	/**
	 * Adding nodes to a frontier; the added nodes are the children of 
	 * a given parent node.
	 * 
	 * @param currentFrontier, parentNodeId
	 */
	private HashMap<Integer, Float> expandFrontier(HashMap<Integer, Float> currentFrontier, Integer parentNodeId)
	{
		currentFrontier.remove(parentNodeId);
		for(Integer childId : lattice.nodeList.get(parentNodeId).get_child_list())
		{	
			if(exp.dashboard.maxSubgraph.contains(childId)) continue;
			double utility = Traversal.calculateDistance(parentNodeId, childId, exp);
			if(currentFrontier.containsKey(childId))
				currentFrontier.put(childId, (float) Math.max(currentFrontier.get(childId), utility));
			else
				currentFrontier.put(childId, (float) utility);
		}
		return currentFrontier;
	}

	/**
	 * Replacing one node in the subgraph with one node from the frontier
	 * 
	 * @param currentSubgraph, frontierNodesUtility
	 */
	private HashMap<Integer,Float> permuteLattice(HashMap<Integer,Float> currentSubgraph, HashMap<Integer,Float> frontierNodes)
	{
		Float maximalUtility = super.sumMapByValue(currentSubgraph);
		int chosenFrontierNodeId = -1;
		for(int frontierNodeId : frontierNodes.keySet())
		{
			
			HashMap<Integer,Float> tempMaxSubgraph = swapSingleNode(frontierNodeId, frontierNodes.get(frontierNodeId), currentSubgraph);
			Float tempMaxUtility = super.sumMapByValue(tempMaxSubgraph);
			if( tempMaxUtility > maximalUtility)
			{
				maximalUtility = tempMaxUtility;
				currentSubgraph = tempMaxSubgraph;
				chosenFrontierNodeId = frontierNodeId;
			}
		}
		
		if(chosenFrontierNodeId > 0)
			currentSubgraph = updateUtilities(currentSubgraph, chosenFrontierNodeId);
		
		return currentSubgraph;
	}
	
	/**
	 * Adding a candidate node to a subgraph at the expense of removing another
	 * 
	 * @param candidateId, candidateUtility, maxSubgraph
	 */
	private HashMap<Integer, Float> swapSingleNode(int candidateId, Float candidateUtility, HashMap<Integer,Float> maxSubgraph)
	{
		Float maxUtility = super.sumMapByValue(maxSubgraph);
		HashMap<Integer,Float> newMaxSubgraph = super.cloneMap(maxSubgraph);

		for(int nodeId : maxSubgraph.keySet())
		{
			HashMap<Integer,Float> tempSubgraph = replace2Nodes(nodeId, candidateId, candidateUtility, maxSubgraph);
			Float newUtility = super.sumMapByValue(tempSubgraph);
			if(newUtility > maxUtility)
			{
				maxUtility = newUtility;
				newMaxSubgraph = tempSubgraph;
			}
		}
		return newMaxSubgraph;
	}
	
	/**
	 * Adding one node, and deleting another. If it is not possible then the original 
	 * subgraph is returned.
	 * 
	 * @param outNodeId, inNodeId, candidateUtility, maxSubgraph
	 */
	private HashMap<Integer,Float> replace2Nodes(int outNodeId, int inNodeId, Float candidateUtility, HashMap<Integer,Float> originalMaxSubgraph)
	{
		HashMap<Integer,Float> newMaxSubgraph = super.cloneMap(originalMaxSubgraph);
		newMaxSubgraph.put(inNodeId, candidateUtility);
		newMaxSubgraph.remove(outNodeId);
		
		// first, if the node has no children we can safely remove it
		ArrayList<Integer> childrenInSubgraph = new ArrayList<>();
		for(int childId : lattice.nodeList.get(outNodeId).child_list)
		{
			if(newMaxSubgraph.containsKey(childId))
				childrenInSubgraph.add(childId);
		}
		if(childrenInSubgraph.size() == 0) 
			return newMaxSubgraph;
		
		// if the node has children, then maybe these children have several parents;
		// so it will still be okay to remove the node
		for(int childId : childrenInSubgraph)
		{
			HashMap<Integer,Float> otherParents = new HashMap<>();
			for(int possibleParentId : newMaxSubgraph.keySet())
			{
				Node possibleParentNode = lattice.nodeList.get(possibleParentId);
				if(possibleParentNode.child_list.contains(childId))
				{
					double dist = Traversal.calculateDistance(possibleParentId, childId, exp);
					otherParents.put(possibleParentId, (float) dist);
				}
			}
			if(otherParents.isEmpty())
				return originalMaxSubgraph;
			
			Float newUtility = Collections.max(otherParents.entrySet(), Map.Entry.comparingByValue()).getValue();
			newMaxSubgraph.remove(childId);
			newMaxSubgraph.put(childId, newUtility);
		}
		return newMaxSubgraph;
	}
}