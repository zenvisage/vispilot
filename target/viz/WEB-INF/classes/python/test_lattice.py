from lattice import Lattice
from node import vizNode
import networkx as nx
from vizObj import vizObj



# set up the tree example
G = Lattice()
root = vizNode(filters = ["All"])

v1 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
v1.setData([48, 46, 6])
v1.setFilters(root.get_filter())
root.set_viz([v1])

v2 = vizObj()
v2.setX(["Clinton", "Trump", "Others"])
v2.setY("% of vote")
v2.setData([31,62,7])
v2.setFilters(["Race = White", "Gender = M"])


M = vizNode(parents = [root], filters = ["Gender = M"])
F = vizNode(parents = [root], filters = ["Gender = F"])
White = vizNode(parents = [root], filters = ["Race = White"])
Black = vizNode(parents = [root], filters = ["Race = Black"])
root.set_children([M,F,White,Black])

WM = vizNode(parents = [White, M], filters = ["Race = White", "Gender = M"])
WF = vizNode(parents=[White, F], filters=["Race = White", "Gender = F"])
BM = vizNode(parents = [Black, M], filters = ["Race = Black", "Gender = M"])
BF = vizNode(parents=[Black, F], filters=["Race = Black", "Gender = F"])
M.set_children([WM, BM])
F.set_children([WF, BF])
White.set_children([WM, WF])
Black.set_children([BM, BF])

WM.set_viz([v2])


# set up the tree example
G.addMultiNodes([root,M,F,White,Black,WM,WF,BM,BF])



ret = G.getNodes()
ret = set(ret)
l1 = set([root,M,F,White,Black,WM,WF,BM,BF])
r1 = (ret == l1)
print("Test1: AddMultipleNodes ")
print(r1)
print("\n")





G.addNode(1)
G.addNode(root)
ret2 = set(G.getNodes())
l2 = set([root,M,F,White,Black,WM,WF,BM,BF,1])
r2 = (ret2 == l2)
print("Test2: Add a single node ")
print(r2)
print("\n")


G.deleteNode(1)
ret3 = (G.numberOfNodes() == 9)
print("Test3: Delete single node and Check the number of Nodes ")
print(ret3)
print("\n")



G.addEdge(root, M)
G.addEdge(White, Black)
ret4 = G.getEdges()
r4 = (G.numberOfEdges() == 2)
print("Test4: Add one edge and Check the number of edges ")
print(r4)
print("\n")


G.deleteEdge(White,Black)
ret5 = G.numberOfEdges() == 1
print("Test5: Delete one edge and Check the number of edges ")
print(ret5)
print("\n")




r6 = root.get_parent() == []
r7 = root.get_child().sort() == [M,F,White,Black].sort()
print("Test6: Check the get_parent function and get_child function ")
print(r6 and r7)
print("\n")



F.set_parents([M])
r7 = F.get_parent().sort() == [root, M].sort()
t = F.remove_parent(M)
t1 = F.get_parent().sort() == [root].sort()
print("Test7: Check set_parents and remove_parent function ")
print(r7 and t1)
print("\n")


root.set_filters(["lala"])
r8 = root.get_filter() == ["lala"]
root.set_filters(["All"])
r9 = root.get_filter() == ["All"]
print("Test8: Check set_filters and get_filter function ")
print(r8 and r9)
print("\n")




print("Test9: Check get_viz for root ")
print(root.get_viz() == [v1])
print("\n")

print("Test10: Check get_viz for White Male node ")
print(WM.get_viz() == [v2])
print("\n")

print("Test11: Check get_filter for root ")
print(root.get_filter() == v1.filters)
print("\n")

print("Test12: Check get_filter for White Male node ")
print(WM.get_filter() == v2.filters)
print("\n")


for nodes in G.getNodes():
    for child in nodes.get_child():
        G.addEdge(nodes, child)


G = G.graph

#print(G.numberOfEdges())
p=nx.drawing.nx_pydot.to_pydot(G)
p.write_png('example.png')


