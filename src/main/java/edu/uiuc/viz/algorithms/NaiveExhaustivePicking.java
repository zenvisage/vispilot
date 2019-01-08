package edu.uiuc.viz.algorithms;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.StatUtils;

import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.lattice.Hierarchia;
import edu.uiuc.viz.lattice.Lattice;
import edu.uiuc.viz.lattice.Node;

/**
 * Naive Exhaustive Picking Baseline
 * 
 * @param k
 * @author dorislee
 */
public class NaiveExhaustivePicking extends Traversal {
	public NaiveExhaustivePicking() {
		super("Naive Exhaustive Picking");
	}

	/**
	 * 
	 * Implementation of the traversal algorithm for generating a subgraph with
	 * maximal utility of k nodes
	 * 
	 * @param k
	 */
	public void pickVisualizations(Experiment exp) {
		boolean DEBUG = false;
		super.printAlgoName();
		this.exp = exp;
		this.lattice = exp.lattice;
		exp.dashboard.maxSubgraph.clear();
		exp.dashboard.maxSubgraphUtility = 0;

		// a map in which keys are node IDs, and values are utilities (interestingness)
		HashMap<Integer, Float> localMaxSubgraph = new HashMap<>();

		// first, we add the root
		Integer rootId = lattice.id2IDMap.get("#");
		if (rootId == null) {
			System.err.println("Lattice root cannot be found in the nodes list");
			return;
		}
		localMaxSubgraph.put(rootId, 0f);
		ArrayList<Integer> rootSubgraph = new ArrayList<Integer>(Arrays.asList(rootId));
		// Compute all possible k-node combo of nodelist
		if (DEBUG) System.out.println("nodelist size:" + lattice.nodeList.size());
		ArrayList<ArrayList<Node>> all_combo=combination(lattice.nodeList, exp.k);
		if (DEBUG) System.out.println("all_combo size:"+all_combo.size());
		// Hashmap storing subgraph list of nodeIDs with values as total utility
		HashMap<ArrayList<Integer>,Double> hmap = new HashMap<ArrayList<Integer>,Double>();
		for (ArrayList<Node> combo : all_combo) {
			ArrayList<Integer> levels =new ArrayList<Integer>();
			boolean connected = false;
			for (Node node:combo) {
				if (DEBUG) System.out.print(node.id+",");
				// Determine level by number of # in the attribute name
				int level = node.id.length() - node.id.replace("#", "").length() - 1;
				levels.add(level);
				//System.out.println(level);
			}
			if (DEBUG) System.out.println("");
			if (DEBUG) System.out.println(levels);
			// Must contain root
			connected = levels.contains(0);
			if (connected) {
				// test missing levels
				connected = testMissingLevel(levels);
				if (connected) {
					// check connectedness of chosen-node combination
					connected = checkConnectedness(combo,levels);
					if (DEBUG) System.out.println(combo);
					if (DEBUG) System.out.println("checkConnectedness:"+connected);
				}
			}
			if (DEBUG) System.out.println(connected);
			// If passed all connected checks, then compute subgraph utility
			if (connected) {
				// convert combo from ArrayList<Node> to ArrayList<Integer>
				ArrayList<Integer> subgraph =new ArrayList<Integer>();
				for (Node node:combo) {
					if (DEBUG) System.out.println(lattice.id2IDMap.get(node.id));
					subgraph.add(lattice.id2IDMap.get(node.id));
				}
				double total_utility = exp.dashboard.computeSubGraphUtility(subgraph);
				hmap.put(subgraph,total_utility);
			}
		}
		/*
		Set set = hmap.entrySet();
	    Iterator iterator = set.iterator();
	    while(iterator.hasNext()) {
	       Map.Entry mentry = (Map.Entry)iterator.next();
	       System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
	       System.out.println(mentry.getValue());
	    }*/
	    
	    // Find entry with the max value 
	    Map.Entry<ArrayList<Integer>,Double> maxEntry = null;
	    for (Map.Entry<ArrayList<Integer>,Double> entry : hmap.entrySet())
	    {
	        if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
	        {
	            maxEntry = entry;
	        }
	    }
	    if (DEBUG) System.out.println("max:"+maxEntry);
	    exp.dashboard.maxSubgraph = maxEntry.getKey();
	    exp.dashboard.maxSubgraphUtility = maxEntry.getValue();
	    exp.dashboard.printMaxSubgraphSummary();
	}
	
	private boolean checkConnectedness(ArrayList<Node> combo,ArrayList<Integer> levels) {
		boolean DEBUG = false;
		if (DEBUG)  System.out.println("--------checkConnectedness---------");
		Collections.reverse(combo);
		Collections.reverse(levels);
//		System.out.println("all nodes in combo:"+combo);
		boolean connectedCombo = false;
		boolean connectedNode = false;
		for (int i=0;i<combo.size()-1;i++) { //Not checking root's parents
			Node node = combo.get(i);
			int level = levels.get(i);
			if (DEBUG)  System.out.println("checking node:"+node.id);
			// Since we know that the list is in the form of ascending, consecutive, possibly repeating integers 
			// find all the index of the nodes in the level belong
			// see if this node is a children of any of these upper-level nodes
			ArrayList<Integer> index = findAllIndexOf(levels,level-1);
			connectedNode = false;
			for (int idx :index) { // Looping through upper-level nodes
				if (DEBUG)  System.out.println("upper level node combo.get(idx):"+combo.get(idx).id);
				if (connectedNode) {
					connectedCombo=true;
					break; // We do not need to inspect additional parents if one already satisfies
				}
				ArrayList<Integer> children= combo.get(idx).get_child_list();
				if (DEBUG) System.out.print("children:"+children+"-->");
				for (int cidx:children) {
					if (DEBUG) System.out.print(lattice.nodeList.get(cidx).id+",");
					if (lattice.nodeList.get(cidx).id.equals(node.id)){
						if (DEBUG) System.out.println("\nnid:"+node.id);
						if (DEBUG) System.out.println("stop early since matched");
						connectedNode=true;
						break;
					}
				}
				if (DEBUG) System.out.println("");
			}
			if (connectedNode==false) {
				return false;// There is no point of checking if one node is disconnected
			}else {
				connectedCombo=true;
			}
		}
		if (DEBUG) {
			System.out.println("connectedNode:"+connectedNode);
			System.out.println("connectedCombo:"+connectedCombo);
		}
		if (connectedCombo) {
			return true;// There is no point of checking if one node is disconnected
		}else {
			return false;
		}
	}

	private ArrayList<Integer> findAllIndexOf(ArrayList<Integer> arr, int element) {
		// Find all index of an element in the array, print as a list.
		ArrayList<Integer> matchedIdx = new ArrayList<Integer>();
		for (int i=0; i<arr.size() ; i++) {
			if (arr.get(i).equals(element)) {
				matchedIdx.add(i);
			}
		}
		return matchedIdx;
	}

	private boolean testMissingLevel(ArrayList<Integer> levels) {
		// Assuming levels is sorted
		for (int i = 0; i < levels.size() - 1; i++) { 
			if (levels.get(i) != levels.get(i+1)) { // Avoid case where repeating consecutive vals
				if (levels.get(i)+ 1 != levels.get(i+1)) { // if next value is one larger than prev
					return false;
				}
			}
		}
		return true;
	}

	static void combinationUtil(ArrayList<ArrayList<Node>> all_combo, ArrayList<Node> arr, ArrayList<Node> data,
			int start, int end, int index, int r) {
		if (index == r) {
			ArrayList<Node> combo = new ArrayList<Node>();
			for (int j = 0; j < r; j++)
				combo.add(data.get(j));
			all_combo.add(combo);
			return;
		}

		for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
			data.set(index, arr.get(i));
			combinationUtil(all_combo, arr, data, i + 1, end, index + 1, r);
		}
	}

	/*
	 * Create all possible combination of the children nodes of size r
	 * 
	 * @param r: number of children to pick in the combination
	 */
	static ArrayList<ArrayList<Node>> combination(ArrayList<Node> arr, int r) {
		ArrayList<ArrayList<Node>> all_combo = new ArrayList<ArrayList<Node>>();
		// A temporary array to store all combination one by one
		ArrayList<Node> data = (ArrayList<Node>) arr.clone();
		combinationUtil(all_combo, arr, data, 0, arr.size() - 1, 0, r);
		// Print all combination
		/*
		for (int i = 0; i < all_combo.size(); i++) {
//			System.out.println(all_combo.get(i));
			System.out.print("[");
			for (int j=0; j<all_combo.get(i).size();j++) {
				System.out.print(all_combo.get(i).get(j).id+",");
			}
			System.out.println("]");
		}*/
		return all_combo;
	}

	public static void main(String[] args)  {

/*		ArrayList<Integer> pivot_children = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
		int r = 3;
		combination(pivot_children, r);*/

//		Euclidean ed = new Euclidean();
//		Hierarchia h = new Hierarchia("mushroom","cap_surface");
//		// Hierarchia h = new Hierarchia("turn","has_list_fn");
//		//Hierarchia h = new Hierarchia("titanic", "survived");
//		Lattice lattice = Hierarchia.generateFullyMaterializedLattice(ed, 0.001, 0.8);
//		
//		 Traversal tr;
//		 tr = new NaiveExhaustivePicking(lattice,new Euclidean());
//		 tr.pickVisualizations(5);
//
//		 tr = new GreedyPicking(lattice,new Euclidean());
//		 tr.pickVisualizations(5);
//		
//		 tr = new BreadthFirstPicking(lattice,new Euclidean());
//		 tr.pickVisualizations(5);

		 
	}
}