from config import app
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import create_engine
import pandas as pd
import json

#data = pd.read_csv('../data/titanic.csv',index_col=0)
# engine = create_engine("postgresql://summarization:lattice@localhost:5433")
engine = create_engine(app.config['SQLALCHEMY_DATABASE_URI'])
# data.to_sql(name='titanic', con=engine, if_exists = 'replace', index=False)

connection = engine.connect()


def initialize_DB():
  '''
  initialize database, if first time running this, then create engine should check if DB called summarization exist or not
  '''
  raise NotImplementedError
def executeQuery(query):
  #Executes query and returns ResultSet as a dictionary of values
  resultSet = connection.execute(query)
  result = {}
  cols = resultSet.keys()
  resultList = [[] for _col in cols]
  for row in resultSet :
      for i in range(len(cols)):
          resultList[i].append(row[i])
      #col.append(row[colname])
      # return result
  for ci,colname in enumerate(cols):
      result[str(colname)]=resultList[ci]
  return result
def upload_data():
  '''
  User uploads data from frontend using a csv file
  This function uploads the data into Postgres DB

  1) For frontend, look at fileUploader.js and index.html in ZV
  2) Frontend send request to backend after submit the form
  3) Then in this function we take the request, read in as a pandas table
  4) then upload onto the sql table
  '''
  raise NotImplementedError
def get_tables():
  '''
  Get a list of all the tables inside the viz-summarization user
  summarization=# SELECT TABLE_NAME  FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' AND  TABLE_SCHEMA='public' ORDER BY TABLE_NAME;
  table_name
  ------------
  titanic
  (1 row)
  We want to be able to retreive this in the front end so that the uplaod dataset dropdown menu updates dynamically.
  '''
  query = "SELECT TABLE_NAME  FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' AND  TABLE_SCHEMA='public' ORDER BY TABLE_NAME;"
  result = executeQuery(query)
  return result["table_name"]

def get_columns(tablename):
  '''
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
  '''
  query = "SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' AND table_name = " + "'" + tablename + "';"
  result = executeQuery(query)
  return result
def query_vizData(tablename,x_attr,y_attr, agg_func, filters):
  '''
  Constructs a typical query for each visualization
  1) SELECT <agg_func>(<y_attr>) FROM  <tablename> WHERE <filters> GROUPBY <x_attr>
  e.g. SELECT SUM(Population) FROM census WHERE RACE=Asian & GENDER=Female GROUPBY GENDER
  2) Read retreived results and store it as a tuple
  3) return tuples for each bar in the visualization [a1,a2,a3]
  #str = "SELECT" + agg_func + "(" + y_attr + ")" + "FROM" + tablename + "WHERE" + filters + "GROUPBY" + x_attr
  #result = connection.execute(str)
  '''
  if filters is None:
    query = "SELECT " + x_attr + ", " +agg_func +"(" + y_attr + ")" + " FROM " + tablename + " GROUP BY " + x_attr+";"
  else: 
    filter_str = ""
    for idx, val in enumerate(filters):
      if(idx != len(filters)-1):
        filter_str += val
        filter_str += " AND "
      else:
        filter_str += val

    query = "SELECT " + x_attr + ", " +agg_func +"(" + y_attr + ")" + " FROM " + tablename + " WHERE " + filter_str + " GROUP BY " + x_attr+";"
  result = executeQuery(query)
  return result

def findDistinctAttrVal(attribute,tablename):
  #Find the distinct values for this attribute
  query = "SELECT DISTINCT "+ attribute +" FROM "+tablename+" ;"
  return executeQuery(query)

def unit_test():
  print "executeQuery:\n"
  query = "SELECT survived, COUNT(id) FROM titanic WHERE sex='male' AND age<20 GROUP BY survived;"
  executeQuery(query)
  print "get_tables:\n", get_tables()
  print "get_columns:\n", get_columns('titanic')
  print "query_vizData:\n", query_vizData("titanic", "survived", "id", "COUNT", ["sex='male'", "age<20"])
  print "findDistinctAttrVal:\n", findDistinctAttrVal("cap_surface","mushroom")
if __name__=="__main__":
  unit_test()











