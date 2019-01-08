var network = null;
var DIR = 'img/refresh-cl/';
var LENGTH_MAIN = 150;
var LENGTH_SUB = 50;
var node_dataset = null;
// Called when the Visualization API is loaded.
var totalclick = {};
var options;
function draw(node,edge) {

    // create a network
    var container = document.getElementById('canvas-graph');
    container.removeChild(container.childNodes[0])

    //(totalclick = []).length = node.length;
    //totalclick.fill(0);

    node_dataset = new vis.DataSet(node);
    for (i = 0; i < node.length; i++){

            totalclick[node[i].id] = 0;

    }
    console.log(totalclick);
    var data = {
        nodes: node_dataset,
        edges: edge
    };
    options = {
        interaction: {
            selectable: true,
            hover: true,
            navigationButtons: true,
            keyboard: true
        },
        nodes: {
          chosen:false,
          borderWidth:3,
          size: 100,
          color: {
              border: '#A1BACB',
              background: '#FFFFFF',
              highlight: 'red',
              hover: '#ff9933'
            },
          font:{color:'#0B131A',
                size:8
          },
          shapeProperties: {
              useBorderWithImage:true
            }

        },
        physics: {enabled: false},
        edges: {smooth: false,
                color: '4E7189'
        },
        layout: {
            randomSeed: undefined,
            improvedLayout:true,
            hierarchical: {
              enabled:true,
              levelSeparation: 260,
              nodeSpacing: 230,
              treeSpacing: 600,
              blockShifting: true,
              edgeMinimization: true,
              parentCentralization: true,
              sortMethod: 'directed'   // hubsize, directed
            }
        }
    };

    //hightlight collapsed node
    for (i = 0; i < node.length; i++) {
        var currNode = node_dataset.get(node[i].id);
        if(currNode.collapse.length > 0){
            currNode.color = {
                border: '#ff9933',
                highlight: '#ff9933'
            }
            node_dataset.update(currNode);

        }
    }

    console.log(node_dataset);
    network = new vis.Network(container, data, options);
    network.on("hoverNode", function (params) {
        //console.log('hoverNode Event:', params);
        var nodeID = params.node;
        //console.log(nodeID)
        var hoveredNode = node_dataset.get(nodeID);
        if(hoveredNode.collapse.length > 0){
            //console.log(hoveredNode)
            hoveredNode.title = ""
            for(i = 0; i < hoveredNode.collapse.length; i++){
                var coltitle = hoveredNode.collapse[i];
                coltitle = coltitle.toString().replace(/#,#/g, ' ');
                coltitle = coltitle.toString().split('$').join('=');
                coltitle = coltitle.toString().split('#').join(',');
                coltitle = coltitle.toString().split(' ').join("<br>");
                coltitle = coltitle.slice(1,-1);
                //coltitle = coltitle.split(" ").join("\n");
                hoveredNode.title += '<div><font size="4">'+coltitle+'</font></div>';
            }

            //console.log(hoveredNode)
            hoveredNode.color = {
                border: '#ff9933'
            }
            node_dataset.update(hoveredNode);
        }
    });
    network.on("click", function(params) {

        var nodeID = params['nodes']['0'];
        console.log(nodeID);

        totalclick[nodeID] = (totalclick[nodeID]+1)%3;
        if(totalclick[nodeID]==0)
            totalclick[nodeID] = 3;
        var color;
        if(totalclick[nodeID]==1)
            color = 'grey';
        else if(totalclick[nodeID]==2)
            color = '#cc66ff';
        else if(totalclick[nodeID]==3)
            color = '#ff9933';

        if (nodeID>=0) {
            var clickedNode = node_dataset.get(nodeID);
            //node_dataset.remove(nodeID);
            console.log(clickedNode);
            console.log(totalclick);


            clickedNode.color = {
                border: color,
                highlight:color
            }

            //console.log("before: ")
            //console.log(node_dataset);
            node_dataset.update(clickedNode);
            //console.log("after: ")
            //console.log(node_dataset);
        }
        var user = $("#user").find(":selected").text();
        $.post("/getInterested",{
            "user" : JSON.stringify(user),
            "task" : JSON.stringify(currentQuery),
            "interested" : JSON.stringify(totalclick),
            "fname" : JSON.stringify(fname)
        },'application/json')
        //document.getElementById('interested-in').innerHTML = '';
        //document.getElementById('not-interested-in').innerHTML = '';
        for (i = 0; i < node.length; i++) {

            if(totalclick[node[i].id]==2){
                //var currNode = node_dataset.get(i);
                //document.getElementById('interested-in').innerHTML+='<li type="square" style="color:green">'+node[i].filterVal+'</li>';
            }
            else if (totalclick[node[i].id]==3){
                //var currNode = node_dataset.get(i);
                //document.getElementById('not-interested-in').innerHTML+='<li type="square" style="color:red">'+node[i].filterVal+'</li>';
            }
        }
        selection = params.nodes
        if (nodeID != undefined) {
          showJQueryDialog(params, nodeID, selection);
          //showPopover()
        }
        else{
            clearJQueryDialog(params, nodeID, selection);
        }
    });

   /*network.on("click", function (params) {
        console.log(params)
       params.event = "[original event]";
       //document.getElementById('eventSpan').innerHTML = '<h2>Click event:</h2>' + JSON.stringify(params, null, 4);
       console.log('click event, getNodeAt returns: ' + this.getNodeAt(params.pointer.DOM));
        //var temp = data.get(this.getNodeAt(params.pointer.DOM));
        //params.nodes.update([{id:1, color:{background:'#0B131A'}}]);
    });*/
    var div = document.createElement('div')
    div.innerHTML="<img src='css/Eclipse.svg' id = 'loadingDashboard' style='display: none; position: relative; z-index: 10; width: 100%; height: 50%;'>"
    container.prepend(div.firstChild);
}
function showJQueryDialog(params, nodeID, selection) {

    title = "Expanding " + node_dataset._data[nodeID]["filterVal"] + "<br> with ";
    var d = document.getElementById('dlg_text');
    var input = '&nbsp;<input type="text" size="1" name="lastname" value="0"> additional visualizations &nbsp;<input type="submit" style="border-radius: 5px;" value="Submit">'
    d.innerHTML = "<style='font-size:22px'>"+ title + input ;
    d.style.position = "fixed";
    d.style.display = "inline";
    d.style.left = params.pointer.DOM.x+40+'px';
    d.style.top = params.pointer.DOM.y+'px';

}
function clearJQueryDialog(params, node_dataset, selection) {


    var d = document.getElementById('dlg_text');
    d.style.display = 'none';
    }
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
////////                   D3 implementation                                       ////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
function render_chart(i, nodeDic){
//    Dic = JSON.parse(nodeDic)

    Dic = nodeDic
    dataset = Dic[i].slice(0,Dic[i].length-1)
    console.log(dataset)
    yAxis_name = Dic[i][Dic[i].length-1]["yName"]
    title = Dic[i][Dic[i].length-1]["filter"]
    if (i==0){
        title = "overall";
    }
    else{
        for(var j = 0; j<title.length;j++){
            if(title[j] == "#"){
                if(j!=0 || j!=title.length-1)
                    title = title.substr(0, j) + '\n' + title.substr(j + 1);
            }
            else if(title[j] == "$"){
                title = title.substr(0, j) + '=' + title.substr(j + 1);
            }
        }
    }

    //$("#title").html(title) 
    //alert(dataset[0][0].xAxis);
   

    // Dimensions for the chart: height, width, and space b/t the bars
    var margins = {top: 30, right: 50, bottom: 30, left: 40}
    var height = 180 - margins.left - margins.right,
        width = 140- margins.top - margins.bottom,
        barPadding = 5

    // Create a scale for the y-axis based on data
    // >> Domain - min and max values in the dataset
    // >> Range - physical range of the scale (reversed)
//    var yScale = d3.scale.linear()
//      .domain([0, d3.max(dataset, function(d){
//        return d.yAxis;
//      })])
//      .range([height, 0]);
    var yScale = d3.scale.linear()
      .domain([0, 100])
      .range([height, 0]);

    // Implements the scale as an actual axis
    // >> Orient - places the axis on the left of the graph
    // >> Ticks - number of points on the axis, automated
    var yAxis = d3.svg.axis()
      .scale(yScale)
      .orient('left')
      .ticks(4);

    // Creates a scale for the x-axis based on city names
    var xScale = d3.scale.ordinal()
      .domain(dataset.map(function(d){
        return d.xAxis;
      }))
      .rangeRoundBands([0, width], .1);

    // Creates an axis based off the xScale properties
    var xAxis = d3.svg.axis()
      .scale(xScale)
      .orient('bottom');

    // Creates the initial space for the chart
    // >> Select - grabs the empty <div> above this script
    // >> Append - places an <svg> wrapper inside the div
    // >> Attr - applies our height & width values from above
    current_cell = "#c"+i.toString();
    var chart = d3.select(current_cell)
      .append('svg')
      .attr('width', width + margins.left + margins.right)
      .attr('height', height + margins.top + margins.bottom)
      .append('g')
      .attr('transform', 'translate(' + margins.left + ',' + margins.top + ')');
    
    chart.selectAll("rect")
          .data(dataset)

    // For each value in our dataset, places and styles a bar on the chart

    // Step 1: selectAll.data.enter.append
    // >> Loops through the dataset and appends a rectangle for each value
    chart.selectAll('rect')
      .data(dataset)
      .enter()
      .append('rect')

      // Step 2: X & Y
      // >> X - Places the bars in horizontal order, based on number of
      //        points & the width of the chart
      // >> Y - Places vertically based on scale
      .attr('x', function(d, i){
        return xScale(d.xAxis);
      })
      .attr('y', function(d){
        return yScale(d.yAxis);
      })


      // Step 3: Height & Width
      // >> Width - Based on barpadding and number of points in dataset
      // >> Height - Scale and height of the chart area
      .attr('width', (width / dataset.length) - barPadding)
      .attr('height', function(d){
        return height - yScale(d.yAxis);
      })
      .attr('fill', 'steelblue')

      // Step 4: Info for hover interaction
      .attr('class', function(d){
        return d.xAxis;
      })
      .attr('id', function(d){
        return d.yAxis;
      });

    // Renders the yAxis once the chart is finished
    // >> Moves it to the left 10 pixels so it doesn't overlap
    chart.append('g')
      .attr('class', 'axis')
      .attr('transform', 'translate(-10, 0)')
      .call(yAxis);

    // Appends the yAxis
    chart.append('g')
      .attr('class', 'axis')
      .attr('transform', 'translate(0,' + (height + 10) + ')')
      .call(xAxis);

    // Adds yAxis title


    chart.append("g")
    .attr("transform", "translate(" + margins.left + "," + margins.top + ")")
    .selectAll(".textlabel")
    .data(dataset)
    .enter()
    .append("text")
    .attr("class", "textlabel")
    .attr("x", function(d){ return xScale(d.xAxis)-40; })
    .attr("y", function(d){ return yScale(d.yAxis)-30; })
    .text(function(d){ return Math.round((d.yAxis) * 100) / 100; })
     .style({"font-family":"Arial", "font-weight":"300"});

    // add bar chart title
    chart.append("text")
        .attr("x", (width / 2))
        .attr("y",  "-12px")
        .attr("text-anchor", "middle")
        .style("font-size", "10px")
        .style("text-decoration", "underline")
        .text(title);

   return chart;
  }
  function handleMouseOver() {
    console.log("alert");
    //alert("alert");
  }
  /**function test_chart(){
    // On document load, call the render() function to load the graph
    for (i = 0; i < data.length; i++) 
    { 
        render_chart(i);
        $('rect').mouseenter(function(){
        $('#xAxis').html(this.className.animVal);
        $('#yAxis').html($(this).attr('id'));
      });
    }**/
/*
    function test_chart(arrayDiv){
    // On document load, call the render() function to load the graph

    for(var i=0; i < data.length; i++){
        arrayDiv[i] = document.createElement('div');
        arrayDiv[i].id = 'block' + i;
        // arrayDiv[i].innerHTML = "render_chart("+i+");$('rect').mouseenter(function(){$('#xAxis').html(this.className.animVal);$('#yAxis').html($(this).attr('id'));});"
        arrayDiv[i].innerHTML = "<div id=chart"+i+"></div>"
        render_chart(i);
    }
  }*/


