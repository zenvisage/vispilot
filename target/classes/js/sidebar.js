// Filter Form
var filters=[];
var opened = false;
$(function()
{
    $(document).on('click', '.btn-add', function(e)
        {
            e.preventDefault();

            var controlForm = $('.controls form:first'),
                currentEntry = $(this).parents('.entry:first'),
                newEntry = $(currentEntry.clone()).appendTo(controlForm);

            newEntry.find('input').val('');

            controlForm.find('.entry:not(:last) .btn-add')
                .removeClass('btn-add').addClass('btn-remove')
                .removeClass('btn-success').addClass('btn-danger')
                .html('<span class="glyphicon glyphicon-minus"></span>');
        }).on('click', '.btn-remove', function(e)
        {
            $(this).parents('.entry:first').remove();

            e.preventDefault();
            return false;
        });
});
// Reading values from filter form

$("#filterSubmit").click( function(){
    filters=[];
    $('.filter').each(function(index,item){
        console.log(item);
        filters.push(item.value);
        constructQueryCallback();
    });
});

// Expandable menuItem (Not being used right now)

function menuItem_open_close() {
    opened = !opened
    if(opened){
        document.getElementById("main").style.marginLeft = "25%";
        if(document.getElementById("newCanvas").checked){
            //document.getElementById("mySidebar2").style.width = "25%";
            //document.getElementById("mySidebar2").style.display = "block";
            document.getElementById("mySidebar").style.display = "none";
        }
        else{
            document.getElementById("mySidebar").style.width = "25%";
            document.getElementById("mySidebar").style.display = "block";
            ///document.getElementById("mySidebar2").style.display = "none";
        }
    }
    else{
        document.getElementById("main").style.marginLeft = "0%";
        document.getElementById("mySidebar").style.display = "none";
        //document.getElementById("mySidebar2").style.display = "none";
    }
}

function menuItem_onclick(id) {
    var x = document.getElementById(id);
    if (x.className.indexOf("w3-show") == -1) {
        x.className += " w3-show";
    } else { 
        x.className = x.className.replace(" w3-show", "");
    }
}
function setIfNotAdd(selector_name,val){
    // if the item is not in the dropdown menu, then add it into the list of items and set it as that.
    if($(selector_name+' option[value="' + val + '"]').length === 0) {
        $(selector_name).append('<option value="' + val + '">' + val + '</option>');
    }    
    $(selector_name).val(val);
}

// Pre-populated queries
// function populateD1Query1(){
//     $("#all_tables").val("mushroom").trigger("change").ajaxSuccess(function(event){
//         $("#xaxis").val("type");
//         $("#metric").val("euclidean");
//         $("#algorithm").val("frontierGreedy");
//         $("#k").val("20");
//         $("#ic").val("0.2");
//         $("#ip").val("0.1");   
//         $("#submit").click();
//         event.preventDefault();
//         event.stopPropagation();
//     })
// }

//function populateA1(){
//    constructQueryWithArgs("mushroom","cap-surface","Breadth First Search","euclidean",0.0,0.001,10)
////    $("#all_tables").val("mushroom")
////    setIfNotAdd("#xaxis","type")
////    setIfNotAdd("#metric","euclidean")
////    setIfNotAdd("#algorithm","frontierGreedy")
////    setIfNotAdd("#k","20")
////    setIfNotAdd("#ic","0.2")
////    setIfNotAdd("#ip","0.1")
////    $("#submit").click();
//}
//mushroom_type_Breadth First Picking_euclidean_ic0.0_ip0.9_k10.json
/*function populateB1(){
    constructQueryWithArgs("mushroom","type","Breadth First Picking","euclidean",0.0,0.9,10)
*//*    $("#all_tables").val("mushroom")
    setIfNotAdd("#xaxis","cap-surface")
    setIfNotAdd("#metric","euclidean")
    setIfNotAdd("#algorithm","frontierGreedy")
    setIfNotAdd("#k","20")
    setIfNotAdd("#ic","0.2")
    setIfNotAdd("#ip","0.1")
    $("#submit").click();*//*
}*/
function populateD2Query1(){
    constructQueryWithArgs("turn","has-list-fn","frontierGreedy","euclidean",0.2,0.1,10)
/*
    $("#all_tables").val("turn")
    setIfNotAdd("#xaxis","has-list-fn")
    setIfNotAdd("#metric","euclidean")
    setIfNotAdd("#algorithm","frontierGreedy")
    setIfNotAdd("#k","10")
    setIfNotAdd("#ic","0.2")
    setIfNotAdd("#ip","0.1")
    $("#submit").click();
*/
}
function populateD2Query2(){
    constructQueryWithArgs("turn","has-list-fn","frontierGreedy","euclidean",0.0,0.1,30)
/*    $("#all_tables").val("turn")
    setIfNotAdd("#xaxis","has-list-fn")
    setIfNotAdd("#metric","euclidean")
    setIfNotAdd("#algorithm","frontierGreedy")
    setIfNotAdd("#k","30")
    setIfNotAdd("#ic","0")
    setIfNotAdd("#ip","0.1")
    $("#submit").click();*/
}
function populateD2Query1(){
    constructQueryWithArgs("turn","has-list-fn","frontierGreedy","euclidean",0.0,0.1,15)
/*
    $("#all_tables").val("turn")
    setIfNotAdd("#xaxis","has-list-fn")
    setIfNotAdd("#metric","euclidean")
    setIfNotAdd("#algorithm","frontierGreedy")
    setIfNotAdd("#k","15")
    setIfNotAdd("#ic","0")
    setIfNotAdd("#ip","0.1")
    $("#submit").click();
*/
}
function populateD3Query1(){
    constructQueryWithArgs("titanic","survived","frontierGreedy","euclidean",0.0,0.1,15)
/*    $("#all_tables").val("titanic")
    setIfNotAdd("#xaxis","survived")
    setIfNotAdd("#metric","euclidean")
    setIfNotAdd("#algorithm","frontierGreedy")
    setIfNotAdd("#k","15")
    setIfNotAdd("#ic","0")
    setIfNotAdd("#ip","0.1")
    $("#submit").click();*/
}
function populateD3Query2(){
    constructQueryWithArgs("titanic","survived","frontierGreedy","kldiv",0.0,0.9,30)
/*
    $("#all_tables").val("titanic")
    setIfNotAdd("#xaxis","survived")
    setIfNotAdd("#metric","kldiv")
    setIfNotAdd("#algorithm","frontierGreedy")
    setIfNotAdd("#k","30")
    setIfNotAdd("#ic","0")
    setIfNotAdd("#ip","0.9")
    $("#submit").click();
*/
}