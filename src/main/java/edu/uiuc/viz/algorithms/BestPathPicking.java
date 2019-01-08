package edu.uiuc.viz.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BestPathPicking extends Traversal{
	
	// mapping from node id to the path that leads to this node
	public HashMap<Integer, ArrayList<Integer>> nodeToPath = new HashMap<>();
	// mapping from node id to the utility of the path that leads to it
	public HashMap<Integer, Float> nodeToPathUtility = new HashMap<>();
	// mapping from node id to its informative parents
	public HashMap<Integer, ArrayList<Integer>> nodeToParents = new HashMap<>();
	
	public BestPathPicking() {
		super("best path picking");
	}
	
	private void initializePaths(Experiment exp)
	{
		ArrayList<Integer> currentLevelNodes = new ArrayList<>();
		// initialize root node
		Integer rootId = exp.lattice.id2IDMap.get("#");
		currentLevelNodes.add(rootId);
		nodeToPath.put(rootId, new ArrayList<>());
		nodeToPathUtility.put(rootId, 0f);
		nodeToParents.put(rootId, new ArrayList<>());
		
		// iterate over each level of the lattice
		while(currentLevelNodes.size() > 0)
		{
			ArrayList<Integer> nextLevelNodes = new ArrayList<>();
			for(Integer parentId : currentLevelNodes)
			{				
				for(Integer childId : exp.lattice.nodeList.get(parentId).get_child_list())
				{
					// deal the case in which the child was just discovered
					if(!nodeToParents.containsKey(childId)) 
					{	
						nodeToParents.put(childId, new ArrayList<>());
						nextLevelNodes.add(childId);
					}
					
					nodeToParents.get(childId).add(parentId);
					ArrayList<Integer> currentPath = Traversal.cloneArray(nodeToPath.get(parentId));
					currentPath.add(parentId);
					
					float numNodes = currentPath.size(); // put max with 1 to handle the root
					if(numNodes + 1 > exp.k) continue; //we don't want to add too long paths
					
					double distance = super.calculateNormalizedDistance(parentId, childId, exp); //check
					Float utility = (float) ((nodeToPathUtility.get(parentId) * (numNodes - 1) / numNodes)); //check
					utility += (float) (distance / numNodes);
	
					if(utility > nodeToPathUtility.getOrDefault(childId, -1f))
					{
						nodeToPath.put(childId, currentPath);
						nodeToPathUtility.put(childId, utility);	
					}
				}
			}
			currentLevelNodes = nextLevelNodes;
		}
	}
	
	private void updatePaths(ArrayList<Integer> addedPath, Integer numOfNodes, Experiment exp) {
		
		// remove nodes that were added to the subgraph
		for(Integer pathNode : addedPath)
		{
			nodeToPathUtility.remove(pathNode);
		}
		
		// update the utility for the remaining candidate nodes
		for(Integer otherNode : nodeToPathUtility.keySet())
		{
			int numNodes = nodeToPath.get(otherNode).size();
			Boolean changedPathFlag = false;
			Integer lastParent = 0;
			ArrayList<Integer> nodesToRemove = new ArrayList<>();
			
			// check if one of its path nodes were added (and update the path)
			for(int i = 0; i < numNodes; i++)
			{
				Integer parentId = nodeToPath.get(otherNode).get(numNodes - i - 1);
				if(addedPath.contains(parentId))
				{
					nodesToRemove.add(parentId);
					if(!changedPathFlag)
					{
						lastParent = parentId;
						changedPathFlag = true;
					}
				}
			}
			
			// recalculate the utility in case the path was changed
			if(changedPathFlag)
			{
				nodeToPath.get(otherNode).removeAll(nodesToRemove);
				ArrayList<Integer> currentPath = new ArrayList<>();
				currentPath.add(lastParent);
				currentPath.addAll(nodeToPath.get(otherNode));
				currentPath.add(otherNode);
				double newUtility = 0;
				int i = 0;
				for( ; i < currentPath.size() - 1 ; i++)
				{
					Integer parentId = currentPath.get(i); 
					Integer childId = currentPath.get(i+1); 
					newUtility += super.calculateNormalizedDistance(parentId, childId, exp); //check
				}
				newUtility /= (double) i;
				nodeToPathUtility.put(otherNode, (float)newUtility);
			}
		}
		
		// remove nodes with path that exceed the required length
		ArrayList<Integer> farNodes = new ArrayList<>();
		for(Integer nodeId : nodeToPathUtility.keySet())
		{
			Integer numPathNodes = nodeToPath.get(nodeId).size();
			if(numPathNodes > exp.k - numOfNodes)
				farNodes.add(nodeId);
		}
		for(Integer farNode : farNodes)
		{
			nodeToPathUtility.remove(farNode);
			nodeToPath.remove(farNode);
		}
	}
	
	@Override
	public void pickVisualizations(Experiment exp) {
		initializePaths(exp);
				
		ArrayList<Integer> subgraphNodes = new ArrayList<>();
		Integer rootId = exp.lattice.id2IDMap.get("#");
		nodeToPath.remove(rootId);
		
		while(subgraphNodes.size() < exp.k && nodeToPath.size() > 0)
		{
			Integer maximalNode = Traversal.getKeyWithMaxVal(nodeToPathUtility);
			ArrayList<Integer> addedPath = nodeToPath.get(maximalNode);
			addedPath.add(maximalNode);
			for(Integer pathNode : addedPath)
			{
				subgraphNodes.add(pathNode);
			}
			Integer numOfNodes = subgraphNodes.size();
			updatePaths(addedPath, numOfNodes, exp);
		}
		// updated the final solution
		for(int nodeId : subgraphNodes)
			exp.dashboard.maxSubgraph.add(nodeId);
		
		exp.dashboard.updateSubGraphUtility();
		exp.dashboard.printMaxSubgraphSummary();
	}

	public static void main(String[] args)
	{
	}
}
