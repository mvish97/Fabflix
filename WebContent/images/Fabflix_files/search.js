$(document).ready(function(){
    $("#search").click(function(e){
        e.preventDefault();
        
        if(!$("#title").val() && !$("#star").val() && !$("#year").val() && !$("#director").val()){
            alert("Please fill-in at least one field.")
        }
        
        var dataToPost = {};
        
        if($("#title").val()){
           dataToPost["title"] =  $("#title").val();              
        }
        
        if($("#star").val()){
            dataToPost["star"] =  $("#star").val();
        }
        
        if($("#year").val()){
            dataToPost["year"] =  $("#year").val();          
        }
        
        if($("#director").val()){
            dataToPost["director"] =  $("#director").val();              
        }
        
        console.log(dataToPost);
        
        $.ajax({
        type: "POST",
        dataType: "json",
        data: dataToPost,
        url: "http://localhost:8080/project2/search",
        success: (resultData) => handleList(resultData)
    });
        
        
    });
});

function handleList(resultData){
    console.log(resultData);
}