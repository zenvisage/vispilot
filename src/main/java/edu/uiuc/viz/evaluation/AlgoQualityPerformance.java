package edu.uiuc.viz.evaluation;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.uiuc.viz.algorithms.BaselineBFS;
import edu.uiuc.viz.algorithms.BaselineKmeans;
import edu.uiuc.viz.algorithms.BreadthFirstPicking;
import edu.uiuc.viz.algorithms.ExhaustivePicking;
import edu.uiuc.viz.algorithms.Experiment;
import edu.uiuc.viz.algorithms.GreedyPicking;
import edu.uiuc.viz.algorithms.MultipleRandomWalk;
import edu.uiuc.viz.algorithms.RandomWalk;
import edu.uiuc.viz.algorithms.RecursiveBreadthFirstPicking;
import edu.uiuc.viz.algorithms.RecursiveNaiveGreedyPicking;
import edu.uiuc.viz.algorithms.ProbablisticPicking;
import edu.uiuc.viz.algorithms.MultipleProbablisticPicking;
import edu.uiuc.viz.algorithms.Traversal;
import edu.uiuc.viz.algorithms.TwoStepLookAheadalgorithm;
import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.EarthMover;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.distance.KLDivergence;
import edu.uiuc.viz.distance.MaxDiff;
import edu.uiuc.viz.lattice.Database;

public class AlgoQualityPerformance {
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
	{
		
	   Experiment exp;
	   int k =10;
	   
	   String aggFunc="SUM";
	   Experiment.experiment_name="../json/";
	   //Distance dist = new Euclidean();
	   // Baseline experiment
	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	   String yAxis = "slots_millis_reduces";
	   String xAxis = "has_list_fn";
	   //String yAxis = "survived";
	   //String xAxis = "has_list_fn";
	   // All Algo Experiments:
	   Traversal frontierGreedy = new BreadthFirstPicking();
	   Traversal levelWiseGreedy = new GreedyPicking();
	   Traversal exhaustive = new ExhaustivePicking();
	   Traversal SRW = new RandomWalk();
	   Traversal MRW1000 = new MultipleRandomWalk(1000);
	   Traversal MRW100000 = new MultipleRandomWalk(100000);
	   Traversal MRW1000000 = new MultipleRandomWalk(1000000);
	   Traversal LA2max = new TwoStepLookAheadalgorithm("max");
	   Traversal LA2sum = new TwoStepLookAheadalgorithm("sum");
	   Traversal LA5 = new RecursiveBreadthFirstPicking(5);
	   Traversal LA5_levelwise = new RecursiveNaiveGreedyPicking(5);
	   Traversal PP = new ProbablisticPicking();
	   Traversal MPP = new MultipleProbablisticPicking();
	   //Traversal[] algoList= {SRW,MRW1000,MRW100000,frontierGreedy,LA2max,LA2sum,LA5,LA5_levelwise,PP};//exhaustive,
	   //Traversal[] algoList= {new ExhaustivePicking()};
	   Traversal[] algoList= {PP};
	   //Distance [] distList = {new KLDivergence(),new MaxDiff(),new EarthMover(),new Euclidean()};
	   Distance [] distList = {new Euclidean()};
	   
	   // Single Turn Experiment k=10 and k=30 , vary algo, dist
//	   PrintWriter writer = new PrintWriter("output_all_dist_k30.csv", "UTF-8");
//	   writer.println("xAxis,yAxis,algo,groupby,dist,total_time,total_utility");
	   
	   /*
	   for (Distance dist: distList) {
		   for (Traversal algo : algoList) {
			   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, algo, dist,0,0.8,false);
			   long duration = exp.timedRunOutput(exp);
			   writer.println(xAxis+","+yAxis+","+algo.getAlgoName()+",\"["+Database.arr2DelimitedStrings(groupby, ",")+"\"],"+dist.getDistName()+","+duration+","+exp.dashboard.maxSubgraphUtility);   
		   }		   
	   }
	   */
	   
	   
	   // This is the baseline Random Walk algorithm
	   System.out.println("***************This is baseline Random Walk algorithm***************");
	   Distance euclid = new Euclidean();
	   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, euclid,0,0.001,false);
	   exp.setAlgo(SRW);
	   long duration = exp.timedRunOutput(exp);   
	   
	   // This is the baseline Kmeans Clustering algorithm
	   Traversal BK = new BaselineKmeans();
	   System.out.println("***************This is baseline Kmeans Clustering algorithm***************");
	   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, euclid,0,0.001,false);
	   exp.setAlgo(BK);
	   duration = exp.timedRunOutput(exp);   
	   
	   // This is the baseline BFS algorithm
	   Traversal BBFS = new BaselineBFS();
	   System.out.println("***************This is baseline BFS algorithm***************");
	   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, euclid,0,0.001,false);
	   exp.setAlgo(BBFS);
	   duration = exp.timedRunOutput(exp);
	   
	   
	   
	   /*
	   ArrayList<String> all_dimensions = new ArrayList<String>(Arrays.asList("is_successful","is_multi_query","is_profile_query","is_event_query","has_impressions_tbl","has_clicks_tbl","has_actions_tbl","has_rtbids_tbl","has_engagement_evnets_tbl","has_viewability_tbl","has_prof_impressions_tbl","has_prof_clicks_tbl","has_prof_actions_tbl","has_prof_rtbids_tbl","has_prof_engagement_events_tbl","has_prof_data_tbl","has_prof_provider_user_ids_tbl","has_prof_container_tags_tbl","has_prof_segments_tbl","has_prof_viewability_tbl","has_distinct","has_count_distinct","has_sum_distinct","has_est_distinct","has_list_fn","has_corr_list_fn","has_list_has_fn","has_list_count_fn","has_list_sum_fn","has_list_min_fn","has_list_max_fn","has_list_sum_range_fn","has_list_max_range_fn","has_list_min_range_fn","has_where_clause","has_having_clause","has_order_by_clause"));
	   ArrayList<String> all_measures = new ArrayList<String>(Arrays.asList("hdfs_bytes_read","hdfs_bytes_written","total_launched_maps","total_launched_reduces","map_input_records","map_output_records","reduce_input_records","reduce_input_groups","reduce_output_records","slots_millis_maps","slots_millis_reduces"));
	   int numIterations = 50;
	   Euclidean dist = new Euclidean();
	   PrintWriter writer = new PrintWriter("output_randomize50.csv", "UTF-8");
	   writer.println("xAxis,yAxis,algo,groupby,dist,total_time,total_utility");
	   for (int i=0;i<numIterations;i++) {
		   System.out.println("---------------- Iteration #"+i+"----------------");
		   // We are picking 8 random dimensions in the groupby and one random measure value, since 35C5 combinations is too much, we are just picking random samples of potential dashboards in our experiments.
		   groupby = Experiment.pickNRandom(all_dimensions, 8);
		   yAxis = all_measures.get(new Random().nextInt(all_measures.size()));
		   xAxis = groupby.get(new Random().nextInt(groupby.size()));
		   
		   for (Traversal algo : algoList) {
			   exp = new Experiment("turn", xAxis, yAxis,groupby,"SUM", k, algo, dist,0,0.8,false);
			   try {
				   long duration = exp.timedRunOutput(exp);
				   writer.println(xAxis+","+yAxis+","+algo.getAlgoName()+",\"["+Database.arr2DelimitedStrings(groupby, ",")+"\"],"+dist.getDistName()+","+duration+","+exp.lattice.maxSubgraphUtility);
			   }catch(Exception e) {
				   i-=1;
				   break;
			   }
			      
		   }		   
	   }
	   writer.close();
	   */
	    
	  	   
	}
}
