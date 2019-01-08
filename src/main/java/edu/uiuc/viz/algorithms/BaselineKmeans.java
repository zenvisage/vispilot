package edu.uiuc.viz.algorithms;
import java.util.ArrayList;
import java.util.Map;

import edu.uiuc.viz.lattice.Lattice;
import net.sf.javaml.clustering.*;
import net.sf.javaml.core.*; 
import net.sf.javaml.distance.*;
import edu.uiuc.viz.algorithms.KMeans;

/**
 * XYZ
 * ABC
 */
public class BaselineKmeans extends Traversal
{
	public BaselineKmeans() 
	{
		super("kmeans");
	}
	
	public void pickVisualizations(Experiment exp) 
	{
		this.exp = exp;
		this.lattice = exp.lattice;
		System.out.println("---------------- Kmeans Clustering -----------------");
		ArrayList<Integer> rwResult = kmeans(lattice,exp.k);
		exp.dashboard.maxSubgraph= rwResult;
		exp.dashboard.maxSubgraphUtility=exp.dashboard.computeSubGraphUtility(rwResult);
		exp.dashboard.printMaxSubgraphSummary();
	}
	
	public static ArrayList<Integer> kmeans(Lattice lattice, Integer k) 
	{
		ArrayList<Integer> dashboard = new ArrayList<Integer>();
		int it = 0;
		Dataset data = new DefaultDataset();
		
		while(it < lattice.nodeList.size())
		{
			//System.out.println(lattice.ID2idMap.get(it));
			ArrayList<Double> value_list = lattice.id2MetricMap.get(lattice.ID2idMap.get(it));
			//System.out.println(value_list);
			double[] values = new double[value_list.size()];
			
			for (int sd = 0; sd < values.length; sd++)
			{
				values[sd] = value_list.get(sd);
			}
			Instance instance = new DenseInstance(values);
			data.add(instance);
			it++;
		}
		/*
		 * Cluster the data, it will be returned as an array of data sets, with
		 * each dataset representing a cluster
		 */
		
		Clusterer km = new KMeans(k);
		
		Dataset[] clusters = km.cluster(data);
		int[] ids = new int[k];
		int[] mins = new int[k];
		for(it = 0; it < k; it++)
		{
			mins[it] = 1000000;
		}
		
		for(it = 0; it <clusters.length; it++)
		{
			for(int ip = 0; ip < clusters[it].size(); ip++)
			{
				Instance x = clusters[it].instance(ip);
				for (Map.Entry<ArrayList<Double>, String> entry: lattice.Metric2idMap.entrySet())
				{
					ArrayList<Double> key = entry.getKey();
					String value = entry.getValue();
					double[] target = new double[key.size()];
					for (int sd = 0; sd < target.length; sd++) 
					{
					    target[sd] = key.get(sd);
					}
					Instance y = new DenseInstance(target);
					EuclideanDistance euclid = new EuclideanDistance();
					double dist = euclid.calculateDistance(x, y);
					int count = value.length() - value.replace("#", "").length();
					if(dist < 0.0001 && count < mins[it])
					{
						ids[it] = lattice.id2IDMap.get(value);
						mins[it] = count;
						//System.out.println(it+":"+ids[it]+","+mins[it]+","+value);
						break;
					}
					
				}
			}
		}
//		for (int i=0;i<k;i++) {
//			System.out.println("mins:"+mins[i]+","+ids[i]);
//		}
		
		for(int i = 0; i < k; i++)
		{
			dashboard.add(ids[i]);
		}
		/*
		for(int i = 0; i < dashboard.size(); i++)
		{
			System.out.print(dashboard.get(i)+ " ");
		}
		*/
		
		return dashboard;
		
		
	       
	      
	}
}
