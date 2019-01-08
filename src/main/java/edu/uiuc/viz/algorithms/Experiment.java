package edu.uiuc.viz.algorithms;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.lattice.Dashboard;
import edu.uiuc.viz.lattice.Database;
import edu.uiuc.viz.lattice.Hierarchia;
import edu.uiuc.viz.lattice.Lattice;
import edu.uiuc.viz.lattice.VizOutput;

public class Experiment {
	static String datasetName;
	public String xAxisName;
	public String yAxisName;
	int k;
	public Distance dist ;
	double iceberg_ratio;// [ic] % of root population size to keep as a node
	double informative_critera; //[ip] % closeness to minDist to be regarded as informative parent
	public Lattice lattice;
	Traversal algo;
	String fname;
	int nbars;
	public Dashboard dashboard;
	public ArrayList<String> groupby;
	public String aggFunc;
	public static String experiment_name="../ipynb/dashboards/json/"+"vary_dataset_ip";
	
	public static Database db ;
	public static ArrayList<String> attribute_names;
	public static HashMap<String, ArrayList<String>> uniqueAttributeKeyVals;

	public Experiment(String datasetName, String xAxisName, String yAxisName, ArrayList<String> groupby, String aggFunc, int k, Distance dist,
			double iceberg_ratio, double informative_critera,boolean online) throws SQLException, FileNotFoundException, UnsupportedEncodingException {
		db = new Database();
		this.datasetName = datasetName;
		this.xAxisName = xAxisName;
		this.yAxisName = yAxisName;
		this.groupby = groupby;
		this.aggFunc = aggFunc.toUpperCase();
		this.k = k;
		this.dist = dist;
		this.iceberg_ratio = iceberg_ratio;
		this.informative_critera = informative_critera;
		this.attribute_names = get_attribute_names();//read the csv file to get attribute names
//		this.attribute_names = Database.resultSet2ArrayStr(Database.getColumns(this.datasetName));
//		this.attribute_names.remove("id");
		this.uniqueAttributeKeyVals = populateUniqueAttributeKeyVals();
		// Generate base table via group-by
		ResultSet rs = Database.viz_query(this.datasetName, this.groupby, this.yAxisName, this.aggFunc, new ArrayList<String>(Arrays.asList()));
		Database.resultSet2csv(rs,this.datasetName,this.groupby,this.aggFunc+"("+this.yAxisName+")"); //generate csv file
		if (online) {
			this.lattice = new Lattice();
		}else {
			//generate the lattice for this experiment
			this.lattice = Hierarchia.generateFullyMaterializedLattice(dist,iceberg_ratio,
							informative_critera,uniqueAttributeKeyVals,attribute_names,xAxisName,datasetName);
			//get the number of filters
			this.nbars = lattice.id2MetricMap.get("#").size();
		}
	}
	public void setK(int k) {
		this.k = k;
	}
	public void setAlgo(Traversal algo) {
		this.algo = algo;
		dashboard = new Dashboard(lattice);
		if (experiment_name!="") {
			File directory = new File(experiment_name);
		    if (! directory.exists()){
		        directory.mkdir();
		    }
			this.fname = experiment_name+"/"+datasetName+"_"+xAxisName.replace("_","-")+"_"+algo.algoName+"_"+dist.getDistName()+"_ic"+iceberg_ratio+"_ip"+informative_critera+"_k"+k+".json";
		}else {
			this.fname = datasetName+"_"+xAxisName.replace("_","-")+"_"+algo.algoName+"_"+dist.getDistName()+"_ic"+iceberg_ratio+"_ip"+informative_critera+"_k"+k+".json";
		}
	}
	public static HashMap<String, ArrayList<String>> populateUniqueAttributeKeyVals() throws SQLException{
		HashMap<String, ArrayList<String>> uniqueAttributeKeyVals = new HashMap<String, ArrayList<String>>();
		for (int i=0;i<attribute_names.size();i++) {
			String key = attribute_names.get(i);
			ArrayList<String> attrVals = 
					Database.resultSet2ArrayStr(Database.findDistinctAttrVal(key, datasetName));
			uniqueAttributeKeyVals.put(key, attrVals);			
		}
		return uniqueAttributeKeyVals;
	}
	static ArrayList<String> get_attribute_names()
    {
        ArrayList<String> attribute_names = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(datasetName+".csv"));
            String line = null;
            if((line = reader.readLine()) != null) 
            {
                String [] names = line.split(",");
                //System.out.println(Arrays.toString(names));
                for(int i = 0; i < names.length-1; i++)
                {
                    attribute_names.add(names[i]);
                }
            }
        }
        catch(IOException e)
        {
            System.out.println("Error in get_attribute_names()");
            System.out.println("attribute_names:"+attribute_names);
        }
        return attribute_names;
    }
	public void runOutput(Experiment exp) throws SQLException {
		algo.pickVisualizations(exp);
		VizOutput vo = new VizOutput(exp);
        String nodeDic = vo.generateNodeDic();
        VizOutput.dumpString2File(fname, nodeDic);
        db.c.close();
	}
	
	public String runOutputReturnJSON(Experiment exp) throws SQLException {
		algo.pickVisualizations(exp);
		VizOutput vo = new VizOutput(exp);
        String nodeDic = vo.generateNodeDic();
        db.c.close();
        return nodeDic;   
	}
	
	public void runTableLayoutOutput(Experiment exp) throws SQLException {
		algo.pickVisualizations(exp);
		VizOutput vo = new VizOutput(exp);
        String nodeDic = vo.generateOrderedNodeDic();
        VizOutput.dumpString2File(fname, nodeDic);
        db.c.close();
	}
	
	public long timedRunOutput(Experiment exp) throws SQLException {
		long startTime = System.nanoTime();
		algo.pickVisualizations(exp);
		long endTime = System.nanoTime();
		VizOutput vo = new VizOutput(exp);
        String nodeDic = vo.generateNodeDic();
        VizOutput.dumpString2File(fname, nodeDic);
        long duration = (endTime - startTime);
        db.c.close();
        return duration;
	}
	public static ArrayList<String> pickNRandom(ArrayList<String> lst, int n) {
		LinkedList<String> copy = new LinkedList<String>(lst);
	    Collections.shuffle(copy);
	    return new ArrayList<String>(copy.subList(0, n));
	}
	public static ArrayList<Double> computeVisualization(Experiment exp,String filterStr) throws SQLException {
		System.out.println("computeVisualization for:"+filterStr);
		String[] items;
		ArrayList<String> split_filters = new ArrayList<String>();
		int hashCount = filterStr.length() - filterStr.replace("#", "").length();

		if (hashCount>=1) {
			if (filterStr.charAt(0)=='#') {
				items = filterStr.substring(1).replace("$","=").split("#");
			}else {
				items = filterStr.replace("$","=").split("#");
			}
			split_filters = new ArrayList<String>(Arrays.asList(items));
		}else {
			split_filters.add(filterStr.replace("$","=")); 
		}
	    System.out.println("split_filters"+split_filters);
	    return Database.computeViz(exp.datasetName, exp.xAxisName,exp.groupby, exp.yAxisName, exp.aggFunc, split_filters);
	}
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
	{
		
		 Experiment exp;
		 int k =5;
		 /*
		 //Debugging Exhaustive
		 ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("has_list_sum_range_fn","has_corr_list_fn","has_prof_clicks_tbl","is_profile_query","has_impressions_tbl","has_prof_engagement_events_tbl"));
		 //ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("has_list_sum_range_fn","has_corr_list_fn","has_prof_clicks_tbl","has_est_distinct","has_list_sum_fn","has_impressions_tbl","is_profile_query","has_prof_engagement_events_tbl"));
		 exp = new Experiment("turn", "has_prof_clicks_tbl","hdfs_bytes_written",groupby,"SUM", k, "frontierGreedy", new Euclidean(),0,0.8,false);
		 long duration = exp.timedRunOutput();
		 System.out.println("Duration:"+duration);
		 exp.algo.printMaxSubgraphSummary();
		 exp = new Experiment("turn", "has_prof_clicks_tbl", "hdfs_bytes_written",groupby,"SUM", k, "greedy", new Euclidean(),0,0.8,false);
		 duration = exp.timedRunOutput();
		 System.out.println("Duration:"+duration);
		 exp.algo.printMaxSubgraphSummary();
		 exp = new Experiment("turn", "has_prof_clicks_tbl","hdfs_bytes_written",groupby,"SUM", k, "naiveExhaustive", new Euclidean(),0,0.8,false);
		 duration = exp.timedRunOutput();
		 System.out.println("Duration:"+duration);
		 exp.algo.printMaxSubgraphSummary();
		 */
	  /*
	   	// Multiple Random Walk Experiment
		PrintWriter writer = new PrintWriter("random_walk_scalability_experiment.csv", "UTF-8");
	 	writer.println("iterations,total_time,total_utility");
    		Euclidean ed = new Euclidean();
    		Hierarchia h = new Hierarchia("turn","has_list_fn");
    		ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
				   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
		Experiment exp = new Experiment("turn", "has_list_fn", "slots_millis_reduces",groupby,"SUM", 30, "multipleRandomWalk", new Euclidean(),0,0.1);
    		int[] numIterationList= {1,10,100,1000,10000,100000,1000000};
    		for (int iter : numIterationList) {
    			System.out.println("numIteration:"+iter);
    			for (int batch=0;batch<10;batch++) {
    			    exp.setMaxCount(iter);
    			    long duration = exp.timedRunOutput();
    			    writer.println(iter+","+duration+","+exp.lattice.maxSubgraphUtility);
        		}
    		}
    		writer.close();
    	*/
	/*
	Euclidean ed = new Euclidean();
	Hierarchia h = new Hierarchia("turn","has_list_fn");
	ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	Experiment exp = new Experiment("turn", "has_list_fn", "slots_millis_reduces",groupby,"SUM", 30, "frontierGreedy", new Euclidean(),0,0.1);
	long duration = exp.timedRunOutput();
	System.out.println(duration+","+exp.lattice.maxSubgraphUtility);
	exp = new Experiment("turn", "has_list_fn", "slots_millis_reduces",groupby,"SUM", 30, "naiveGreedy", new Euclidean(),0,0.1);
	duration = exp.timedRunOutput();
	System.out.println(duration+","+exp.lattice.maxSubgraphUtility);
	*/
	  /* 
	   Experiment exp;
	   ArrayList<String> all_dimensions = new ArrayList<String>(Arrays.asList("is_successful","is_multi_query","is_profile_query","is_event_query","has_impressions_tbl","has_clicks_tbl","has_actions_tbl","has_rtbids_tbl","has_engagement_evnets_tbl","has_viewability_tbl","has_prof_impressions_tbl","has_prof_clicks_tbl","has_prof_actions_tbl","has_prof_rtbids_tbl","has_prof_engagement_events_tbl","has_prof_data_tbl","has_prof_provider_user_ids_tbl","has_prof_container_tags_tbl","has_prof_segments_tbl","has_prof_viewability_tbl","has_distinct","has_count_distinct","has_sum_distinct","has_est_distinct","has_list_fn","has_corr_list_fn","has_list_has_fn","has_list_count_fn","has_list_sum_fn","has_list_min_fn","has_list_max_fn","has_list_sum_range_fn","has_list_max_range_fn","has_list_min_range_fn","has_where_clause","has_having_clause","has_order_by_clause"));
	   ArrayList<String> all_measures = new ArrayList<String>(Arrays.asList("hdfs_bytes_read","hdfs_bytes_written","total_launched_maps","total_launched_reduces","map_input_records","map_output_records","reduce_input_records","reduce_input_groups","reduce_output_records","slots_millis_maps","slots_millis_reduces"));
	   String [] algoList = {"frontierGreedy","naiveGreedy","greedy","multipleRandomWalk"};//,"exhaustive"
	   int numIterations = 50;
	   int k =30;
	   String aggFunc="SUM";
	   experiment_name="../ipynb/dashboards/json/"+"baseline";
	   // Baseline experiment
	   PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
	   writer.println("xAxis,yAxis,algo,groupby,total_time,total_utility");
	   for (int i=0;i<numIterations;i++) {
		   System.out.println("---------------- Iteration #"+i+"----------------");
		   // We are picking 8 random dimensions in the groupby and one random measure value, since 35C5 combinations is too much, we are just picking random samples of potential dashboards in our experiments.
		   ArrayList<String> groupby = pickNRandom(all_dimensions, 8);
		   String yAxis = all_measures.get(new Random().nextInt(all_measures.size()));
		   String xAxis = groupby.get(new Random().nextInt(groupby.size()));
		   //System.out.println(groupby);
		   //System.out.println(xAxis+","+yAxis);
		   for (String algo : algoList) {
			   try {
				   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, algo, new Euclidean(),0,0.8,false);
				   if (algo.equals("multipleRandomWalk")) {
					   for (int iterations: new  int[] {1,10,1000,10000,100000,1000000}) {
						   exp.setMaxCount(iterations);
						   long duration = exp.timedRunOutput();
						   writer.println(xAxis+","+yAxis+","+algo+iterations+",\"["+Database.arr2DelimitedStrings(groupby, ",")+"\"],"+duration+","+exp.lattice.maxSubgraphUtility);
					   }
				   }else {
					   long duration = exp.timedRunOutput();
					   writer.println(xAxis+","+yAxis+","+algo+",\"["+Database.arr2DelimitedStrings(groupby, ",")+"\"],"+duration+","+exp.lattice.maxSubgraphUtility);
				   }
			   }catch(Exception e) {
				   System.out.println("Failed on:"+ xAxis+","+yAxis+","+algo+",\"["+Database.arr2DelimitedStrings(groupby, ",")+"\"]");
			   }
		   }
	   }
	   writer.close();
	   */
	   
	   // Generating all possible outputs for frontend to use
//	   Experiment exp;
//	   Distance [] distList = {new KLDivergence(),new MaxDiff(),new EarthMover(),new Euclidean()};
//       String [] algoList = {"frontierGreedy","naiveGreedy","greedy"};
//       experiment_name="../ipynb/dashboards/json/"+"vary_all";
//       double [] ip_vals = {0.1,0.3,0.5,0.7,0.9,1};
//       double [] ic_vals = {0,0.05,0.1,0.15,0.2};
//       int [] k_vals = {15,20,25,30};
//       for (Distance dist:distList) {
//	    	   for (String algo:algoList) {
//	    	   	   for (double ip: ip_vals) {
//	    	   		   for (double ic: ic_vals) {
//	    	   			   for (int k : k_vals) {
//	    	   				   try {
//		    	   				   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList("survived","sexcode","pc_class"));
//		    	   				   exp = new Experiment("titanic", "survived", "id",groupby, "COUNT", k, algo, dist,ic,ip);
//						    	   exp.runOutput();
//						    	   groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
//						    			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
//						    	   exp = new Experiment("turn", "has_list_fn", "slots_millis_reduces",groupby,"SUM", k, algo, dist,ic,ip);
//						    	   exp.runOutput();
//						    	   groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
//						    	   exp = new Experiment("mushroom","type","type",groupby, "COUNT", k, algo, dist,ic,ip);
//						    	   exp.runOutput();
//						    	   exp = new Experiment("mushroom","cap_surface","cap_surface",groupby, "COUNT", k, algo, dist,ic,ip);
//						    	   exp.runOutput();
//	    	   				   }
//	    	   				   catch (Exception e){
//	    	   					   System.out.println("Failed at:"+k+","+ic+","+ip+","+algo);
//	    	   				   }
//	    	   			   }
//	    	   		   }
//	    	   	   }
//	       }
//       }
       
       
       /*
		// Testing different algo on different datasets
       String algo = "frontierGreedy";
       double [] ip_vals = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1};
       experiment_name="../ipynb/dashboards/json/"+"vary_dataset_ip";
       for (double ip: ip_vals) {
    	   	   System.out.println("ip:"+ip);
	    	   exp = new Experiment("titanic", "survived", "COUNT(id)", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp = new Experiment("turn", "has_list_fn", "SUM(slots_millis_reduces)", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp = new Experiment("mushroom","type", "COUNT", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp = new Experiment("mushroom","cap_surface", "COUNT", k, algo, new Euclidean(),0.1,ip);
	    	   exp.runOutput();
	    	   exp.h.db.c.close();
       }
	   */	   
	   
	}
}