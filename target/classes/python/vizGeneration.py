# This file contains code that constructs the visualization node and lattices
from database import *
from vizObj import *
from node import *
from lattice import *

def generateVizObj(tablename,x_attr,y_attr, agg_func, filters):
	v = vizObj()
	# 	summarization=# SELECT survived, COUNT(id)  FROM titanic WHERE sex='male' AND age<20 GROUP BY survived;
	#  survived | count
	# ----------+-------
	#         0 |    46
	#         1 |    27
	# (2 rows)
	(xVals, yVals) = query_vizData(tablename,x_attr,y_attr, agg_func, filters)
	v.setX(xVals)
	v.setData(yVals) 
	v.setY(agg_func+'('+y_attr+')') 
	v.setFilters(filters)
	v.setAgg(agg_func)
	return v  

def buildCondition(attribute,value):
    return str(attribute +"= '"+ value+"'")

def level(nodeFilter):
    # determine which level a filter lies in
    return len(nodeFilter) - nodeFilter.count('*')
def arr2List(lst):
    return [list(_x) for _x in lst]
def findNodeWithFilter(G,filter):
    # return the node with that filter
    for vnode, val in G.graph.node.iteritems():
        if val["filters"]==filter:
            return vnode
import itertools
from itertools import combinations
def generateLattice(xAttr,yAttr,aggrFunc,attributes,tablename,MAX_DEPTH=-1,DEBUG=False):
    '''
    Given a list of categorical attributes, automatically generate 
    lattice structure with appropriate parent child relationship. 
    '''
    if MAX_DEPTH==-1:
        MAX_DEPTH=len(attributes)
    assert MAX_DEPTH<=len(attributes)

    #1. determine all possible filters from given attribute names
    filters = {}
    for attr in attributes: 
        filters.update(findDistinctAttrVal(attr,tablename))
    conditions = [[] for _i in filters.keys()]
    for i,attr in enumerate(filters.keys()):
        for val in filters[attr]:
            conditions[i].append(buildCondition(attr,val))
        conditions[i].append("*")
    # 2. Generate all possible filter combinations
    node_filters=[]
    for combination in itertools.product(*conditions):
        node_filters.append(combination)   
    node_filters_levels = np.array([level(nf) for nf in node_filters])
    # Cleaning out "*" for "all"
    node_filters = np.array([list(filter(lambda x: x != '*', a)) for a in node_filters])
    # 3. Assign parent child relationships of each node (corresponding to a filter) to generate the lattice
    # Base Case: Starting from root --> level 1
    idx = np.where(node_filters_levels==1)[0]
    prev_level_filters=node_filters[idx]
    G = Lattice()
    vobj = vizObj(x=xAttr,y=yAttr,filters=None,agg_func='COUNT',tablename=tablename)
    root = vizNode(vobj,None)
    G.addNode(root)
    for nf in node_filters[idx]:
        vobj= vizObj(x=xAttr,y=yAttr,filters=list(nf),agg_func='COUNT',tablename=tablename)
        vnode = vizNode(viz=vobj,parents=[root])
        G.addNode(vnode)

    if DEBUG: 
        plt.figure()
        plt.title("Level 1")
        nx.draw(G.graph)
        plt.savefig("graph1.png")
    # Level n >2 
    for level_i in range(2,MAX_DEPTH+1):
        idx = np.where(node_filters_levels==level_i)[0]
        level_n_filters=  node_filters[idx]
        for nf in level_n_filters:
            vobj= vizObj(x=xAttr,y=yAttr,filters=nf,agg_func='COUNT',tablename=tablename)
            parent_lst = []
            for f in nf:
                if f!="*":
                    parent_idx = np.where([f in x for x in  prev_level_filters ])[0]
                    parent_nodes = [findNodeWithFilter(G,str(f)) for f in prev_level_filters[parent_idx]]
                    parent_lst.extend(parent_nodes)
            vnode = vizNode(viz=vobj,parents=parent_lst)
            G.addNode(vnode)
        if DEBUG:
            plt.figure()
            plt.title("Level  {}".format(level_i))
            nx.draw(G.graph)
            plt.savefig("graph{}.png".format(level_i))
    return G