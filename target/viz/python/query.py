import ast
from database import * 
class Query:
    def __init__(self,dataset,xAxis,yAxis,aggFunc,filters,method):
        self.dataset = dataset
        self.xAxis = xAxis
        self.yAxis = yAxis
        self.aggFunc = aggFunc
        self.filters = ast.literal_eval(filters) # string split to lists
        self.method = method #What you want to do with this
        self.whereClause = self.filter2Where()
    def filter2Where(self):
        whereClause = ""
        if len(self.filters)<=0:
            return whereClause
        for condition in self.filters[:-1]:
          whereClause+= condition+" AND "
        whereClause+=self.filters[-1]
        return whereClause
    def execute(self):
    	return query_vizData(self.dataset,self.xAxis,self.yAxis, self.aggFunc, self.filters)
    def __repr__(self):
    	# query = "SELECT " + x_attr + ", " +agg_func +"(" + y_attr + ")" + " FROM " + tablename + " WHERE " + filter_str + " GROUP BY " + x_attr
    	return "<{}: {}, x={}, {}({}) WHERE {}>".format(self.method,self.dataset,self.xAxis,self.aggFunc,self.yAxis,self.whereClause)
  