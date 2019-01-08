package edu.uiuc.viz.algorithms;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.lattice.Hierarchia;
import edu.uiuc.viz.lattice.Lattice;

/**
 * Randomly picking children of the current nodes in dashboard (if not already present)
 * Stop until reach k nodes in dashboard
 */
public class MultipleRandomWalk extends Traversal{
	int maxCount;
	public MultipleRandomWalk(int maxCount) {
		super("Multiple Random Walk("+maxCount+" iter)");
		this.maxCount=maxCount;
	}
	
	public void pickVisualizations(Experiment exp) {
	   this.exp = exp;
	   this.lattice = exp.lattice;
	   exp.dashboard.maxSubgraphUtility=0; // reset maxSubgraphUtility when picking
	   System.out.println("---------------- Multiple Random Walk -----------------");
	   int count =0;
	   
	   while (count < maxCount) {
		   ArrayList<Integer> rwResult = RandomWalk.randomWalk(lattice,exp.k);
	       double total_utility=exp.dashboard.computeSubGraphUtility(rwResult);
	       if (total_utility>exp.dashboard.maxSubgraphUtility){
	    	   		exp.dashboard.maxSubgraph= rwResult; 
	    	   		exp.dashboard.maxSubgraphUtility=total_utility;
	       }
	       count+=1;
	   }
	   exp.dashboard.printMaxSubgraphSummary();
   }
}
