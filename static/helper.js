function constructQueryWithArgs(dataset,xAxis,algorithm,metric,ic,ip,k){
    query = {
        "dataset": dataset,
        "xAxis": xAxis,
        "algorithm": algorithm,
        "metric": metric,
        "ic":parseFloat(ic).toFixed(1),
        "ip": ip,
        "k": k,
        "method": "query"
    }
    console.log(ic)
    readDashboardOutput(query)
    return query
}
function constructQuery(){
    query = {
        "dataset": $("#all_tables").val() || "",
        "xAxis": $("#xaxis").val() || "",
        "yAxis": $("#yaxis").val() || "",
        "aggFunc": $("input[name='aggFunc']:checked").val() || "",
        "algorithm": $("#algorithm").val() || "",
        "metric": $("#metric").val() || "",
        "filters": JSON.stringify(filters) ,
        "ic":parseFloat($("#ic").val()).toFixed(1)|| "",
        "ip":parseFloat($("#ip").val()).toFixed(1)|| "",
        "k":$("#k").val()|| "",
        "method": "query"
    }
    readDashboardOutput(query)
    return query
}
var tableChecked = {};
var chartarray;
function readDashboardOutput(query){
    fname = query["dataset"]+"_"+query["xAxis"]+"_"+query["algorithm"]
            +"_"+query["metric"]+"_ic"+query["ic"]+"_ip"+query["ip"]+"_k"+query["k"]+".json"
    //Determine which canvas is currently on
    console.log(fname)
    readDashboardFile(fname)
}
function readDashboardFile(fname){
    var nodeDic = ""
    var json_pathloc = "http://"+window.location.hostname+":"+window.location.port+"/generated_dashboards/"+fname
    if(layout=="table"){
        document.getElementById('canvas-graph').style.display = 'none';
        document.getElementById('canvas-table').style.display = '';
        document.getElementById('charttable').style.display = '';

        document.getElementById('right-sidebar-graph').style.display = 'none';
        document.getElementById('right-sidebar-table').style.display = '';

        document.getElementById('selected').innerHTML = '';
        document.getElementById('notselected').innerHTML = '';
        $.ajax({
            url: json_pathloc,
            type: "GET",
            dataType: "text",
            success: function(data) {
                data = data.replace(/\\"/g, '"')

                //console.log(data)
                //data = ('{\"0\": [{ \"xAxis\": \"0\", \"yAxis\":8901.330244122693},{ \"xAxis\": \"1\", \"yAxis\":11982.699453217303},{\"childrenIndex\":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14], \"populationSize\":2147483647, \"filter\":\"#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"1\": [{ \"xAxis\": \"0\", \"yAxis\":2617.150014320924},{ \"xAxis\": \"1\", \"yAxis\":24.329102935422725},{\"childrenIndex\":[15, 17, 39, 41, 45, 49, 53, 54], \"populationSize\":2147483647, \"filter\":\"#is_profile_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"2\": [{ \"xAxis\": \"0\", \"yAxis\":2522.4267606727904},{ \"xAxis\": \"1\", \"yAxis\":6.6490602244851456},{\"childrenIndex\":[225], \"populationSize\":2147483647, \"filter\":\"#is_profile_query$0#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"3\": [{ \"xAxis\": \"0\", \"yAxis\":2209.5112668860293},{ \"xAxis\": \"1\", \"yAxis\":10.67315699098313},{\"childrenIndex\":[97, 125], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$0#is_profile_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"4\": [{ \"xAxis\": \"0\", \"yAxis\":2209.5112668860293},{ \"xAxis\": \"1\", \"yAxis\":10.67315699098313},{\"childrenIndex\":[], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$0#is_profile_query$0#is_event_query$1#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"5\": [{ \"xAxis\": \"0\", \"yAxis\":2522.4267606727904},{ \"xAxis\": \"1\", \"yAxis\":6.6490602244851456},{\"childrenIndex\":[], \"populationSize\":2147483647, \"filter\":\"#is_profile_query$0#is_event_query$1#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"6\": [{ \"xAxis\": \"0\", \"yAxis\":1024.4670902958453},{ \"xAxis\": \"1\", \"yAxis\":2487.6941747440464},{\"childrenIndex\":[17, 18, 21, 22, 25, 29, 33, 37, 38], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$1#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"7\": [{ \"xAxis\": \"0\", \"yAxis\":459.1386720813525},{ \"xAxis\": \"1\", \"yAxis\":95.66689422152325},{\"childrenIndex\":[131, 161, 185, 201, 209], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$1#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"8\": [{ \"xAxis\": \"0\", \"yAxis\":7876.863153826848},{ \"xAxis\": \"1\", \"yAxis\":9495.005278473256},{\"childrenIndex\":[16, 19, 23, 27, 31], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"9\": [{ \"xAxis\": \"0\", \"yAxis\":407.6387474348949},{ \"xAxis\": \"1\", \"yAxis\":13.655945944439596},{\"childrenIndex\":[99, 105, 113, 121, 129], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$1#is_profile_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}]}')
                chartarray = JSON.parse(data)

                console.log(chartarray)

                var len = Object.keys(chartarray).length;
                var rownum = len/5 ;
                var colnum = 5
                var table = document.getElementById('charttable'), tr, td, row, cell;
                table.innerHTML = ''
                tableChecked = {};
                for (var i = 0; i < len; i++){
                    tableChecked[i] = 0;
                }
                for (row = 0; row < rownum; row++) {
                    tr = document.createElement("tr");
                    tr.style = "border: 1px solid LightGray;border-collapse: separate"
                    for (cell = 0; cell < colnum; cell++) {
                        var tdId = 'td' + (row * colnum + cell).toString();
                        td = document.createElement('td');
                        td.setAttribute('id',tdId);
                        //td.setAttribute('height', 20%);
                        tr.appendChild(td);
                        td.style = "border: 5px solid grey;border-collapse: separate; align='center' "

                        td.innerHTML = /*'<p style = "font-size:10px;color:#787878">'+(row * colnum + cell).toString()+'</p>'*/'<div id="c' +(row * colnum + cell).toString()+'" style = "border-collapse: separate;" onclick="showfilter(this)"></div>'
                    }
                    table.appendChild(tr);
                }
                console.log(len)
                var table = document.getElementById("charttable");

                var cell_idx = 0;
                for(cell_idx = 0; cell_idx < len; cell_idx++){
                    //render_chart(cell_idx,chartarray)
                    var svgString = generateSVG(cell_idx,chartarray)
                    var cellid = 'c'+ cell_idx.toString();
                    var currentcell = document.getElementById(cellid);
                    //console.log(svgString)
                    currentcell.innerHTML = svgString
                    }
                }
        })
    }
    else{
        document.getElementById('canvas-graph').style.display = '';
        document.getElementById('canvas-table').style.display = 'none';
        document.getElementById('charttable').style.display = 'none';

        document.getElementById('right-sidebar-graph').style.display = '';
        document.getElementById('right-sidebar-table').style.display = 'none';

        document.getElementById('selected').innerHTML = '';
        document.getElementById('notselected').innerHTML = '';
        $.ajax({
            url: json_pathloc,
            type: "GET",
            dataType: "text",
            success: function(data) {
                getNodeEdgeListThenDraw(data);
            }
        })
    }
}
 function constructQueryCallback(){
     var query = constructQuery();
     $.post('/postQuery', query ,'application/json')
     .success(
         function(data){
             // results = JSON.parse(data);
             console.log(data)
             // Do stuff
     })
 }

function populateOptions(list,selector)
{
    selector.innerHTML="";
    //selector.options[0] = new Option("None","None");
    for (var i=0;i<list.length;i++){
      selector.options[i+1] = new Option(list[i],list[i]);
    }
}
// Proper way of actually reading from a DB
// var columns =[];
// $("#all_tables").change(function (){
//     $.post('/getColumns',{
//         "tablename": $("#all_tables").val()
//     },'application/json').success( function(data){
//         // console.log(data)
//         columns = JSON.parse(data);
//         console.log(columns)
//         populateOptions(columns,document.getElementById("xaxis"));
//         populateOptions(columns,document.getElementById("yaxis"));
//         constructQueryCallback()
//     })
// })
// $("#xaxis").change(constructQueryCallback)
// $("#yaxis").change(constructQueryCallback)
// $("input[name='aggFunc']").change(constructQueryCallback)
var columns =[];
var slider_k_input = document.getElementById('slider-k-input'),
   slider_k_output = document.getElementById('slider-k-output');

var availableDict;
$("#all_tables").change(function (){
    $.post('/getAvailableFiles',{
        "tablename": $("#all_tables").val()
    },'application/json').success( function(data){
        console.log(data)
        availableDict = data;
        populateOptions(data["xAxis"],document.getElementById("xaxis"));
        populateOptions(data["dist"],document.getElementById("metric"));
        populateOptions(data["algo"],document.getElementById("algorithm"));
        populateSlider('k',data);
        populateSlider('ic',data);
        populateSlider('ip',data);
    })
})

function populateSlider(name,data){
    var slider_input = document.getElementById(name),
        slider_output = document.getElementById(name);
        slider_input.oninput = function(){
            document.getElementById(name).max=data[name].length - 1;
            slider_output.innerHTML = data[name][this.value];
        };
        slider_input.oninput();
}

//var values = [1,3,5,10,20,50,100];    //values for k
//var slider_k_input = document.getElementById('slider-k-input'),
   //slider_k_output = document.getElementById('slider-k-output');





function mymenuicon(x) {
    x.classList.toggle("change");
}

// $("#submit").click(constructQuery)
// $("#xaxis").change(constructQuery)
// $("input[name='aggFunc']").change(constructQuery)
function getNodeEdgeListThenDraw(nodeDicStr){
    var jsonClean = true
    if (typeof(nodeDicStr)=="object"){
        jsonClean = false
    }
    document.getElementById("loadingDashboard").style.display = "inline"
    $.post("/getNodeEdgeList",{
        "nodeDic" : JSON.stringify(nodeDicStr),
        "jsonClean":jsonClean
    },'application/json').success(function(data){
        edgeList = data[0];
        nodeList = data[1];
        console.log("edgeList:")
        console.log(edgeList)
        console.log("nodeList:")
        console.log(nodeList)
        draw(nodeList,edgeList)
        document.getElementById("loadingDashboard").style.display = "none";
    })
}

// Get the modal
var modal = document.getElementById('message');

// Get the button that opens the modal
var btn = document.getElementById("helpmsg");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks on the button, open the modal
btn.onclick = function() {
    modal.style.display = "block";
}

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
    modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

function IsJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}
var layout = "table";
var currentQuery = "";

function populateA1(){
    currentQuery = "A1"
    layout = "graph"
    fname = "ct_police_stop_stop-outcome_greedy_picking_euclidean_ic0.0_ip0.9_k10.json"
    readDashboardFile(fname)
}

function populateA2(){
    layout = "table"
    currentQuery = "A2"
    fname = "ct_police_stop_stop-outcome_kmeans_euclidean_ic0.0_ip0.001_k10.json"
    readDashboardFile(fname)
}

function populateA3(){
    layout = "table"
    currentQuery = "A3"
    fname = "ct_police_stop_stop-outcome_levelwiseBFS_euclidean_ic0.0_ip0.001_k10.json"
    readDashboardFile(fname)
}

function populateB1(){
    layout="graph";
    currentQuery = "B1"
    fname = "autism_autism_greedy_picking_euclidean_ic0.0_ip0.9_k10.json"
    readDashboardFile(fname)
}

function populateB2(){
    layout = "table"
    currentQuery = "B2"
    fname = "autism_autism_kmeans_euclidean_ic0.0_ip0.001_k10.json"
    readDashboardFile(fname)
}

function populateB3(){
    layout = "table"
    currentQuery = "B3"
    fname  = "autism_autism_levelwiseBFS_euclidean_ic0.0_ip0.001_k10.json"
    readDashboardFile(fname)
}

function populateT1(){
    layout = "graph"
    currentQuery = "T1"
    fname = "titanic_survived_greedy_picking_euclidean_ic0.0_ip0.9_k10.json"
    readDashboardFile(fname)
}

function populateT2(){
    layout = "table"
    currentQuery = "T2"
    fname = "titanic_survived_kmeans_euclidean_ic0.0_ip0.001_k10.json"
    readDashboardFile(fname)
}

function populateT3(){
    layout = "table"
    currentQuery = "T3"
    fname = "titanic_survived_levelwiseBFS_euclidean_ic0.0_ip0.001_k10.json"
    readDashboardFile(fname)
}
/*
function drawTable(){
    newCanvas = true;
    document.getElementById('canvas-graph').style.display = 'none';
    document.getElementById('canvas-table').style.display = '';

    document.getElementById('right-sidebar-graph').style.display = 'none';
    document.getElementById('right-sidebar-table').style.display = '';
    if ((!userChanged)&tableDrawn) return;
    document.getElementById('selected').innerHTML = '';
    document.getElementById('notselected').innerHTML = '';
    fname = 'mushroom_type_frontierGreedy_euclidean_ic0.2_ip0.1_k20.json'
    console.log(fname)
        var nodeDic = ""
        var json_pathloc = "http://"+window.location.hostname+":"+window.location.port+"/generated_dashboards/"+fname
        $.ajax({
            url: json_pathloc,
            type: "GET",
            dataType: "text",
            success: function(data) {
                data = data.replace(/\\"/g, '"')

                //console.log(data)
                data = ('{\"0\": [{ \"xAxis\": \"0\", \"yAxis\":8901.330244122693},{ \"xAxis\": \"1\", \"yAxis\":11982.699453217303},{\"childrenIndex\":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14], \"populationSize\":2147483647, \"filter\":\"#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"1\": [{ \"xAxis\": \"0\", \"yAxis\":2617.150014320924},{ \"xAxis\": \"1\", \"yAxis\":24.329102935422725},{\"childrenIndex\":[15, 17, 39, 41, 45, 49, 53, 54], \"populationSize\":2147483647, \"filter\":\"#is_profile_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"2\": [{ \"xAxis\": \"0\", \"yAxis\":2522.4267606727904},{ \"xAxis\": \"1\", \"yAxis\":6.6490602244851456},{\"childrenIndex\":[225], \"populationSize\":2147483647, \"filter\":\"#is_profile_query$0#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"3\": [{ \"xAxis\": \"0\", \"yAxis\":2209.5112668860293},{ \"xAxis\": \"1\", \"yAxis\":10.67315699098313},{\"childrenIndex\":[97, 125], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$0#is_profile_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"4\": [{ \"xAxis\": \"0\", \"yAxis\":2209.5112668860293},{ \"xAxis\": \"1\", \"yAxis\":10.67315699098313},{\"childrenIndex\":[], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$0#is_profile_query$0#is_event_query$1#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"5\": [{ \"xAxis\": \"0\", \"yAxis\":2522.4267606727904},{ \"xAxis\": \"1\", \"yAxis\":6.6490602244851456},{\"childrenIndex\":[], \"populationSize\":2147483647, \"filter\":\"#is_profile_query$0#is_event_query$1#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"6\": [{ \"xAxis\": \"0\", \"yAxis\":1024.4670902958453},{ \"xAxis\": \"1\", \"yAxis\":2487.6941747440464},{\"childrenIndex\":[17, 18, 21, 22, 25, 29, 33, 37, 38], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$1#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"7\": [{ \"xAxis\": \"0\", \"yAxis\":459.1386720813525},{ \"xAxis\": \"1\", \"yAxis\":95.66689422152325},{\"childrenIndex\":[131, 161, 185, 201, 209], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$1#has_distinct$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"8\": [{ \"xAxis\": \"0\", \"yAxis\":7876.863153826848},{ \"xAxis\": \"1\", \"yAxis\":9495.005278473256},{\"childrenIndex\":[16, 19, 23, 27, 31], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}],\"9\": [{ \"xAxis\": \"0\", \"yAxis\":407.6387474348949},{ \"xAxis\": \"1\", \"yAxis\":13.655945944439596},{\"childrenIndex\":[99, 105, 113, 121, 129], \"populationSize\":2147483647, \"filter\":\"#is_multi_query$1#is_profile_query$0#\",\"yName\":\"SUM(slots_millis_reduces)\",\"xName\":\"has_list_fn\"}]}')
                chartarray = JSON.parse(data)

                console.log(chartarray)

                var len = Object.keys(chartarray).length;
                var rownum = len/5 ;
                var colnum = 5
                var table = document.getElementById('charttable'), tr, td, row, cell;
                table.innerHTML = ''
                tableChecked = [];
                for (var i = 0; i < 20; i++){
                    tableChecked.push(0);
                }
                for (row = 0; row < rownum; row++) {
                    tr = document.createElement('tr');
                    tr.style = "border: 1px solid LightGray;border-collapse: separate"
                    for (cell = 0; cell < colnum; cell++) {
                        var tdId = 'td' + (row * colnum + cell).toString();
                        td = document.createElement('td');
                        td.setAttribute('id',tdId);
                        //td.setAttribute('height', 20%);
                        tr.appendChild(td);
                        td.style = "border: 3px solid grey;border-collapse: separate; align='center' "

                        td.innerHTML = *//*'<p style = "font-size:10px;color:#787878">'+(row * colnum + cell).toString()+'</p>'*//*'<div id="c' +(row * colnum + cell).toString()+'" style = "border-collapse: separate;" onclick="showfilter(this)"></div>'
                    }
                    table.appendChild(tr);
                }
                console.log(len)
                var table = document.getElementById("charttable");

                var cell_idx = 0;
                for(cell_idx = 0; cell_idx < len; cell_idx++){
                    //render_chart(cell_idx,chartarray)
                    var svgString = generateSVG(cell_idx,chartarray)
                    var cellid = 'c'+ cell_idx.toString();
                    var currentcell = document.getElementById(cellid);
                    //console.log(svgString)
                    currentcell.innerHTML = svgString
                    }
                }

        })
    tableDrawn = true;
    userChanged = false;
}*/
//function toggleCanvas(element)
//{
//    if (element.checked){
//        document.getElementById('canvas-graph').style.display = 'none';
//        document.getElementById('canvas-table').style.display = '';
//        if(opened){
//            document.getElementById("mySidebar2").style.width = "25%";
//            document.getElementById("mySidebar2").style.display = "block";
//            document.getElementById("mySidebar").style.display = "none";
//        }
//        document.getElementById('right-sidebar-graph').style.display = 'none';
//        document.getElementById('right-sidebar-table').style.display = '';
//    }
//    else{
//        document.getElementById('canvas-graph').style.display = '';
//        document.getElementById('canvas-table').style.display = 'none';
//        if(opened){
//            document.getElementById("mySidebar").style.width = "25%";
//            document.getElementById("mySidebar").style.display = "block";
//            document.getElementById("mySidebar2").style.display = "none";
//        }
//        document.getElementById('right-sidebar-graph').style.display = '';
//        document.getElementById('right-sidebar-table').style.display = 'none';
//    }
//}


// Direct input graphDic submission form 
$("#graphDicSubmit").click(function(){
    if(layout=="table"){
        var nodeDic = $("#graphDic").val()
        nodeDic = nodeDic.replace(/\\"/g, '"')
        chartarray = JSON.parse(nodeDic)
        console.log(chartarray)

        var len = Object.keys(chartarray).length;

        var rownum = len/5 ;
        var colnum = 5
        var table = document.getElementById('charttable'), tr, td, row, cell;
        table.innerHTML = ''
        tableChecked = {};
        for (var i = 0; i < len; i++){
            tableChecked[i]=0;
        }
        for (row = 0; row < rownum; row++) {
            tr = document.createElement('tr');
            tr.style = "border: 1px solid LightGray;border-collapse: separate"
            for (cell = 0; cell < colnum; cell++) {
                var tdId = 'td' + (row * colnum + cell).toString();
                td = document.createElement('td');
                td.setAttribute('id',tdId);
                //td.setAttribute('height', 20%);
                tr.appendChild(td);
                td.style = "border: 3px solid grey;border-collapse: separate; align='center' "

                td.innerHTML = '<p style = "font-size:10px;color:#787878">'+(row * colnum + cell).toString()+'</p><div id="c' +(row * colnum + cell).toString()+'" style = "border-collapse: separate;" onclick="showfilter(this)"></div>'
            }
            table.appendChild(tr);
        }
        console.log(len)
        var table = document.getElementById("charttable");

        var cell_idx = 0;

        for(cell_idx = 0; cell_idx < len; cell_idx++){
            //render_chart(cell_idx,chartarray)

            var svgString = generateSVG(cell_idx,chartarray/*, xLabel, yLabel*/);
            var cellid = 'c'+ cell_idx.toString();
            var currentcell = document.getElementById(cellid);
            //console.log(svgString)
            currentcell.innerHTML = svgString
            }
        }

    else{
        getNodeEdgeListThenDraw($("#graphDic").val());
    }

})
// Trump Clinton example (display by default, on startup)
// getNodeEdgeListThenDraw({'1': [{'xAxis': 'Clinton', 'yAxis': 48}, {'xAxis': 'Trump', 'yAxis': 46}, {'xAxis': 'Others', 'yAxis': 6}, {'filter': 'All', 'childrenIndex': [2, 3], 'yName': '% of vote'}], '2': [{'xAxis': 'Clinton', 'yAxis': 31}, {'xAxis': 'Trump', 'yAxis': 62}, {'xAxis': 'Others', 'yAxis': 7}, {'filter': 'Race = White', 'childrenIndex': [4, 5], 'yName': '% of vote'}], '3': [{'xAxis': 'Clinton', 'yAxis': 21}, {'xAxis': 'Trump', 'yAxis': 70}, {'xAxis': 'Others', 'yAxis': 9}, {'filter': 'Gender = F', 'childrenIndex': [], 'yName': '% of vote'}], '4': [{'xAxis': 'Clinton', 'yAxis': 21}, {'xAxis': 'Trump', 'yAxis': 52}, {'xAxis': 'Others', 'yAxis': 27}, {'filter': 'Color = Blue', 'childrenIndex': [5], 'yName': '% of vote'}], '5': [{'xAxis': 'Clinton', 'yAxis': 20}, {'xAxis': 'Trump', 'yAxis': 30}, {'xAxis': 'Others', 'yAxis': 50}, {'filter': 'Job = Student', 'childrenIndex': [], 'yName': '% of vote'}]})
// Titanic Default Example
fname = "default"
//getNodeEdgeListThenDraw({"0": [{ "xAxis": "0", "yAxis":65.72734196496572},{ "xAxis": "1", "yAxis":34.27265803503427},{"childrenIndex":[1, 2, 3, 4, 5], "populationSize":1313, "filter":"#","yName":"id"}],"1": [{ "xAxis": "0", "yAxis":83.31374853113984},{ "xAxis": "1", "yAxis":16.686251468860164},{"childrenIndex":[6, 7, 8], "populationSize":851, "filter":"#sexcode$0#","yName":"id"}],"2": [{ "xAxis": "0", "yAxis":33.33333333333333},{ "xAxis": "1", "yAxis":66.66666666666666},{"childrenIndex":[9, 10, 11], "populationSize":462, "filter":"#sexcode$1#","yName":"id"}],"3": [{ "xAxis": "0", "yAxis":40.06211180124223},{ "xAxis": "1", "yAxis":59.93788819875776},{"childrenIndex":[6, 9], "populationSize":322, "filter":"#pc_class$1#","yName":"id"}],"4": [{ "xAxis": "0", "yAxis":57.49999999999999},{ "xAxis": "1", "yAxis":42.5},{"childrenIndex":[7, 10], "populationSize":280, "filter":"#pc_class$2#","yName":"id"}],"5": [{ "xAxis": "0", "yAxis":80.59071729957806},{ "xAxis": "1", "yAxis":19.40928270042194},{"childrenIndex":[8, 11], "populationSize":711, "filter":"#pc_class$3#","yName":"id"}],"6": [{ "xAxis": "0", "yAxis":67.0391061452514},{ "xAxis": "1", "yAxis":32.960893854748605},{"childrenIndex":[], "populationSize":179, "filter":"#sexcode$0#pc_class$1#","yName":"id"}],"7": [{ "xAxis": "0", "yAxis":85.54913294797689},{ "xAxis": "1", "yAxis":14.450867052023122},{"childrenIndex":[], "populationSize":173, "filter":"#sexcode$0#pc_class$2#","yName":"id"}],"8": [{ "xAxis": "0", "yAxis":88.37675350701403},{ "xAxis": "1", "yAxis":11.623246492985972},{"childrenIndex":[], "populationSize":499, "filter":"#sexcode$0#pc_class$3#","yName":"id"}],"9": [{ "xAxis": "0", "yAxis":6.293706293706294},{ "xAxis": "1", "yAxis":93.7062937062937},{"childrenIndex":[], "populationSize":143, "filter":"#sexcode$1#pc_class$1#","yName":"id"}],"10": [{ "xAxis": "0", "yAxis":12.149532710280374},{ "xAxis": "1", "yAxis":87.85046728971963},{"childrenIndex":[], "populationSize":107, "filter":"#sexcode$1#pc_class$2#","yName":"id"}],"11": [{ "xAxis": "0", "yAxis":62.264150943396224},{ "xAxis": "1", "yAxis":37.735849056603776},{"childrenIndex":[], "populationSize":212, "filter":"#sexcode$1#pc_class$3#","yName":"id"}]})


//mode2 functions
function CreateRow() {
    var table = document.getElementById("FilterTable");
    var rowCount = table.rows.length;
    var row = table.insertRow(rowCount);
    var cell1 = row.insertCell(0);

    cell1.innerHTML = "NEW CELL1";

}

function DeleteRow() {
	var table = document.getElementById("FilterTable");
    var rowCount = table.rows.length;
    table.deleteRow(rowCount-1);
}

function showfilter(cell){

     //var Dic = JSON.parse('{\"0\": [{ \"xAxis\": \"0\", \"yAxis\":65.72734196496572},{ \"xAxis\": \"1\", \"yAxis\":34.27265803503427},{\"childrenIndex\":[1, 2, 3, 4, 5], \"populationSize\":1313, \"filter\":\"#\",\"yName\":\"id\"}],\"1\": [{ \"xAxis\": \"0\", \"yAxis\":83.31374853113984},{ \"xAxis\": \"1\", \"yAxis\":16.686251468860164},{\"childrenIndex\":[6, 7, 8], \"populationSize\":851, \"filter\":\"#sexcode$0#\",\"yName\":\"id\"}],\"2\": [{ \"xAxis\": \"0\", \"yAxis\":33.33333333333333},{ \"xAxis\": \"1\", \"yAxis\":66.66666666666666},{\"childrenIndex\":[9, 10, 11], \"populationSize\":462, \"filter\":\"#sexcode$1#\",\"yName\":\"id\"}],\"3\": [{ \"xAxis\": \"0\", \"yAxis\":40.06211180124223},{ \"xAxis\": \"1\", \"yAxis\":59.93788819875776},{\"childrenIndex\":[6, 9], \"populationSize\":322, \"filter\":\"#pc_class$1#\",\"yName\":\"id\"}],\"4\": [{ \"xAxis\": \"0\", \"yAxis\":57.49999999999999},{ \"xAxis\": \"1\", \"yAxis\":42.5},{\"childrenIndex\":[10], \"populationSize\":280, \"filter\":\"#pc_class$2#\",\"yName\":\"id\"}],\"5\": [{ \"xAxis\": \"0\", \"yAxis\":80.59071729957806},{ \"xAxis\": \"1\", \"yAxis\":19.40928270042194},{\"childrenIndex\":[8, 11], \"populationSize\":711, \"filter\":\"#pc_class$3#\",\"yName\":\"id\"}],\"6\": [{ \"xAxis\": \"0\", \"yAxis\":67.0391061452514},{ \"xAxis\": \"1\", \"yAxis\":32.960893854748605},{\"childrenIndex\":[], \"populationSize\":179, \"filter\":\"#sexcode$0#pc_class$1#\",\"yName\":\"id\"}],\"7\": [{ \"xAxis\": \"0\", \"yAxis\":88.37675350701403},{ \"xAxis\": \"1\", \"yAxis\":11.623246492985972},{\"childrenIndex\":[], \"populationSize\":499, \"filter\":\"#sexcode$0#pc_class$3#\",\"yName\":\"id\"}],\"8\": [{ \"xAxis\": \"0\", \"yAxis\":6.293706293706294},{ \"xAxis\": \"1\", \"yAxis\":93.7062937062937},{\"childrenIndex\":[], \"populationSize\":143, \"filter\":\"#sexcode$1#pc_class$1#\",\"yName\":\"id\"}],\"9\": [{ \"xAxis\": \"0\", \"yAxis\":12.149532710280374},{ \"xAxis\": \"1\", \"yAxis\":87.85046728971963},{\"childrenIndex\":[], \"populationSize\":107, \"filter\":\"#sexcode$1#pc_class$2#\",\"yName\":\"id\"}],\"10\": [{ \"xAxis\": \"0\", \"yAxis\":62.264150943396224},{ \"xAxis\": \"1\", \"yAxis\":37.735849056603776},{\"childrenIndex\":[], \"populationSize\":212, \"filter\":\"#sexcode$1#pc_class$3#\",\"yName\":\"id\"}]}');
    //var Dic = JSON.parse('{\"0\": [{ \"xAxis\": \"y\", \"yAxis\":39.931068439192515},{ \"xAxis\": \"s\", \"yAxis\":31.462333825701627},{ \"xAxis\": \"f\", \"yAxis\":28.55736090595766},{ \"xAxis\": \"g\", \"yAxis\":0.04923682914820286},{\"childrenIndex\":[1, 2, 3, 4, 5, 6, 7, 8, 9, 10], \"populationSize\":8124, \"filter\":\"#\",\"yName\":\"cap_surface\"}],\"1\": [{ \"xAxis\": \"y\", \"yAxis\":44.43309499489275},{ \"xAxis\": \"s\", \"yAxis\":36.057201225740556},{ \"xAxis\": \"f\", \"yAxis\":19.40755873340143},{ \"xAxis\": \"g\", \"yAxis\":0.10214504596527069},{\"childrenIndex\":[11, 13], \"populationSize\":3916, \"filter\":\"#type$p#\",\"yName\":\"cap_surface\"}],\"2\": [{ \"xAxis\": \"y\", \"yAxis\":35.741444866920155},{ \"xAxis\": \"s\", \"yAxis\":27.186311787072242},{ \"xAxis\": \"f\", \"yAxis\":37.0722433460076},{ \"xAxis\": \"g\", \"yAxis\":0.0},{\"childrenIndex\":[12, 14, 16], \"populationSize\":4208, \"filter\":\"#type$e#\",\"yName\":\"cap_surface\"}],\"3\": [{ \"xAxis\": \"y\", \"yAxis\":41.71954314720812},{ \"xAxis\": \"s\", \"yAxis\":26.015228426395936},{ \"xAxis\": \"f\", \"yAxis\":32.233502538071065},{ \"xAxis\": \"g\", \"yAxis\":0.031725888324873094},{\"childrenIndex\":[18], \"populationSize\":3152, \"filter\":\"#cap_shape$f#\",\"yName\":\"cap_surface\"}],\"4\": [{ \"xAxis\": \"y\", \"yAxis\":41.5061295971979},{ \"xAxis\": \"s\", \"yAxis\":37.3029772329247},{ \"xAxis\": \"f\", \"yAxis\":21.19089316987741},{ \"xAxis\": \"g\", \"yAxis\":0.0},{\"childrenIndex\":[], \"populationSize\":2284, \"filter\":\"#cap_color$n#\",\"yName\":\"cap_surface\"}]}')
    var Dic = chartarray;
    var title = [];
    var idx = 0
    Object.keys(Dic).forEach(function(key){

        title[idx] = Dic[idx][Dic[idx].length-1]["filter"]
            if (title[idx] == "#"){
                    title[idx] = "overall";
                }
            else if (title[idx]=="collapsed"){
                  title[idx]= ""
            }

            else{
                    for(var j = 0; j<title[idx].length;j++){
                        if(title[idx][j] == "#"){
                            title[idx] = title[idx].substr(0, j) + ',\n' + title[idx].substr(j + 1);
                        }
                        else if(title[idx][j] == "$"){
                            title[idx] = title[idx].substr(0, j) + '=' + title[idx].substr(j + 1);
                        }
                    }
                    title[idx] = title[idx].slice(1,-2)
                }

         idx+=1;
    })

    var i = cell.id .slice(1);
    tableChecked[i] = (tableChecked[i]+1)%3;
    if(tableChecked[i]==0)
            tableChecked[i] = 3;
        var color;
        if(tableChecked[i]==1)
            color = 'grey';
        else if(tableChecked[i]==2)
            color = '#339933';
        else if(tableChecked[i]==3)
            color =  'red';
    var tdid = 'td' + i.toString();
    var user = $("#user").find(":selected").text();
        $.post("/getInterested",{
            "user" : JSON.stringify(user),
            "task" : JSON.stringify(currentQuery),
            "interested" : JSON.stringify(tableChecked),
            "fname" : JSON.stringify(fname)
        },'application/json')
    document.getElementById(tdid).style.borderColor = color
    document.getElementById('selected').innerHTML=""
    document.getElementById('notselected').innerHTML=""

    Object.keys(tableChecked).forEach(function(key) {

        if(tableChecked[key]==2){

            document.getElementById('selected').innerHTML+=  '<li type="square" style="color:green">'+title[key]+'</li>';
        }
        else if(tableChecked[key]==3){
            document.getElementById('notselected').innerHTML+= '<li type="square" style="color:red">'+title[key]+'</li>';
        }
    });



    
}

function generateSVG(cell_idx, chartarray/*, xLabel, yLabel*/) {
	var svgString = ''
	var l = chartarray[cell_idx].length-1
    var yVals = []
    var xAttrs = []
    var dataset = chartarray[cell_idx].slice(0,l)

    for(i=0; i<l; i++){
        yVals.push(dataset[i]['yAxis']);
        xAttrs.push(dataset[i]['xAxis'])
        var t = chartarray[cell_idx][l]["filter"]
        var yName = chartarray[cell_idx][l]["yName"]
        var xName = chartarray[cell_idx][l]["xName"]

        if (t=="#")
              t="overall"
        else if (t=="collapsed")
              t= ""
        else{
             for(var j = 0; j<t.length;j++){
                   if(t[j] == "$"){
                        t = t.substr(0, j) + '=' + t.substr(j + 1);
                   }
             }
             }
    }
    $.ajax({
        type: "POST",
        url: "/getBarchart",
        async: false,
        data: {
            "yVals" : JSON.stringify(yVals),
            "xAttrs" : JSON.stringify(xAttrs),
            "title" : JSON.stringify(t),
            "yName" : JSON.stringify(yName),
            "xName" : JSON.stringify(xName)
        },
        success: function(data){
            svgString = data

        }
    })
    return svgString

}

function updateTextInput(val) {
          document.getElementById('textInput').value=val;
        }

