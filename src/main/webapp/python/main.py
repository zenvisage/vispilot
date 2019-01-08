from config import *
from flask import render_template
from data import *
from database import *
from vizGeneration import *
from flask import jsonify
from flask_sqlalchemy import SQLAlchemy
from query import Query
from lattice import Lattice
from barchart import bar_chart
import pandas as pd
import json
import glob
from ast import literal_eval
import os 
from datetime import datetime
db = SQLAlchemy(app)

@app.route("/getTreeJSON")
def getTreeJSON():
  (nodeDic, node, edge) = getJsonFromLattice()
  session['nodeDic'] = nodeDic
  session['node'] = node
  session['edge'] = edge
  return nodeDic, node, edge

# @app.route("/getTree")
# def getTree():
#   (treeTreant, nodeDic) = getJsonFromLattice()
#   session['nodeDic'] = nodeDic
#   return nodeDic

@app.route("/getColumns", methods=['POST','GET'])
def getColumns():
  # The proper way of reading from DB (not from JSON outputs)
  # column_name = json.dumps(get_columns(request.form['tablename'])["column_name"])
  metadata = readExperimentJsons()
  column_name = list(set(metadata[metadata["datasetname"]==request.form['tablename']].xAxis))
  print column_name
  session['column_name'] = column_name  # a list containing all the column names
  return jsonify(column_name)
@app.route("/getAvailableFiles", methods=['POST','GET'])
def getAvailableFiles():
  # The proper way of reading from DB (not from JSON outputs)
  # column_name = json.dumps(get_columns(request.form['tablename'])["column_name"])
  metadata = readExperimentJsons()
  available_files={}
  for attr_name in metadata.columns:
    available_files[attr_name]=getUniqueCols(metadata,request.form['tablename'],attr_name)
  session['available_files'] = available_files
  return jsonify(available_files)
def getUniqueCols(metadata,datasetname,attributeName):
  return list(set(metadata[metadata["datasetname"]==datasetname][attributeName]))
@app.route("/getNodeEdgeList", methods=['POST','GET'])
def getNodeEdgeList():
  # Given the nodeDic, compute node list and edge list in Lattice.py, then return them as JS vars.
  if str(request.form["jsonClean"]) =="true":
    nodeDic = literal_eval(json.loads(request.form['nodeDic'].replace('\n', '').decode('string_escape')))
  else:
    nodeDic = json.loads(request.form['nodeDic'])
    
  G = Lattice()

  node = G.generateNode(nodeDic)
  edge = G.generateEdge(nodeDic)
  return jsonify(edge,node)
@app.route("/upload_data", methods=['POST'])
def upload_data():
  import pandas as pd
  from sqlalchemy import create_engine
  data = pd.read_csv("mushrooms.csv")
  engine = create_engine("postgresql://summarization:lattice@localhost:5432")
  data.to_sql(name='mushroom', con=engine, if_exists = 'replace', index=False)

@app.route("/getTables")
def getTables():
  metadata = readExperimentJsons()
  # tableList = get_tables() # The proper way of reading from DB (not from JSON outputs)
  tableList = list(set(metadata["datasetname"]))
  session['tableList'] = tableList
  return tableList

@app.route("/getInterested", methods=['POST'])
def getInterested():
    path = "user_study_"+datetime.strftime(datetime.now(),"%Y_%m_%d")+".log"
    f = open(path,'a')
    f.write(datetime.strftime(datetime.now(),"%Y-%m-%d-%H-%M-%S")+","+\
            str(request.form['user'])+","+request.form['task']+","+\
            request.form['fname']+","+str(request.form['interested'])+"\n")
    f.close()
    return ('', 204)

@app.route("/getBarchart", methods=['POST'])
def getBarchart():
    yVals = request.form['yVals']
    xAttrs = request.form['xAttrs']
    title = request.form['title']
    yName = request.form['yName']
    xName = request.form['xName']
    yVals = yVals.replace('[','').replace(']','')
    yVals = [float(s) for s in yVals.split(',')]
    xAttrs = xAttrs.replace('[', '').replace(']', '').replace('"','')
    xAttrs = [s for s in xAttrs.split(',')]
    return bar_chart(yVals, xAttrs, xtitle=xName, ytitle=yName, title=title, top_right_text="", N=1, width=0.1)


@app.route("/postQuery", methods=['GET', 'POST'])
def postQuery():
    dataset = request.form['dataset']
    xAxis = request.form['xAxis']
    yAxis = request.form['yAxis']
    aggFunc = request.form['aggFunc']
    filters = str(request.form['filters'])
    method = request.form['method']
    query = Query(dataset,xAxis,yAxis,aggFunc,filters,method)
    # run create lattice code 
    return jsonify({"results":"test"})

def readExperimentJsons():
    #read the available experiment jsons and get a list of available experiment parameters that to show on the frontend
    metadata = []
    for fname in glob.glob("static/generated_dashboards/*"):
        datasetname, xAxis,algo,dist,ic,ip,k = fname[:-5].split("/")[-1].split("_")
        ic = float(ic[2:])
        ip = float(ip[2:])
        k = int(k[1:])
        metadata.append([datasetname, xAxis,algo,dist,ic,ip,k])
    metadata =pd.DataFrame(metadata,columns=["datasetname","xAxis","algo","dist","ic","ip","k"])
    return metadata 
@app.route("/", methods=['GET', 'POST'])
def index():
    '''
    (ret, nodeDic) = getJsonFromLattice()
    table_name = get_tables()  # a list containing all the table names
    column_name = get_columns("titanic") # a list containing all the column names

    print "ret: "
    '''
    # all_tables = getTables()
    # dummy example
    #nodeDic, node, edge= getTreeJSON()

    # column_name = [""]
    # select_table_name = str(request.form.get('table_select'))
    # session['select_table_name'] = select_table_name
    # if session.get('select_table_name', None) is not None:
    #   column_name = getColumns(session.get('select_table_name', None))
    # #if(session.get('select_table_name', None) is not None) and (len(session.get('filter_list', None)) != 0) and (session.get('select_yaxis', None) is not None) and (session.get('select_aaxis', None) is not None) and (session.get('select_avg_name', None) is not None):
    #   #query_vizData(session.get('select_table_name', None), session.get('select_aaxis', None), session.get('select_yaxis', None), session.get('select_avg_name', None), session.get('filter_list', None))

    # generateVizObj("titanic", "survived", "id", "COUNT", ["sex='male'", "age<20"])

    # return render_template("main.html", treeTreant2 = treeTreant, all_tables = all_tables,\
    #                         column = json.loads(column_name), nodeDic = nodeDic)
    #return render_template("main.html", treeTreant2 = treeTreant, all_tables = all_tables,\
    #                        nodeDic = nodeDic)
    return render_template("main.html")#, all_tables = all_tables)#, nodeDic = nodeDic, node = node, edge = edge)

if __name__ == "__main__":

    app.debug = True
    app.run()
