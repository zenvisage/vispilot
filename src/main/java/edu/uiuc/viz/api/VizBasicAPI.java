package edu.uiuc.viz.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.json.*;
import com.google.gson.Gson;

import edu.uiuc.viz.algorithms.*;
import edu.uiuc.viz.distance.*;
import edu.uiuc.viz.evaluation.*;
import edu.uiuc.viz.lattice.*;
/*! \mainpage
*
* \section intro_sec Introduction
*
* As datasets continue to grow in size and complexity, exploring multi-dimensional datasets remain challenging for
analysts. A common operation during this exploration is drilldown—understanding the behavior of data subsets by progressively adding filters. While widely used, in the absence of
careful attention towards confounding factors, drill-downs
could lead to inductive fallacies. Specifically, an analyst may
end up being “deceived” into thinking that a deviation in
trend is attributable to a local change, when in fact it is a
more general phenomenon; we term this the drill-down fallacy. One way to avoid falling prey to drill-down fallacies is
to exhaustively explore all potential drill-down paths, which
quickly becomes infeasible on complex datasets with many
attributes. We present VisPilot, an accelerated visual data
exploration tool that guides analysts through the key insights
in a dataset, while avoiding drill-down fallacies. Our user
study results show that VisPilot helps analysts discover
interesting visualizations, understand attribute importance,
and predict unseen visualizations better than other multidimensional data analysis baselines.
*
* \section install_sec Installation
*
* \subsection step1 To build the project, run:
```
bash build.sh
```
Under the ``/vispilot/`` directory.

Install postgres at: https://postgresapp.com/

If it doesn't work on the current port(5432), try a another port.
```
$psql -d postgres

CREATE USER summarization WITH CREATEDB CREATEROLE;
ALTER USER summarization WITH PASSWORD 'lattice';
ALTER USER summarization WITH SUPERUSER;
```


\subsection step2 To run the project, run:
```
bash run.sh
```
Under the ``/vispilot/`` directory.

*/

/**
 * The VizBasicAPI class is a collection of API functions to connect the front-end and the back-end.
 */

@Controller
public class VizBasicAPI {
	Hashtable<String, Lattice> cache;
	public VizBasicAPI(){
			cache = new Hashtable<String, Lattice>();
		}
	/**
	 * Generate the Experiment object and the Lattice object.
	 * Return the node list in JSON format.
	 */
	@RequestMapping(value = "/draw", method = RequestMethod.POST)
	@ResponseBody
	public String draw(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, InterruptedException, SQLException {
		
		String name = request.getParameter("datasetName").replace("\"", "");
		String y = request.getParameter("yAxis").replace("\"", "");
		String x = request.getParameter("xAxis").replace("\"", "");
		String agg = request.getParameter("aggType").replace("\"", "");
		int k = Integer.parseInt(request.getParameter("k"));
		double ic = Double.parseDouble(request.getParameter("ic"));
		double info = Double.parseDouble(request.getParameter("info"));
		Traversal ourAlgo = new GreedyPicking();
		Distance dist = new Euclidean();
		ArrayList<String>  groupby = null;
		boolean online = false;
		
		if (name.equals("turn")){
		   	groupby = new ArrayList<String>(Arrays.asList("is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
				"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
		}
		else if (name.equals("ct_police_stop")) {
		   // Dataset #2 : Police Stop
		   groupby = new ArrayList<String>(Arrays.asList(
			"driver_gender", "driver_race", "search_conducted",
			"contraband_found",  "duration", "stop_outcome","is_arrested",
			"stop_time", "driver_age", "speeding_violations", "other_violations",
			"registration_plates_violations","moving_violations","cell_phone_violations"));//"is_arrested",
		}
		else if (name.equals("mushroom")) {
		   // Dataset #3 : Mushroom 
		   groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
		}else if (name.equals("titanic")) {
		   // Dataset #3 : Titanic 
		   groupby = new ArrayList<String>(Arrays.asList("survived","gender","pc_class"));
		}else if (name.equals("autism")) {
		   // Dataset #2 : Autism
		   groupby = new ArrayList<String>(Arrays.asList("autism", "a1_score", "a2_score", "a3_score", "a4_score", "a5_score", "a6_score", "a7_score","a8_score", "a9_score", "a10_score"));
		}else if (name.equals("cancer")){
		   // for debug only: dropping high cardinality columns ["primary_diagnosis","morphology","tissue_or_organ_of_origin","site_of_resection_or_biopsy"]
			groupby = new ArrayList<String>(Arrays.asList("gender", "race", "vital_status", "ethnicity", "age",
		       "years_to_last_follow_up",  "morphology_behavior",
		       "diagnosis_contain_adenocarcinoma", "diagnosis_contain_squamous",
		       "diagnosis_contain_papillary", "diagnosis_contain_infiltrating",
		       "diagnosis_contain_glioblastoma",
		       "diagnosis_contain_cystadenocarcinoma", "diagnosis_contain_melanoma",
		       "diagnosis_contain_endometrioid", "diagnosis_contain_hepatocellular",
		       "diagnosis_contain_transitional", "diagnosis_contain_lobular",
		       "diagnosis_contain_mixed", "diagnosis_contain_oligodendroglioma",
		       "diagnosis_contain_anaplastic", "diagnosis_contain_leukemia",
		       "diagnosis_contain_lymphoma", "diagnosis_contain_acute",
		       "diagnosis_contain_myeloid", "diagnosis_contain_tumor",
		       "origin_region"));
		   // groupby = new ArrayList<String>(Arrays.asList(
		   // 	"gender", "race", "vital_status", "ethnicity", "age",
		   //     "years_to_last_follow_up", "morphology_behavior",
		   //     "diagnosis_contain_squamous",
		   //      "diagnosis_contain_melanoma",
		   //      "diagnosis_contain_mixed", 
		   //      "diagnosis_contain_leukemia",
		   //      "diagnosis_contain_lymphoma", 
		   //      "diagnosis_contain_tumor",
		   //     "origin_region"));
		   
		}
//		Database d = new Database();
//		ResultSet ret= d.getColumns(name);
//		groupby = d.resultSet2ArrayStr(ret);
//		groupby.remove(new String("id"));
		
		String key = name + x + y + agg;
		
		
		if(cache.containsKey(key)) {//exists in cache
			System.out.print("----------------------exists in cache-----------------------");
			Experiment exp = new Experiment(name, x, y ,groupby,agg, k, dist,ic,info,false);
			Lattice l = cache.get(key);
			exp.setLattice(l);
			exp.setK(k);
			exp.setAlgo(ourAlgo);
	        String nodedic = exp.runOutputReturnJSON(exp);
			return nodedic;
		}
		else {//does not exist in cache
			System.out.print("--------------------does not exist in cache------------------");
			
			System.out.print(name + x + y + agg + Integer.toString(k) + " "+Double.toString(ic)+" "+Double.toString(info));
			Experiment exp = new Experiment(name, x, y ,groupby,agg, k, dist,ic,info,false);
			System.out.print(exp.uniqueAttributeKeyVals);
			
//			for (Entry<String, ArrayList<String>> entry : exp.uniqueAttributeKeyVals.entrySet()) {
//			    String attr = entry.getKey();
//			    int length = entry.getValue().size();
//			    if(length >= 20) {
//			    	exp.groupby.remove(attr);
//			    }
//			}
//			exp.groupby = exp.attribute_names;
//			java.util.Iterator<Entry<String, ArrayList<String>>> it = exp.uniqueAttributeKeyVals.entrySet().iterator();
//		    while (it.hasNext()) {
//		        Map.Entry pair = (Map.Entry)it.next();
//		        String attr = (String) pair.getKey();
//		        int len = ((ArrayList<String>) pair.getValue()).size();
//		        if(len>=20) {// remove attributes with large cardinality
//		        	exp.attribute_names.remove(attr);
//		        	exp.groupby.remove(attr);
//		        	it.remove();
//		        }
//		        //System.out.println(pair.getKey() + " = " + pair.getValue());
//		         // avoids a ConcurrentModificationException
//		    }
			
			System.out.println(exp.attribute_names);
			System.out.println(exp.uniqueAttributeKeyVals);
			System.out.println(exp.groupby);
			//exp.groupby = groupby;
			exp.buildLattice(false);
			cache.put(key, exp.lattice);
			exp.setAlgo(ourAlgo);
	        String nodedic = exp.runOutputReturnJSON(exp);
			return nodedic;
		}		
    }
	
	/**
	 * Fetch the column name of a given table.
	 */
	@RequestMapping(value = "/getCol", method = RequestMethod.POST)
	@ResponseBody
	public ArrayList<String> getCol(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, InterruptedException, SQLException {
		String name = request.getParameter("datasetName").replace("\"", "");
		if(name == "Select an Option") {
			return new ArrayList<String>(Arrays.asList("Select an Option"));
		}
		Database d = new Database();
		ResultSet ret= d.getColumns(name);
		ArrayList<String> colArrs = d.resultSet2ArrayStr(ret);
		return colArrs;
	}	
	
	/**
	 * Fetch the names of all tables in the database.
	 */
	@RequestMapping(value = "/getTable", method = RequestMethod.POST)
	@ResponseBody
	public ArrayList<String> getTable(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, InterruptedException, SQLException {
		
		Database d = new Database();
		ResultSet ret= d.getTables();
		ArrayList<String> tableArrs = d.resultSet2ArrayStr(ret);
		return tableArrs;
	}	
	
	/**
	 * Generate a new expanded node list given the expand number.
	 */
	@RequestMapping(value = "/expand", method = RequestMethod.POST)
	@ResponseBody
	public String expand(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, InterruptedException, SQLException {
		String newroot = request.getParameter("rootid").replace("\"", "");
		int expand = Integer.parseInt(request.getParameter("expand"));
		System.out.print(expand);
		Distance dist = new Euclidean();
		ArrayList<String>  groupby = null;

		boolean online = false;
		
		String name = request.getParameter("datasetName").replace("\"", "");
		String y = request.getParameter("yAxis").replace("\"", "");
		String x = request.getParameter("xAxis").replace("\"", "");
		String agg = request.getParameter("aggType").replace("\"", "");
		//int k = Integer.parseInt(request.getParameter("k"));
		double ic = Double.parseDouble(request.getParameter("ic"));
		double info = Double.parseDouble(request.getParameter("info"));
		ArrayList<Integer> org = new ArrayList<Integer>(Arrays.asList(1,2,3,5,6,7,8,9));
		Traversal ourAlgo = new GreedyPickingAlternativeRoot(newroot);
		
//		if (name.equals("turn")){
//		   	groupby = new ArrayList<String>(Arrays.asList("is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
//				"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
//		}
//		else if (name.equals("ct_police_stop")) {
//		   // Dataset #2 : Police Stop
//		   groupby = new ArrayList<String>(Arrays.asList(
//			"driver_gender", "driver_race", "search_conducted",
//			"contraband_found",  "duration", "stop_outcome",
//			"stop_time", "driver_age"));//"is_arrested",
//		}
//		else if (name.equals("mushroom")) {
//		   // Dataset #3 : Mushroom 
//		   groupby = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
//		}else if (name.equals("titanic")) {
//		   // Dataset #3 : Titanic 
//		   groupby = new ArrayList<String>(Arrays.asList("survived","gender","pc_class"));
//		}else if (name.equals("autism")) {
//		   // Dataset #2 : Autism
//		   groupby = new ArrayList<String>(Arrays.asList("autism", "a1_score", "a2_score", "a3_score", "a4_score", "a5_score", "a6_score", "a7_score","a8_score", "a9_score", "a10_score"));
//		}
		
		Database d = new Database();
		ResultSet ret= d.getColumns(name);
		groupby = d.resultSet2ArrayStr(ret);
		groupby.remove(new String("id"));
		
		String key = name + x + y + agg;
		System.out.print(name + x + y + agg + Integer.toString(expand) + " "+Double.toString(ic)+" "+Double.toString(info));
		Experiment exp = new Experiment(name, x, y ,groupby,agg, expand, dist,ic,info,false);
		Lattice l = cache.get(key);
		exp.setLattice(l);
		exp.setK(expand);
		exp.setAlgo(ourAlgo);
        String nodedic = exp.runOutputReturnJSON(exp);
//      System.out.print(nodedic);
//		String nodedic = name + x + y + agg + Integer.toString(k) + " "+Double.toString(ic)+" "+Double.toString(info);
		return nodedic;
	}
	
	
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException{
		Experiment.experiment_name="../ipynb/dashboards/";
		Distance dist = new Euclidean();
		ArrayList<String>  groupby = new ArrayList<String>(Arrays.asList("survived","gender","pc_class"));
		//"titanic""survived""id""COUNT"9 0.0 0.9
		String yAxis = "id";
	    String xAxis = "survived"; 
	    String aggType = "COUNT";
	    Traversal ourAlgo = new GreedyPickingAlternativeRoot("#gender$female#");
	    Experiment exp = new Experiment("titanic", xAxis, yAxis,groupby,aggType, 9, dist,0,0.9,false);
	    System.out.println(exp.lattice.id2IDMap.get("#gender$female#"));
	    exp.setAlgo(ourAlgo);
		VizOutput vo = new VizOutput(exp);
        String nodedic = vo.generateNodeDic();
        System.out.print(nodedic);
		
	}
	
}





