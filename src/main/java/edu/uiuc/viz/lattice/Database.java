package edu.uiuc.viz.lattice;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import edu.uiuc.viz.algorithms.Experiment;
public class Database {
	private String database = "summarization";
	private String host = "jdbc:postgresql://localhost:5432/"+database;
	private String username = "summarization";
	private String password = "lattice";
	public static Connection c = null;
	public Database() {
		 try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager.getConnection(host, username, password);
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
      System.out.println("Opened database successfully");
	}
	/**
	 * Query database and return result
	 */ 
	public static  ResultSet query(String sQLQuery) throws SQLException {
		//System.out.println(sQLQuery);
	    Statement stmt = c.createStatement();
	    return stmt.executeQuery(sQLQuery);
	}
	
	
	public static ArrayList<Double> printResultSet(ResultSet rs) throws SQLException {
		
		ArrayList<Double> rsArr = new ArrayList<Double>();
		while (rs.next()) {
//			rsArr.add(rs.getString(1));
			for (int j =1 ; j< rs.getMetaData().getColumnCount()+1; j++ ) {
				System.out.print(rs.getString(j)+",");
			}
			System.out.print("\n");
	    }
		return rsArr;
	}
	
	/**
	 * Covert the result set object to a ArrayList.
	 */
	public static ArrayList<String> resultSet2ArrayStr(ResultSet rs) throws SQLException {
		ArrayList<String> rsArr = new ArrayList<String>();
		while (rs.next()) {
			for (int j =1 ; j< rs.getMetaData().getColumnCount()+1; j++ ) {
				//System.out.print(rs.getString(j)+",");
				rsArr.add(rs.getString(j));
			}
			//System.out.print("\n");
	    }
		return rsArr;
	}
	
	/**
	 * Covert the result set object to a CSV file.
	 */
	public static void resultSet2csv(ResultSet rs,String filename, ArrayList<String> header,String measure_name) throws SQLException, FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filename+".csv", "UTF-8");
		writer.println(arr2DelimitedStrings(header, ",")+","+measure_name);
		ArrayList<Double> rsArr = new ArrayList<Double>();
		while (rs.next()) {
//			rsArr.add(rs.getString(1));
			for (int j =1 ; j< rs.getMetaData().getColumnCount()+1; j++ ) {
				if (j!=rs.getMetaData().getColumnCount()) {
					writer.print(rs.getString(j)+",");
				}else {
					writer.print(rs.getString(j));
				}
				
			}
			writer.print("\n");
	    }
		writer.close();
	}
	
	public static String arr2DelimitedStrings(ArrayList<String> arr, String delimiter) {
		String arrJoined="";
		for (int i=0; i< arr.size(); i++) {
			if (i != arr.size()-1) {
				arrJoined += arr.get(i) ;
				if (delimiter==",") {
					arrJoined += delimiter;
				}else {
					arrJoined += " "+delimiter+" ";
				}
			}else {
				arrJoined += arr.get(i);
			}
		}
		return arrJoined;
	}
	
	/**
	 * Generate the query statement, query the data set and return result.
	 */
	public static  ResultSet viz_query(String tablename, ArrayList<String> x_attr,String y_attr,String agg_func, ArrayList<String> filters) throws SQLException {
		String xAttrJoined =arr2DelimitedStrings(x_attr, ",");
		String query_stmt = "SELECT " + xAttrJoined + ", " +agg_func +"(" + y_attr + ")" + " FROM " + tablename;
		if (filters.size()==0) {
			query_stmt += " GROUP BY " + xAttrJoined + ";";
		}else {
			query_stmt += " WHERE "+ arr2DelimitedStrings(filters, "AND");
	        query_stmt +=" GROUP BY " + xAttrJoined +";";
		}
		System.out.println(query_stmt);
		ResultSet result = query(query_stmt);
		return result;
	}
	public static boolean isNumeric(String s) {  
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  
	public static ArrayList<Double> computeViz(Experiment exp, ArrayList<String> filters) throws SQLException {
		//System.out.println("------");
		//System.out.println(filters);
		// When the filter variable are characters and not int/floats, need to insert single quotes.
		ArrayList<String> newFilters = new ArrayList<String>();
		if (filters.size()!=0) { // non-root
			int startIdx = filters.get(0).indexOf("=");
			if (!isNumeric(filters.get(0).substring(startIdx))) {
				Iterator<String> iter = filters.iterator();
				while (iter.hasNext()) {
					String filter = iter.next();
					newFilters.add(filter.replace("=", "='") +"'");
					iter.remove();
				}
			}else {
				newFilters = filters;
			}
		}
		
		
		String query_stmt ="SELECT " + exp.xAxisName + ", " +exp.aggFunc +"(" + exp.yAxisName + ")" + " FROM " + exp.datasetName;
		if (newFilters.size()!=0) { query_stmt += " WHERE "+ arr2DelimitedStrings(newFilters, "AND");}
        query_stmt +=" GROUP BY " + exp.xAxisName+";"; 
        //System.out.println(query_stmt);
		ResultSet rs = query(query_stmt);
		ArrayList<Double> measure_values = new ArrayList<Double>();
		ArrayList<String> options = exp.uniqueAttributeKeyVals.get(exp.xAxisName);
		HashMap<String,Double> opt2measure = new HashMap<String,Double> (); 
		while (rs.next()) {
			String opt = rs.getString(1);
			double val = rs.getDouble(rs.getMetaData().getColumnCount());
			opt2measure.put(opt, val);
		}
		// Fill zero to populate measure_value array from HashMap
		
		for (int oi=0 ; oi<options.size();oi++) {
			String opt = options.get(oi);
			//System.out.println(opt);
			if (opt2measure.containsKey(opt)) {
				measure_values.add(opt2measure.get(opt));
			}else {
				measure_values.add(0.0);
//				System.out.println("Contain missing value");
//				System.out.println(opt2measure);
			}
//			System.out.println(measure_values);
	    }
	    return measure_values;
		
	}
	
	/**
	 * Get the column names of a given table.
	 */
	public static ResultSet getColumns(String tablename) throws SQLException {
		/*
		  Get a list of all the columns inside a particular table to display to the front end
		  in the x and y axis selection panel dropdown menu
	
		  SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' AND table_name = '<tablename>';
	
		  e.g.
		  summarization=# SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' AND table_name = 'titanic';
		   column_name
		  -------------
		   Name
		   PClass
		   Age
		   Sex
		   Survived
		   SexCode
		  (6 rows)
		*/
		String query_stmt = "SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' AND table_name = " + "'" + tablename + "';";
	    return Database.query(query_stmt);
	}
	
	/**
	 * Find the distinct values for this attribute
	 */
	public static ResultSet findDistinctAttrVal(String attribute, String tablename) throws SQLException {
		String query_stmt = "SELECT DISTINCT "+ attribute +" FROM "+tablename+" ;";
		return Database.query(query_stmt);
	}
	
	/**
	 * Get the name of all tables stored in the database.
	 */
	public static ResultSet getTables() throws SQLException {
		String query_stmt = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND TABLE_TYPE='BASE TABLE';";
		return Database.query(query_stmt);
	}
	
	public static void main(String[] args) throws SQLException, FileNotFoundException, UnsupportedEncodingException 
    {
		Database db = new Database();
//		
		ResultSet rs = db.getColumns("titanic");
//		System.out.println(rs);
		ArrayList<Double> rsArr = printResultSet(rs);
		
//		ResultSet rs = Database.viz_query("titanic", new ArrayList<String>(Arrays.asList("survived")), "id", "COUNT", new ArrayList<String>(Arrays.asList("sex='male'", "age<20")));
//		ArrayList<Double> rsArr = printResultSet(rs);
		
//		ArrayList<String> colArrs = new ArrayList<String>(Arrays.asList("survived","sexcode","pc_class"));
//		ResultSet rs = Database.viz_query("titanic", colArrs, "id", "COUNT", new ArrayList<String>(Arrays.asList()));
//		//rsArr = printResultSet(rs);
//		resultSet2csv(rs,"titanic",new ArrayList<String>(Arrays.asList("survived","sexcode","pc_class")),"COUNT(id)");
		
// 		If compute using all columns then there is only one item for each group 
//		ResultSet cols = Database.getColumns("mushroom");
//		ArrayList<String> colArrs = resultSet2ArrayStr(cols);
//		colArrs.remove("type");
//		rs = Database.viz_query("mushroom", colArrs, "type", "COUNT", new ArrayList<String>(Arrays.asList()));
//		resultSet2csv(rs,"mushroom",colArrs,"COUNT(type)");
		
		// Small Mushroom example
//		ArrayList<String> colArrs = new ArrayList<String>(Arrays.asList("type","cap_shape", "cap_surface" , "cap_color" , "bruises" , "odor"));
//		ResultSet rs = Database.viz_query("mushroom", colArrs, "type", "COUNT", new ArrayList<String>(Arrays.asList()));
//		resultSet2csv(rs,"mushroom",colArrs,"COUNT(type)");
		// All mushroom attributes example 
//		ArrayList<String> colArrs = new ArrayList<String>(Arrays.asList("type","cap_shape",  "cap_color" , "bruises" , "odor","gill_size","gill_color","habitat"));
//		ResultSet rs = Database.viz_query("mushroom", colArrs, "type", "COUNT", new ArrayList<String>(Arrays.asList()));
//		resultSet2csv(rs,"mushroom",colArrs,"COUNT(type)");
//		colArrs = new ArrayList<String>(Arrays.asList( "is_multi_query","is_profile_query","is_event_query","has_impressions_tbl",
//													  "has_clicks_tbl","has_actions_tbl","has_distinct","has_list_fn"));
//		rs = Database.viz_query("turn", colArrs, "slots_millis_reduces", "SUM", new ArrayList<String>(Arrays.asList()));
//		resultSet2csv(rs,"turn",colArrs,"SUM(slots_millis_reduces)");
    }
	
}
