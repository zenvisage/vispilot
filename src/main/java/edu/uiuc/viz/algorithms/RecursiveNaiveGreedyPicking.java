package edu.uiuc.viz.algorithms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.lattice.Hierarchia;
import edu.uiuc.viz.lattice.Lattice;

public class RecursiveNaiveGreedyPicking extends LookAheadPicking{

	private Integer numSteps;
	
	public RecursiveNaiveGreedyPicking(Integer numSteps) {
		super("Recursive Naive Greedy Picking (N="+numSteps+")");
		this.numSteps = numSteps;
	}

	@Override
	protected HashMap<Integer, Float> updateExternal(Experiment exp, ArrayList<Integer> localMaxSubgraph,
			HashMap<Integer, Float> currentFrontier, Integer parentNodeId, Integer k) 
	{
		currentFrontier.remove(parentNodeId);
		for(Integer childId : lattice.nodeList.get(parentNodeId).get_child_list())
		{	
			if(localMaxSubgraph.contains(childId)) continue;
			double edgeUtility = super.calculateDistance(parentNodeId, childId, exp);
			double greedyUtility = findMaximalPath(childId, Math.min(numSteps, k-localMaxSubgraph.size()));
			double utility = edgeUtility + greedyUtility;
			
			if(currentFrontier.containsKey(childId))
				currentFrontier.put(childId, (float) Math.max(currentFrontier.get(childId), utility));
			else
				currentFrontier.put(childId, (float) utility);
		}
		return currentFrontier;
	}
	
	private double findMaximalPath(Integer nodeId, Integer pathLength)
	{
		HashMap<Integer, Float> subgraph = new HashMap<>();
		subgraph.put(nodeId, 0f);
		int parentId = nodeId;
		int maxId = -1;
		
		for(int i = 0 ; i < pathLength-1 ; i++)
		{
			double maxUtility = -1;
			for (Integer childId : lattice.nodeList.get(parentId).get_child_list())
			{
				double utility = super.calculateDistance(parentId, childId, exp);
				//System.out.println("parent:" + parentId + ", child:" + childId + " " + utility);

				if(utility > maxUtility)
				{
					maxUtility = utility;
					maxId = childId;
				}
			}
			if(maxUtility == -1) break;
			
			subgraph.put(maxId, (float) maxUtility);
			parentId = maxId;
		}
		//System.out.println(subgraph);
		return super.sumMapByValue(subgraph);
	}

	public static void main(String[] args) throws SQLException
	{
		/*
		   String[] datasets = {"turn", "titanic", "mushroom"};
		   String[] xAxis = {"has_list_fn", "pc_class", "type"};
		   int dataset_id = 0;
		   int k = 20;
		   
		   Euclidean ed = new Euclidean();
		   Hierarchia h = new Hierarchia(datasets[dataset_id], xAxis[dataset_id]);
		   Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed,0.001,0.8);
	       //Hierarchia.print_map(lattice.id2MetricMap);
	       //Hierarchia.print_map(lattice.id2IDMap);
		   
	       Traversal tr; 
	       tr = new RecursiveNaiveGreedyPicking(lattice, new Euclidean(), 2);
	       ((RecursiveNaiveGreedyPicking) tr).findMaximalPath(0, 5);
	     */
	}
}
