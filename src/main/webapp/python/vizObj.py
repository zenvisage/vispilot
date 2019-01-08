import networkx as nx
from database import *

class vizObj:
    '''
    A vizObj contains the data values for generating the visualization.
    '''
    def __init__(self, x, y,filters,agg_func,tablename):
        self.X = x #strings
        self.Y = y
        self.filters = filters # List of constraints, it is the same as the filters in parents. [("gender","female")]
        self.agg_func = agg_func # aggregation function
        self.tablename = tablename
        self.data = []#self.compute_data() #list of data values y1,y2...
        self.expectation = self.get_expectation('NULL')
        
    def compute_data(self):
        # return query_vizData(self.tablename, self.X, self.Y, self.agg_func, self.filters)[self.agg_func.lower()]
        return query_vizData(self.tablename, self.X, self.Y, self.agg_func, self.filters)

    def get_expectation(self,type):
        # models for generating user expectation
        return 0

    # TODO
    def __repr__(self):
        return '(X: {}, Y: {}, filters: {}, data: {})'.format(self.X, self.Y, self.filters, self.data)

    def setX(self, x):
        self.X =x 

    def setY(self, y):
        self.Y = y

    def setFilters(self, constraints):
        self.filters = constraints

    def setAgg(self, agg):
        self.agg_func = agg

    def setData(self, data):
        self.data = data