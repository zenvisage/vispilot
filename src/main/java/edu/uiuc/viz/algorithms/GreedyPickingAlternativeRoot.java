package edu.uiuc.viz.algorithms;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.lattice.Lattice;

/**
 * Looping through all children of current node (over all node in nodeList)
 * Greedily adding in nodes with larger utility than current max 
 * Stop until reach k nodes in dashboard
 */
public class GreedyPickingAlternativeRoot extends Traversal{
	String alternativeRoot;
	ArrayList<Integer> existingDashboard;
	public GreedyPickingAlternativeRoot(ArrayList<Integer> existingDashboard, String alternativeRoot) {
		super("greedy_picking_alternative_root");
		this.alternativeRoot=alternativeRoot;
		this.existingDashboard=existingDashboard;
	}
	
	public void pickVisualizations(Experiment exp) {
	   this.exp = exp;
	   this.lattice=exp.lattice;
	   System.out.println("---------------- Greedy Picking -----------------");
       double total_utility = 0;
       ArrayList<Integer> dashboard = new ArrayList<Integer>();
       int correspondingID = exp.lattice.id2IDMap.get(alternativeRoot);
       System.out.println(correspondingID);
       dashboard.add(correspondingID); 
       // Stop when dashboard exceeds desired size k 
       while(dashboard.size()<exp.k && dashboard.size() < lattice.nodeList.size())
       {	
       	   double max_utility = 0;
           //System.out.println("Dashboard Size: "+dashboard.size());
           int next = -1;
           for(int i = 0; i < dashboard.size(); i++)
           {
               //System.out.println("Children of: "+node_list.get(dashboard.get(i)).get_id());
        	       // Looping through all children indexes 
               for(int j = 0; j < lattice.nodeList.get(dashboard.get(i)).get_dist_list().size(); j++)
               {
            	   	   
                   int flag = 0;
                   //System.out.println("Current Node: "+node_list.get(dashboard.get(i)).get_child_list().get(j));
                   for(int sp = 0; sp < dashboard.size(); sp++)
                   {
                       // Check if the node to be added is already in the dashboard 
                       if(lattice.nodeList.get(dashboard.get(i)).get_child_list().get(j).equals(dashboard.get(sp)))
                       {
                           //System.out.println("Already in");
                           flag =1;
                           break;
                       }
                   }
                   if(flag == 0 && lattice.nodeList.get(dashboard.get(i)).get_dist_list().get(j) > max_utility)
                   {
                       max_utility = lattice.nodeList.get(dashboard.get(i)).get_dist_list().get(j);
                       next = lattice.nodeList.get(dashboard.get(i)).get_child_list().get(j);
                   }
               }
           }
           dashboard.add(next);
           total_utility+=max_utility;
       }
       System.out.println(dashboard);
       dashboard.remove((Integer) correspondingID);
       //dashboard.remove(correspondingID);// Do not add alternative root since it is already included in the original dashboard
       //Combining this with existing dashboard
       for (int existingID : existingDashboard) {
    	   		dashboard.add(existingID);
       }
       exp.dashboard.maxSubgraph= dashboard; 
       exp.dashboard.maxSubgraphUtility=total_utility;
       exp.dashboard.printMaxSubgraphSummary();
   }
	
}
