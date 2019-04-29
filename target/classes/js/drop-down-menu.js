/**
*Fetch Column names of a certain datset
*/
function getCol(){
    $.ajax({
            type: "POST",
            url: "viz/getCol",
            async: false,
            data: {
                "datasetName" : JSON.stringify(document.getElementById('data').value),
            },
            success: function(data) {
                    //console.log(data);
                    document.getElementById("x").innerHTML = "";
                    document.getElementById("y").innerHTML = "";
                    for(i = 0; i<data.length; i++){
                        document.getElementById("x").innerHTML += "<option>" + data[i] + "</option>";
                        document.getElementById("y").innerHTML += "<option>" + data[i] + "</option>";
                    }
                }

            })
}


/**
* Fetch the name of datasets to dynamically fill the Dataset field.
*/
function getTable(){
    $.ajax({
            type: "POST",
            url: "viz/getTable",
            async: false,
            data: {
                
            },
            success: function(data) {
                    //console.log(data);
                    document.getElementById("data").innerHTML = "";
                    document.getElementById("data").innerHTML += "<option>" + "Select an Option" + "</option>";
                    for(i = 0; i<data.length; i++){
                        document.getElementById("data").innerHTML += "<option>" + data[i] + "</option>";
                    }
                }

            })
}
