from lattice import Lattice
from vizObj import vizObj
import networkx as nx


class vizNode:

    '''
    A Node object contains a list of vizObj (data objects)

    '''
    def __init__(self,viz,parents):
        self.vizObj = viz
        self.id = 0
        self.children=[]
        # self.children = children
        self.parents = parents
        self.childrenIndex = [] #index of all the children ids 
        self.filters = viz.filters

    def __repr__(self):
         return str(self.filters)
        # return '(Filters: %s, vizObjs: %s)' % (self.filters, self.vizObj)

    def get_parent(self):
        return self.parents

    # def get_child(self):
    #     return self.children

    def get_viz(self):
        return self.vizObj

    def get_filter(self):
        return self.filters

    def set_parents(self, parent):
        self.parents = parent

    def set_children(self, children):
        self.children = children
        for each in children:
            self.childrenIndex.append(each.id)


    def set_children_index(self, index):
        self.childrenIndex = index

    def set_viz(self, viz):
        self.vizObj = viz

    def set_filters(self, filters):
        self.filters = filters

    # def remove_child(self, child):
    #     for each in self.children:
    #         if each == child:
    #             self.children.remove(each)
    #             break

    def remove_parent(self, parent):
        for each in self.parents:
            if each == parent:
                self.parents.remove(each)
                break


    def __repr__(self):
        return "<vizNode "+self.vizObj.__repr__()+">"

