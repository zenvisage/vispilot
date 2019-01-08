package edu.uiuc.viz.evaluation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
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
import edu.uiuc.viz.algorithms.GreedyPickingAlternativeRoot;
import edu.uiuc.viz.algorithms.MultipleRandomWalk;
import edu.uiuc.viz.algorithms.RandomWalk;
import edu.uiuc.viz.algorithms.Traversal;
import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;
import edu.uiuc.viz.lattice.Database;

public class UserStudyBaseline {
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
	{
	   Experiment exp;
	   int k =9;
	   Experiment.experiment_name="../ipynb/dashboards/";
	   String dataset_name="titanic";
	   ArrayList<String> groupby = null;
	   String yAxis = null;
	   String xAxis = null; 
	   String aggType = null;
	   Distance dist = new Euclidean();
	   // Dataset #1 : Turn
	   if (dataset_name.equals("turn")){
		   	groupby = new ArrayList<String>(Arrays.asList("is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
				"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
			yAxis = "job_id";
			xAxis = "is_multi_query";//"has_list_fn";
			aggType = "COUNT";
	   }else if (dataset_name.equals("ct_police_stop")) {
		   // Dataset #2 : Police Stop
		   groupby = new ArrayList<String>(Arrays.asList(
			"driver_gender", "driver_race", "search_conducted",
			"contraband_found",  "duration", "stop_outcome",
			"stop_time", "driver_age"));//"is_arrested",
		   yAxis = "id";
		   xAxis = "stop_outcome";//"is_arrested";  
		   aggType = "COUNT";
	   }else if (dataset_name.equals("mushroom")) {
		   // Dataset #3 : Mushroom 
		   groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
		   yAxis = "type";
		   xAxis = "type";//"cap_surface"; 
		   aggType = "COUNT";
	   }else if (dataset_name.equals("titanic")) {
		   // Dataset #3 : Titanic 
		   groupby = new ArrayList<String>(Arrays.asList("survived","gender","pc_class"));
		   yAxis = "id";
		   xAxis = "survived"; 
		   aggType = "COUNT";
	   }else if (dataset_name.equals("autism")) {
		   // Dataset #2 : Autism
		   groupby = new ArrayList<String>(Arrays.asList("autism", "a1_score", "a2_score", "a3_score", "a4_score", "a5_score", "a6_score", "a7_score","a8_score", "a9_score", "a10_score"));
				   //,"gender", "jaundice", "pdd_family"
		   yAxis = "*";
		   xAxis = "autism";  
		   aggType = "COUNT";
	   }
	   
//	   Traversal ourAlgo = new GreedyPicking();
//	   exp = new Experiment(dataset_name, xAxis, yAxis,groupby,aggType, k, dist,0,0.9,false);
//	   exp.setK(5);
//	   exp.setAlgo(ourAlgo);
//	   exp.runOutput(exp);
//	   Traversal algo = new GreedyPickingAlternativeRoot(exp.dashboard.maxSubgraph,"#duration$30+ min#");
//	   exp.setK(3);
//	   exp.setAlgo(algo);
//	   exp.runOutput(exp);
	   Traversal ourAlgo = new GreedyPicking();
	   exp = new Experiment(dataset_name, xAxis, yAxis,groupby,aggType, k, dist,0,0.9,false);
	   exp.setAlgo(ourAlgo);
	   exp.runOutput(exp);
//	   Traversal ourAlgo = new BreadthFirstPicking();
//	   exp = new Experiment(dataset_name, xAxis, yAxis,groupby,aggType, k, dist,0,0.9,false);
//	   exp.setAlgo(ourAlgo);
//	   exp.runOutput(exp);
//
//	   exp = new Experiment(dataset_name, xAxis, yAxis,groupby,aggType, k, dist,0,0.001,false);
//	   Traversal clustering = new BaselineKmeans();
//	   exp.setAlgo(clustering);
//	   exp.runTableLayoutOutput(exp);   
//	   
//	   Traversal BBFS = new BaselineBFS();
//	   exp.setAlgo(BBFS);
//	   exp.runTableLayoutOutput(exp);   
	   
//	   Traversal randWalk = new RandomWalk();
//	   exp.setAlgo(randWalk);
//	   exp.runTableLayoutOutput(exp);   
	   
	}
}
