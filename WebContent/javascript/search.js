

$(document).ready(function(){
    $("#search").click(function(e){
        e.preventDefault();
        
        if(!$("#title").val() && !$("#star").val() && !$("#year").val() && !$("#director").val()){
            alert("Please fill-in at least one field.");
            return;
        }
        
        var dataToPost = {
            "title": "",
            "star": "",
            "year": "",
            "director": "",
            "offset": 0,
            "limit": sessionStorage.n
        };
        
        dataToPost.title =  $("#title").val();              
        dataToPost.star =  $("#star").val();
        dataToPost.year = $("#year").val();          
        dataToPost.director =  $("#director").val();
        
        localStorage.setItem("dataToPost", JSON.stringify(dataToPost));
        localStorage.setItem("linkToCall", sessionStorage.baseurl+"/project2/search");
        location.replace("movieList.html");
    });
});