
/**
*Main expand function. Send all parameters to back end to get the expanded nodeDic.
*/
function Expand(id){
    var d = document.getElementById('dlg_text');
    d.style.display = 'none';
    
    //calculate the number of children to know exactly how many nodes we need to expand
    var children = numOfChildren(id, child_dic);
    console.log(children);
    var expand = parseInt(document.getElementById("expandID").value) + parseInt(children);
    console.log(expand)
    //dic = JSON.parse(dic);
    console.log(dic[id]);
    $.ajax({
            type: "POST",
            url: "viz/expand",
            async: false,
            data: {
                "rootid": JSON.stringify(dic[id]),
                "expand": expand,
                "datasetName" : JSON.stringify(document.getElementById('data').value),
                "yAxis" : JSON.stringify(document.getElementById('y').value),
                "xAxis" : JSON.stringify(document.getElementById('x').value),
                "aggType" : JSON.stringify(document.getElementById('agg').value),
                //"k" : document.getElementById('kOutputId').value,
                "ic" : 0,
                "info" : 0.9
            },
            success: function(data) {
                    console.log(data);
                    var new_dic = old_dic.slice(0,-1) + ',' + data.substring(1);
                    console.log(new_dic);
                    getNodeEdgeListThenDraw(new_dic);
                },
              error: function(xhr, textStatus, error){
                  alert("Invalid Input! Try another smaller number.");
              }

            })
}

/**
*Calculate the number of children of a certain node given node_id.
*/
function numOfChildren(id, child_dic){
    var ret = 1;
    console.log(child_dic);
    if((!id in child_dic) || (child_dic[id] == undefined) || (child_dic[id].length == 0))
        return ret;

    for(i in child_dic[id]){
        //console.log(child_dic[i]);
        if(child_dic[id][i] in dic){
            console.log(child_dic[id][i]);
            ret += numOfChildren(child_dic[id][i], child_dic);
        }
    }
    return ret;
}

/**
*Show Expanding dialog on UI.
*/
function showDialog(nodeID){
    var title = dic[nodeID];
    if(title=="#")
        title="overall"
    else if(title=="collapsed")
        title="collapsed"
    else
        title = String(title.split("#").join(", ").split("$").join("=")).slice(1, -2);
    title = "Expanding " + title + "<br> with ";
    var d = document.getElementById('dlg_text');
    var expandroot = (node_dataset._data[nodeID].id);
    //console.log(node_dataset._data[nodeID].filterVal);
    //var expandroot = node_dataset._data[nodeID]["filterVal"];
    //console.log(expandroot);
    var input1 = '&nbsp;<input  width="50px" type="number" id="expandID" value="0"> additional visualizations &nbsp;<button onclick="Expand(';
    var input2 = ')">Submit</button>';
    d.innerHTML = "<style='font-size:22px'>"+ title + input1 + "'" + expandroot+"'" + input2;
    document.getElementById('expandID').style.width = '50px';
}
