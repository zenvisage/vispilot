var dic = [];
var subpop_dic = [];
var root_pop = 0;
var group_dic = [];

async function generateNode(nodeDicStr,callback){
    console.log("start svg");
    dic = [];
    var nodelist = [];
    nodeDicStr = JSON.parse(nodeDicStr);

    for(i in nodeDicStr){
        var node = nodeDicStr[i];
        //console.log(node);
        var yVals = [];
        var xAttrs = [];
        var collapse = [];
        var filterVal;
        var xname;
        var yname;
        var subpop;
        var group_num;

        for(values in node){
            if(node[values]['yAxis']!=null)
                yVals.push(node[values]['yAxis']);
            if(node[values]['xAxis']!=null)
                xAttrs.push(node[values]['xAxis']);
            if(node[values]["filter"]!=null){
                dic[i]=  node[values]["filter"];
                
                if(node[values]["filter"]=="#"){
                    filterVal="overall";
                    root_pop =node[values]['populationSize'];
                }
                else if(node[values]["filter"]=="collapsed")
                    filterVal="collapsed"
                else
                    filterVal = String(node[values]["filter"].split("#").join(", ").split("$").join("=")).slice(1, -2);
            }
            if(node[values]['populationSize']!=null)
                subpop_dic[i]=node[values]['populationSize'];
            if(node[values]['xName']!=null)
                xname = node[values]['xName'];
            if(node[values]['yName']!=null)
                yname = node[values]['yName'];
            if(node[values]['collapse']!=null)
                collapse.push(node[values]['collapse']);
            if(root_pop != 0)
                group_dic[i] = Math.round((node[values]['populationSize'] / root_pop) / 0.12) ;
        }
        console.log(filterVal);
        /*console.log(yVals);
        console.log(xAttrs);
        console.log(filterVal);
        console.log(xname);
        console.log(yname);
        console.log(collapse);*/
        //var vlSpec = bar_chart();
        
        //Vega Lite features
        vlSpec = {};
        vlSpec['$schema'] = "https://vega.github.io/schema/vega-lite/v3.json";
        vlSpec['width'] = 120;
        vlSpec['height'] = 120;
        vlSpec['title'] = filterVal;
        vlSpec['data'] = {};
        vlSpec['data']['values'] = [];
        for(i in xAttrs){
            vlSpec['data']['values'].push({});
            vlSpec['data']['values'][i][xname] = xAttrs[i];
            // vlSpec['data']['values'][i][yname] = Math.max( Math.round(yVals[i] * 10) / 10, 2.8 ).toFixed(2);
            vlSpec['data']['values'][i][yname] = yVals[i].toFixed(2);
        }
        vlSpec['config'] = {
            "text":{"color":"black"},
            "view": {
             "stroke": "transparent"
          }
        };
        vlSpec['encoding'] = {
            "x": {"field": xname, "type": "ordinal", "axis":{"title":false}},
            "y": {"field": yname, "type": "quantitative", "scale":{"domain": [0,100]},"axis": {"title":false, "grid":false}}, 
            "color":{
              "legend": null,
              "field": xname,
              "scale":{
                "name": "color",
                "type": "ordinal",
                "range": ["#EF9242", "#7CD485","#9FC0F0", "plum","teal", "#F68B8B", "DarkOrchid"]
              }
            }
          };
        vlSpec['layer'] = [{
            "mark": "bar"
          }, {
            "mark": {
              "type": "text", 
              "align": "center", 
              "center": "middle", 
              "dx": 3, 
              "dy": -4,
              "color": "black",
              "fontSize": 13
            },
            "encoding": {
              "text": {"field": yname, "type": "quantitative", "fontSize":20}
            }
          }];
    
        console.log(vlSpec);
        
        var vgSpec = vl.compile(vlSpec, {}).spec;
        var view = new vega.View(vega.parse(vgSpec)).renderer('none').initialize();
        var svgString =  (view.toSVG()).then(function(result) {
            //console.log(filterVal);
            nodelist.push({"id": parseInt(i), "group": 0, "filterVal":filterVal,"collapse":collapse,"image": "data:image/svg+xml;base64," + btoa(result), "shape": 'image'});
            if (nodelist.length == Object.keys(nodeDicStr).length){//$("#kInputId").val()
                //Finish generating svg for all nodes
                console.log("finish svg");
                var cur = 0;
                for(idx in nodeDicStr){
                    nodelist[cur].id = parseInt(idx);
                    cur++;
                }
                console.log(nodelist);
                callback(nodelist);
            }
         }).catch(function(err) { console.error(err); });
        //console.log(t);
    }
}
var child_dic = []; 
function generateEdge(nodeDicStr){
    child_dic = []; 
    var edgelist = [];
    nodeDicStr = JSON.parse(nodeDicStr);
    
    if(Object.keys(nodeDicStr).length>0){
        var nBars = (Object.values(nodeDicStr)[0]).length -1;
        for( key in (nodeDicStr)){
            //console.log(key);
            if(nodeDicStr[key][nBars]['childrenIndex'].length>0){
                child_dic[key] = nodeDicStr[key][nBars]['childrenIndex'];
                for(i in nodeDicStr[key][nBars]['childrenIndex']){
                    var child = nodeDicStr[key][nBars]['childrenIndex'][i];
                    //console.log(child);
                    edgelist.push({"from": parseInt(key), "to": parseInt(child), "length": 200, "arrows":'to'});
                }
            }
        }
    }
    
    return edgelist;
}
var old_dic;

function getNodeEdgeListThenDraw(nodeDicStr){
    old_dic = nodeDicStr;
    nodeDicStr = nodeDicStr.split("\\").join("");
    console.log(nodeDicStr);
//    nodeDicStr = JSON.stringify(nodeDicStr);
//    console.log(nodeDicStr);
    
    var edgelist = generateEdge(nodeDicStr);
    // var nodelist = generateNode(nodeDicStr, function(x){draw(x,edgelist)});
    generateNode(nodeDicStr, function(x){draw(x,edgelist)});
    // console.log("edgeList:")
    // console.log(edgelist)
    // console.log("nodeList:")
    // console.log(nodelist)
    // draw(nodelist, edgelist);
}


function get_subpop(nodeID){
    var s = subpop_dic[nodeID];
    return s;
}

function buildGroup(node){
    //console.log("!!!!!!!!!!!!!!!");
    //console.log(node);
    for(i in node){
        //console.log(node[i]);
        node[i]["group"] = group_dic[node[i]["id"]];
    }
    return node;
}
//Main draw function
function Draw(){
    //console.log(document.getElementById('agg').value)
    //document.getElementById("canvas-graph").style.display = "none"
    document.getElementById("loadingDashboard").style.display = "inline"
    $.ajax({
        type: "POST",
        url: "viz/draw",
        async: false,
        data: {
            "datasetName" : JSON.stringify(document.getElementById('data').value),
            "yAxis" : JSON.stringify(document.getElementById('y').value),
            "xAxis" : JSON.stringify(document.getElementById('x').value),
            "aggType" : JSON.stringify(document.getElementById('agg').value),
            "k" : document.getElementById('kOutputId').value,
            "ic" : 0,
            "info" : 0.9
        },
        success: function(data) {
                console.log(data);
                getNodeEdgeListThenDraw(data);
                document.getElementById("loadingDashboard").style.display = "none";
            }
        
    })
}
