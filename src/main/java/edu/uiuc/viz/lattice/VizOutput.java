package edu.uiuc.viz.lattice;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.uiuc.viz.algorithms.BreadthFirstPicking;
import edu.uiuc.viz.algorithms.Experiment;
import edu.uiuc.viz.algorithms.RandomWalk;
import edu.uiuc.viz.algorithms.Traversal;
import edu.uiuc.viz.distance.Distance;
import edu.uiuc.viz.distance.Euclidean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
public class VizOutput {
	public static Experiment exp;
	public VizOutput(Experiment exp) {
		this.exp = exp;
	}
	public String generateNodeDic() {
		// Node dictionary contains all the data required for generating the visualizations
		/*  {1: [{'xAxis': 'Clinton', 'yAxis': 48},
			  {'xAxis': 'Trump', 'yAxis': 46},
			  {'xAxis': 'Others', 'yAxis': 6},
			  {'childrenIndex': [2, 3], 'filter': 'All', 'yName': '% of vote'}],
			 2: [{'xAxis': 'Clinton', 'yAxis': 31},
			  {'xAxis': 'Trump', 'yAxis': 62},
			  {'xAxis': 'Others', 'yAxis': 7},
			  {'childrenIndex': [], 'filter': 'Race = White', 'yName': '% of vote'}],
			 3: [{'xAxis': 'Clinton', 'yAxis': 21},
			  {'xAxis': 'Trump', 'yAxis': 70},
			  {'xAxis': 'Others', 'yAxis': 9},
			  {'childrenIndex': [], 'filter': 'Gender = F', 'yName': '% of vote'}]}
		*/
		ArrayList<String> xAttr = exp.uniqueAttributeKeyVals.get(exp.xAxisName);
		String nodeDic = "{";
		//nodeDic+="\\\"label\\\":[{\\\"xName\\\":\\\""+exp.xAxisName+"\\\",\\\"yName\\\":\\\""+exp.aggFunc+"("+exp.yAxisName+")"+"\\\"}],";
		for (int i=0; i< exp.dashboard.maxSubgraph.size();i++) {
			int selectedNodeID = exp.dashboard.maxSubgraph.get(i);
			//System.out.println("i="+i+",nodeID:"+selectedNodeID);
			nodeDic+= "\\\""+(selectedNodeID)+"\\\": [";
			Node selectedNode = exp.lattice.nodeList.get(selectedNodeID);
			ArrayList<Double> nodeVal = exp.lattice.id2MetricMap.get(selectedNode.id);
			for (int ix=0; ix<xAttr.size();ix++) {
				nodeDic+="{ \\\"xAxis\\\": \\\""+xAttr.get(ix)+"\\\", \\\"yAxis\\\":"+ nodeVal.get(ix) +"},";
			}
			nodeDic+="{\\\"childrenIndex\\\":"+selectedNode.get_child_list()+
					", \\\"populationSize\\\":"+selectedNode.getPopulation_size()+
				    ", \\\"filter\\\":\\\""+selectedNode.get_id() +"\\\",\\\"yName\\\":\\\""+exp.aggFunc+"("+exp.yAxisName+")"+
				    "\\\",\\\"xName\\\":\\\""+exp.xAxisName+"\\\"}]";
			if (i!=exp.dashboard.maxSubgraph.size()-1) {
				nodeDic+=',';
			}
			//System.out.println(selectedNode.get_id()+" : "+selectedNode.get_child_list());
		}
		nodeDic+="}";
		//System.out.println("selectedNodes:"+selectedNodes);
		//System.out.println(nodeDic);
		return nodeDic;
	}
	public String generateOrderedNodeDic() {
		// Generating node dictionary where keys are all sequentially ordered (for table layout)
		ArrayList<String> xAttr = exp.uniqueAttributeKeyVals.get(exp.xAxisName);
		String nodeDic = "{";
		for (int i=0; i< exp.dashboard.maxSubgraph.size();i++) {
			int selectedNodeID = exp.dashboard.maxSubgraph.get(i);
			nodeDic+= "\\\""+(i)+"\\\": [";
			Node selectedNode = exp.lattice.nodeList.get(selectedNodeID);
			ArrayList<Double> nodeVal = exp.lattice.id2MetricMap.get(selectedNode.id);
			for (int ix=0; ix<xAttr.size();ix++) {
				nodeDic+="{ \\\"xAxis\\\": \\\""+xAttr.get(ix)+"\\\", \\\"yAxis\\\":"+ nodeVal.get(ix) +"},";
			}
			nodeDic+="{\\\"childrenIndex\\\":"+selectedNode.get_child_list()+
					", \\\"populationSize\\\":"+selectedNode.getPopulation_size()+
				    ", \\\"filter\\\":\\\""+selectedNode.get_id() +"\\\",\\\"yName\\\":\\\""+exp.aggFunc+"("+exp.yAxisName+")"+
				    "\\\",\\\"xName\\\":\\\""+exp.xAxisName+"\\\"}]";
			if (i!=exp.dashboard.maxSubgraph.size()-1) {
				nodeDic+=',';
			}
			//System.out.println(selectedNode.get_id()+" : "+selectedNode.get_child_list());
		}
		nodeDic+="}"; 
		return nodeDic;
	}
	
	public static void dumpGenerateNodeDicFromNoHierarchia(int idx,Lattice lattice,ArrayList<Integer> selectedNodes) {
		String nodeDic = "{";
		for (int i=0; i<selectedNodes.size();i++) {
			int selectedNodeID = selectedNodes.get(i);
			//System.out.println("i="+i+",nodeID:"+selectedNodeID);
			nodeDic+= "\\\""+(selectedNodeID)+"\\\": [";
			Node selectedNode = lattice.nodeList.get(selectedNodeID);
			ArrayList<Double> nodeVal = lattice.id2MetricMap.get(selectedNode.id);
			for (int ix=0; ix<2;ix++) {
				nodeDic+="{ \\\"xAxis\\\": \\\""+"x"+"\\\", \\\"yAxis\\\":"+ nodeVal.get(ix) +"},";
			}
			nodeDic+="{\\\"childrenIndex\\\":"+selectedNode.get_child_list()+
					", \\\"populationSize\\\":"+selectedNode.getPopulation_size()+
				    ", \\\"filter\\\":\\\""+selectedNode.get_id() +"\\\",\\\"yName\\\":\\\""+"y"+"\\\"}]";
			if (i!=selectedNodes.size()-1) {
				nodeDic+=',';
			}
		}
		nodeDic+="}";
		dumpString2File("test"+idx+".json", nodeDic);
	}
	public String generateLatticeDic() {
		// Dictionary containing node IDs and Lattice/Graph 
		return null;
	}
	public static void dumpString2File(String filename,String content) {
		try {
            Files.write(Paths.get(filename), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
	{
	   Experiment exp;
	   int k =10;
	   Experiment.experiment_name="../ipynb/dashboards/json/UserStudyBaseline";
	   // Dataset #1  
	   String dataset_name = "turn";
	   ArrayList<String> groupby = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
			   	"has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
	   String yAxis = "slots_millis_reduces";
	   String xAxis = "has_list_fn";
	   String aggType = "SUM";
	   Distance dist = new Euclidean();
	   Traversal BFS = new RandomWalk();
	   exp = new Experiment(dataset_name, xAxis, yAxis,groupby,aggType, k, dist,0,0.8,false);
	   exp.setAlgo(BFS);
	   exp.runOutput(exp);
	   exp.runTableLayoutOutput(exp);   
	}
}
