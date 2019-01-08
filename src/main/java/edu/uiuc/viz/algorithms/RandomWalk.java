package edu.uiuc.viz.algorithms;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.lattice.Hierarchia;
import edu.uiuc.viz.lattice.Lattice;
import edu.uiuc.viz.lattice.Node;

/**
 * Randomly picking children of the current nodes in dashboard (if not already present)
 * Stop until reach k nodes in dashboard
 */
public class RandomWalk extends Traversal{
	
	public RandomWalk() {
		super("random_walk");
	}
	
	public void pickVisualizations(Experiment exp) {
	   this.exp = exp;
	   this.lattice = exp.lattice;
	   System.out.println("---------------- Random Walk -----------------");
	   ArrayList<Integer> rwResult = randomWalk(lattice,exp.k);
	   exp.dashboard.maxSubgraph= rwResult; 
	   exp.dashboard.maxSubgraphUtility=exp.dashboard.computeSubGraphUtility(rwResult);
	   exp.dashboard.printMaxSubgraphSummary();
   }
	
	public static ArrayList<Integer> randomWalk(Lattice lattice,Integer k) {
	       ArrayList<Integer> dashboard = new ArrayList<Integer>();
	       dashboard.add(0); // Adding root
	       // Stop when dashboard exceeds desired size k 
	       while(dashboard.size()<k && dashboard.size() < lattice.nodeList.size())
	       {	
	    	   	   ArrayList<Integer> currentFrontier = RandomWalk.getFrontier(lattice,dashboard);
	           Random r = new Random(System.currentTimeMillis());
	           int myRandomNumber = 0;
	           if (currentFrontier.size()>0) {
	        	   	   myRandomNumber = r.nextInt(currentFrontier.size());
		           dashboard.add(currentFrontier.get(myRandomNumber));
	           }else {
	        	   	   break;
	           }
	       }
	       return dashboard;
	}
	public static ArrayList<Integer> getFrontier(Lattice lattice,ArrayList<Integer> dashboard) {
		ArrayList<Integer> currentFrontier = new ArrayList<Integer>();
        //System.out.println("Dashboard Size: "+dashboard.size());
        int next = -1;
        for(int i = 0; i < dashboard.size(); i++)
        {
             //System.out.println("Children of: "+lattice.nodeList.get(dashboard.get(i)).get_id());
     	       // Looping through all children indexes 
            int flag = 0;
            Integer currentNodeID = dashboard.get(i);
            Node currentNode = lattice.nodeList.get(currentNodeID);
            for(int j = 0; j < currentNode.child_list.size(); j++)
            { 
                //System.out.println(j+"th Child: "+ lattice.nodeList.get(currentNode.get_child_list().get(j)).id);
                for(int sp = 0; sp < dashboard.size(); sp++)
                {
                    // Check if the node to be added is already in the dashboard 
                    if(lattice.nodeList.get(dashboard.get(i)).child_list.get(j).equals(dashboard.get(sp)))
                    {
                        //System.out.println("Already in");
                        flag =1;
                        break;
                    }
                }
                if(flag == 0)
                {
                    next = lattice.nodeList.get(dashboard.get(i)).child_list.get(j);
                    currentFrontier.add(next);
                }
            }
        }
		return currentFrontier;
	} 
}
