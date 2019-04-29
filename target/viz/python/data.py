from lattice import Lattice
from node import vizNode
import networkx as nx
from vizObj import vizObj
from collections import OrderedDict
import json

def getJsonFromLattice():
    # set up the tree example
    G = Lattice()
    v1 = vizObj(x=["Clinton", "Trump", "Others"],y="% of vote",filters=["All"],\
                agg_func="SUM",tablename="election")
    v1.setData([48, 46, 6])
    root = vizNode(viz=v1, parents=None)

    v2 = vizObj(x=["Clinton", "Trump", "Others"],y="% of vote",filters=["Race = White"],\
                agg_func="SUM",tablename="election")
    v2.setData([31, 62, 7])
    W = vizNode(viz=v2,parents=[root])

    v3 = vizObj(x=["Clinton", "Trump", "Others"],y="% of vote",filters=["Gender = F"],\
                agg_func="SUM",tablename="election")
    v3.setData([21, 70, 9])
    F = vizNode(viz=v3,parents=[root])

    v4 = vizObj(x=["Clinton", "Trump", "Others"], y="% of vote", filters=["Color = Blue"], \
                agg_func="SUM", tablename="election")
    v4.setData([21, 52, 27])
    B = vizNode(viz=v4, parents=[W])

    v5 = vizObj(x=["Clinton", "Trump", "Others"], y="% of vote", filters=["Job = Student"], \
                agg_func="SUM", tablename="election")
    v5.setData([20, 30, 50])
    J = vizNode(viz=v5, parents=[W, B])
    # set up the tree example
    for nodes in G.getNodes():
        for child in nodes.get_child():
            G.addEdge(nodes, child)
    G.addMultiNodes([root,W,F,B,J])
    root.set_children([W,F])
    W.set_children([B])
    W.set_children([J])
    B.set_children([J])


    nodeDic = G.generateNodeDic()
    #nodeDic = "{\"1\": [{ \"xAxis\": \"0\", \"yAxis\":43.40789274938343},{ \"xAxis\": \"1\", \"yAxis\":56.59210725061657},{\"childrenIndex\":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12], \"populationSize\":492526704188, \"filter\":\"#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"2\": [{ \"xAxis\": \"0\", \"yAxis\":99.09927735409117},{ \"xAxis\": \"1\", \"yAxis\":0.9007226459088256},{\"childrenIndex\":[13, 28, 30, 34], \"populationSize\":65236316603, \"filter\":\"#is_profile_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"3\": [{ \"xAxis\": \"0\", \"yAxis\":99.09927735409117},{ \"xAxis\": \"1\", \"yAxis\":0.9007226459088256},{\"childrenIndex\":[17, 28, 38, 43], \"populationSize\":65236316603, \"filter\":\"#is_event_query$1#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"4\": [{ \"xAxis\": \"0\", \"yAxis\":98.84352038584385},{ \"xAxis\": \"1\", \"yAxis\":1.156479614156162},{\"childrenIndex\":[30, 38, 48], \"populationSize\":55050620539, \"filter\":\"#has_impressions_tbl$1#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"5\": [{ \"xAxis\": \"0\", \"yAxis\":87.72514337314605},{ \"xAxis\": \"1\", \"yAxis\":12.27485662685395},{\"childrenIndex\":[25, 35, 41, 46, 50, 52], \"populationSize\":115838325255, \"filter\":\"#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"6\": [{ \"xAxis\": \"0\", \"yAxis\":73.54175531539768},{ \"xAxis\": \"1\", \"yAxis\":26.458244684602324},{\"childrenIndex\":[64, 97, 102, 105, 107], \"populationSize\":53156154309, \"filter\":\"#is_profile_query$1#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"7\": [{ \"xAxis\": \"0\", \"yAxis\":73.54175531539768},{ \"xAxis\": \"1\", \"yAxis\":26.458244684602324},{\"childrenIndex\":[73, 97, 111, 115, 117], \"populationSize\":53156154309, \"filter\":\"#is_event_query$0#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"8\": [{ \"xAxis\": \"0\", \"yAxis\":29.77955596154781},{ \"xAxis\": \"1\", \"yAxis\":70.22044403845219},{\"childrenIndex\":[26, 27, 36, 42, 47, 51, 53], \"populationSize\":376688378933, \"filter\":\"#has_distinct$1#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"9\": [{ \"xAxis\": \"0\", \"yAxis\":30.47638712866313},{ \"xAxis\": \"1\", \"yAxis\":69.52361287133687},{\"childrenIndex\":[15, 18, 20, 22, 24, 27], \"populationSize\":82477942992, \"filter\":\"#is_multi_query$1#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"10\": [{ \"xAxis\": \"0\", \"yAxis\":19.748564939482314},{ \"xAxis\": \"1\", \"yAxis\":80.25143506051768},{\"childrenIndex\":[66, 76, 83, 88, 91], \"populationSize\":68383077780, \"filter\":\"#is_multi_query$1#has_distinct$1#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"11\": [{ \"xAxis\": \"0\", \"yAxis\":77.05061337960112},{ \"xAxis\": \"1\", \"yAxis\":22.949386620398883},{\"childrenIndex\":[81, 120, 122], \"populationSize\":61495053848, \"filter\":\"#has_impressions_tbl$0#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"12\": [{ \"xAxis\": \"0\", \"yAxis\":20.596887099254975},{ \"xAxis\": \"1\", \"yAxis\":79.40311290074501},{\"childrenIndex\":[56, 58, 60, 62], \"populationSize\":71779638383, \"filter\":\"#is_multi_query$1#is_profile_query$1#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"13\": [{ \"xAxis\": \"0\", \"yAxis\":20.596887099254975},{ \"xAxis\": \"1\", \"yAxis\":79.40311290074501},{\"childrenIndex\":[56, 68, 70, 72], \"populationSize\":71779638383, \"filter\":\"#is_multi_query$1#is_event_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"14\": [{ \"xAxis\": \"0\", \"yAxis\":21.13295807883538},{ \"xAxis\": \"1\", \"yAxis\":78.86704192116461},{\"childrenIndex\":[78, 80], \"populationSize\":72696207193, \"filter\":\"#is_multi_query$1#has_impressions_tbl$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"15\": [{ \"xAxis\": \"0\", \"yAxis\":21.933813158690548},{ \"xAxis\": \"1\", \"yAxis\":78.06618684130945},{\"childrenIndex\":[85], \"populationSize\":73168015971, \"filter\":\"#is_multi_query$1#has_clicks_tbl$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"16\": [{ \"xAxis\": \"0\", \"yAxis\":34.90524231845271},{ \"xAxis\": \"1\", \"yAxis\":65.09475768154729},{\"childrenIndex\":[14, 15, 29, 31, 32, 33], \"populationSize\":427290387585, \"filter\":\"#is_profile_query$1#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"17\": [{ \"xAxis\": \"0\", \"yAxis\":34.90524231845271},{ \"xAxis\": \"1\", \"yAxis\":65.09475768154729},{\"childrenIndex\":[16, 18, 29, 37, 39, 40], \"populationSize\":427290387585, \"filter\":\"#is_event_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"18\": [{ \"xAxis\": \"0\", \"yAxis\":79.8957702319091},{ \"xAxis\": \"1\", \"yAxis\":20.10422976809091},{\"childrenIndex\":[86, 124], \"populationSize\":70437357911, \"filter\":\"#has_clicks_tbl$0#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"19\": [{ \"xAxis\": \"0\", \"yAxis\":22.863232070479246},{ \"xAxis\": \"1\", \"yAxis\":77.13676792952076},{\"childrenIndex\":[], \"populationSize\":74047346293, \"filter\":\"#is_multi_query$1#has_actions_tbl$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}],\"20\": [{ \"xAxis\": \"0\", \"yAxis\":36.43204694793704},{ \"xAxis\": \"1\", \"yAxis\":63.56795305206296},{\"childrenIndex\":[19, 20, 44, 45], \"populationSize\":437476083649, \"filter\":\"#has_impressions_tbl$0#\",\"yName\":\"SUM(slots_millis_reduces)\"}]}"
    #nodeDic = json.loads(nodeDic.replace('\n', '').decode('string_escape'))
    print "nodeDic:"
    print nodeDic
    node = G.generateNode(nodeDic)
    edge = G.generateEdge(nodeDic)
    #print(ret)
    ret2 = '<svg xmlns="http://www.w3.org/2000/svg" width="480" height="260"><g transform="translate(50,30)"><rect x="0" y="0" width="100%" height="100%" fill="#ffffff"></rect><rect x="14" y="0" width="105" height="200" fill="steelblue" class="Clinton" id="48"></rect><rect x="139" y="8.333333333333343" width="105" height="191.66666666666666" fill="steelblue" class="Trump" id="46"></rect><rect x="264" y="175" width="105" height="25" fill="steelblue" class="Others" id="6"></rect><g class="axis" transform="translate(-10, 0)"><g transform="translate(0,200)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">0</text></g><g transform="translate(0,158.33333333333334)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">10</text></g><g transform="translate(0,116.66666666666667)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">20</text></g><g transform="translate(0,75)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">30</text></g><g transform="translate(0,33.33333333333334)" style="opacity: 1;"><line class="tick" x2="-6" y2="0"></line><text x="-9" y="0" dy=".32em" style="text-anchor: end;">40</text></g><path class="domain" d="M-6,0H0V200H-6"></path></g><g class="axis" transform="translate(0,210)"><g transform="translate(70.5,0)" style="opacity: 1;"><line class="tick" y2="6" x2="0"></line><text y="9" x="0" dy=".71em" style="text-anchor: middle;">Clinton</text></g><g transform="translate(195.5,0)" style="opacity: 1;"><line class="tick" y2="6" x2="0"></line><text y="9" x="0" dy=".71em" style="text-anchor: middle;">Trump</text></g><g transform="translate(320.5,0)" style="opacity: 1;"><line class="tick" y2="6" x2="0"></line><text y="9" x="0" dy=".71em" style="text-anchor: middle;">Others</text></g><path class="domain" d="M0,6V0H390V6"></path></g><text transform="translate(-30, -20)">% of vote</text><text x="195" y="-8px" text-anchor="middle" style="font-size: 16px; text-decoration: underline;">All</text></g></svg>'
    return (nodeDic, node, edge)
    # M = vizNode(parents=[root], filters=["Gender = M"])
    # F = vizNode(parents=[root], filters=["Gender = F"])
    # White = vizNode(parents=[root], filters=["Race = White"])
    # Black = vizNode(parents=[root], filters=["Race = Black"])

    # WM = vizNode(parents=[White, M], filters=["Race = White", "Gender = M"])
    # WF = vizNode(parents=[White, F], filters=["Race = White", "Gender = F"])
    # BM = vizNode(parents=[Black, M], filters=["Race = Black", "Gender = M"])
    # BF = vizNode(parents=[Black, F], filters=["Race = Black", "Gender = F"])

    # G.addMultiNodes([root, M, F, White, Black, WM, WF, BM, BF])
    # root.set_children([M, F, White, Black])
    # M.set_children([WM, BM])
    # F.set_children([WF, BF])
    # White.set_children([WM, WF])
    # Black.set_children([BM, BF])

    # WM.set_viz([v2])

    # v3 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v3.setData([41, 52, 7])
    # v3.setFilters(M.get_filter())
    # M.set_viz([v3])

    # v4 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v4.setData([54, 41, 5])
    # v4.setFilters(F.get_filter())
    # F.set_viz([v4])

    # v5 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v5.setData([37, 57, 6])
    # v5.setFilters(White.get_filter())
    # White.set_viz([v5])

    # v6 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v6.setData([89, 8, 3])
    # v6.setFilters(Black.get_filter())
    # Black.set_viz([v6])

    # v7 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v7.setData([43, 52, 5])
    # v7.setFilters(WF.get_filter())
    # WF.set_viz([v7])

    # v8 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v8.setData([82, 13, 5])
    # v8.setFilters(BM.get_filter())
    # BM.set_viz([v8])

    # v9 = vizObj(["Clinton", "Trump", "Others"], "% of vote")
    # v9.setData([94, 4, 2])
    # v9.setFilters(BF.get_filter())
    # BF.set_viz([v9])

    