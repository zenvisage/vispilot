package edu.uiuc.viz.algorithms;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.lattice.Dashboard;
import edu.uiuc.viz.lattice.Hierarchia;
import edu.uiuc.viz.lattice.Lattice;
import edu.uiuc.viz.lattice.Node;

/**
 * Exhaustive Picking Baseline
 * 
 * @param k
 * @author dorislee
 */
public class ExhaustivePicking extends Traversal{
	static int numCompletedGraph=0;
	public ExhaustivePicking() {
		super("Exhaustive Picking");
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
		
		this.exp = exp;
		this.lattice = exp.lattice;
		super.printAlgoName();
		
		Dashboard dashboard = new Dashboard(lattice);
		
		//a map in which keys are node IDs, and values are utilities (interestingness)
		HashMap<Integer,Float> localMaxSubgraph = new HashMap<>();

		// first, we add the root
		Integer rootId = lattice.id2IDMap.get("#");
		if(rootId == null)
		{
			System.err.println("Lattice root cannot be found in the nodes list");
			return;
		}
		localMaxSubgraph.put(rootId, 0f);
		ArrayList<Integer> rootSubgraph = new ArrayList<Integer>(Arrays.asList(rootId));
		pickChildren(exp.k, rootSubgraph, lattice.nodeList.get(rootId));
		exp.dashboard.printMaxSubgraphSummary();
	}
	
	/*
	 * Implementing a recursive algorithm that picks all possible combinations of children
	 * @ param
	 * k: maximal number of nodes picked 
	 * G:  currently picked subgraph
	 * pivot : node of interest that we are picking the children from
	 */
	public void pickChildren(Integer k, ArrayList<Integer> G, Node pivot) {
		System.out.println("pivot="+pivot.id);
		System.out.println("pivot="+lattice.nodeList.indexOf(pivot));
		int n = G.size();
		ArrayList<Integer> pivot_children = pivot.get_child_list();
		int m = Math.min(k-n, pivot_children.size());
//		int m= k-n;
		for (int i =1; i<=m; i++) {
			System.out.println("i="+i);
			ArrayList<ArrayList<Integer>> child_combo_list = combination(pivot_children,i);
			//System.out.println("child_combo_list:"+child_combo_list);
			for (ArrayList<Integer> child_combo : child_combo_list) {
				//System.out.println("child_combo:"+child_combo);
				//System.out.println("G:"+G);
				ArrayList<Integer> newG =  (ArrayList<Integer>) Stream.concat(G.stream(), child_combo.stream())
                        						.collect(Collectors.toList());
				double totalUtility =0;
				if (newG.size()==k) {
					numCompletedGraph+=1;
					totalUtility=exp.dashboard.computeSubGraphUtility(newG);
					System.out.println("newG:"+newG+":"+totalUtility);
					for (int j=0; j<newG.size();j++) {
						System.out.print(lattice.nodeList.get(newG.get(j)).id);
					}
					System.out.println("\n");
					
					/*if (newG.get(1)==14) {
						System.out.println("newG:"+newG+":"+totalUtility);
					}*/
					if (totalUtility>exp.dashboard.maxSubgraphUtility) {
						//System.out.println("newG:"+newG);
						//System.out.println("totalUtility:"+totalUtility);
						exp.dashboard.maxSubgraph = newG;
						exp.dashboard.maxSubgraphUtility = totalUtility;
						//VizOutput.dumpGenerateNodeDicFromNoHierarchia(i, lattice, newG);
					}
				}
				//System.out.println("newG:"+newG);
				//System.out.println("newG.size():"+newG.size());
				if (newG.size()<k) {
					for (int childID: child_combo) {
						int childIdx = pivot.get_child_list().indexOf(childID);
						Node childNode  = lattice.nodeList.get(childID);
						pickChildren(k,newG, childNode);
					}
				}
			}
		}
	}
    static void combinationUtil(ArrayList<ArrayList<Integer>> all_combo,ArrayList<Integer> arr, ArrayList<Integer> data, int start,
                                int end, int index, int r)
    {
        if (index == r)
        {
        		ArrayList<Integer> combo = new ArrayList<Integer>();
            for (int j=0; j<r; j++)
            		combo.add(data.get(j));
            all_combo.add(combo);
            return;
        }

        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data.set(index,arr.get(i)) ;
            combinationUtil(all_combo, arr, data, i+1, end, index+1, r);
        }
    }
 
	/* Create all possible combination of the children nodes of size r
	 * @param 
	 * r: number of children to pick in the combination
	 */
    static ArrayList<ArrayList<Integer>> combination(ArrayList<Integer> arr,   int r)
    {
    		ArrayList<ArrayList<Integer>> all_combo = new ArrayList<ArrayList<Integer>>(); 
        // A temporary array to store all combination one by one
    		ArrayList<Integer> data = (ArrayList<Integer>) arr.clone();
        combinationUtil(all_combo,arr, data, 0, arr.size()-1, 0, r);
        // Print all combination
        /*
        for (int i =0;i<all_combo.size();i++) {
        		System.out.println(all_combo.get(i));
        }
        */
        return all_combo;
    }
}