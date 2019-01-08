package edu.uiuc.viz.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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



@Controller
public class VizBasicAPI {

	public VizBasicAPI(){
	
		}
	
	@RequestMapping(value = "/draw", method = RequestMethod.POST)
	@ResponseBody
	public String draw(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, InterruptedException, SQLException {
		Distance dist = new Euclidean();
		ArrayList<String>  groupby = null;

		boolean online = false;
		
		String name = request.getParameter("datasetName").replace("\"", "");
		String y = request.getParameter("yAxis").replace("\"", "");
		String x = request.getParameter("xAxis").replace("\"", "");
		String agg = request.getParameter("aggType").replace("\"", "");
		int k = Integer.parseInt(request.getParameter("k"));
		double ic = Double.parseDouble(request.getParameter("ic"));
		double info = Double.parseDouble(request.getParameter("info"));
		Traversal ourAlgo = new GreedyPicking();
		
		if (name.equals("turn")){
		   	groupby = new ArrayList<String>(Arrays.asList("is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
				"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
		}
		else if (name.equals("ct_police_stop")) {
		   // Dataset #2 : Police Stop
		   groupby = new ArrayList<String>(Arrays.asList(
			"driver_gender", "driver_race", "search_conducted",
			"contraband_found",  "duration", "stop_outcome",
			"stop_time", "driver_age"));//"is_arrested",
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
		}


		System.out.print(name + x + y + agg + Integer.toString(k) + " "+Double.toString(ic)+" "+Double.toString(info));
		Experiment exp = new Experiment(name, x, y ,groupby,agg, k, dist,ic,info,false);
		exp.setAlgo(ourAlgo);
        String nodedic = exp.runOutputReturnJSON(exp);
//      System.out.print(nodedic);
//		String nodedic = name + x + y + agg + Integer.toString(k) + " "+Double.toString(ic)+" "+Double.toString(info);
		return nodedic;
    }
	
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
	
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException{
		/*Experiment.experiment_name="../ipynb/dashboards/";
		Distance dist = new Euclidean();
		ArrayList<String>  groupby = new ArrayList<String>(Arrays.asList("survived","gender","pc_class"));
		//"titanic""survived""id""COUNT"9 0.0 0.9
		String yAxis = "id";
	    String xAxis = "survived"; 
	    String aggType = "COUNT";
	    Traversal ourAlgo = new GreedyPicking();
	    Experiment exp = new Experiment("titanic", xAxis, yAxis,groupby,aggType, 9, dist,0,0.9,false);
	    exp.setAlgo(ourAlgo);
		VizOutput vo = new VizOutput(exp);
        String nodedic = vo.generateNodeDic();
        System.out.print(nodedic);*/
		Database d = new Database();
		ResultSet ret= d.getColumns("titanic");
		ArrayList<String> colArrs = d.resultSet2ArrayStr(ret);
		System.out.print(colArrs);
	}
	
}





