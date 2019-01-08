package edu.uiuc.viz.lattice;
/*
 * @author Himel
 */
import java.util.*;

public class Node 
{
    public String id; // Combined filter list with "#"
    //public ArrayList<String> filters; 
    public ArrayList<Integer> child_list; // List of node IDs of the children
    public ArrayList<Double> dist_list; // edge weights (distance) of this node to all the children
    Double population_size;
    
    public Double getPopulation_size() {
		return population_size;
	}
	public void setPopulation_size(Double population_size) {
		this.population_size = population_size;
	}
		
	public Node(String id)
    {
        this.id = id; 
        child_list = new ArrayList<>();
        dist_list = new ArrayList<>();
    }
	public void set_child_list(ArrayList<Integer> child_list)
    {
        this.child_list = new ArrayList<>(child_list);
    }
    public void set_dist_list(ArrayList<Double> dist_list)
    {
        this.dist_list = new ArrayList<>(dist_list);
    }
    public String get_id()
    {
        return id;
    }
    public ArrayList<Integer> get_child_list()
    {
        return child_list;
    }  
    public ArrayList<Double> get_dist_list()
    {
        return dist_list;
    } 
}
