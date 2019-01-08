//var node_struct = document.getElementById("treeNode").innerHTML;
//var nodeDic = document.getElementById("treeNode2").innerHTML;
// console.log("nodedic")
// console.log(nodeDic)
//
//var chart_config = {
//        chart: {
//            container: "#tree_container",
//
//            animateOnInit: true,
//
//            node: {
//                collapsable: true
//            },
//            animation: {
//                nodeAnimation: "easeOutBounce",
//                nodeSpeed: 700,
//                connectorsAnimation: "bounce",
//                connectorsSpeed: 700
//            }
//        },
//        nodeStructure: JSON.parse(node_struct)
//    };
//
//
//
//
//
//
//    function test_chart_collapsable(arrayDiv, nodeDic){
//        // On document load, call the render() function to load the graph
//        chartid = document.getElementById("chartid");
//
//        for(var i = 1; i < 3; i++) { // Need to replace this number 3 to dynamically read values from length of the nodeDic
//            arrayDiv[i] = document.createElement('div');
//            arrayDiv[i].id = 'chart' + i;
//
//            chartid.appendChild(arrayDiv[i]);
//
//
//            p = document.createElement('p');
//            spanx = document.createElement('span');
//            spanx.id = "xAxis" + i;
//            spany = document.createElement('span');
//            spany.id = "yAxis" + i;
//            p.appendChild(spanx);
//            p.appendChild(spany);
//            arrayDiv[i].appendChild(p);
//            //chart_container.appendChild("<p><span id="xAxis"></span> - <span id="yAxis"></span></p>")
//
//            $('rect').mouseenter(function(){
//                $('#xAxis' + i).html(this.className.animVal);
//                $('#yAxis' + i).html($(this).attr('id'));
//            });
//            // console.log(nodeDic)
//            render_chart(i, nodeDic);
//        }
//        // console.log("done");
//    }
//
