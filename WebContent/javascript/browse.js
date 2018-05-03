
function handleGenres(resultData){
    var genres = $("#genres_list");
    genres.append("<ul>");
    for (var i=0; i<resultData.length; i++){
        var temp = resultData[i][(i+1).toString()];
        genres.append("<li class='genre_button' id='"+temp+"'>"+temp+"</li>");
    }
    genres.append("</ul>");
    
    $(".genre_button").click(function(e){
        e.preventDefault();
        var data = {
            "genre": e.target.id,
            "offset": 0,
            "limit": sessionStorage.n
        };
        console.log(e.target.id);
        localStorage.dataToPost = JSON.stringify(data);
        localStorage.linkToCall = sessionStorage.baseurl+"/project2/browseByGenre";
        location.replace("movieList.html");
    });
}

$("document").ready(function(e){
    var alphanums = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(',');
    var letters = $("#letter_list");
    
    letters.append("<ul>");
    for (var i=0; i<alphanums.length; i++){
        var temp = alphanums[i];
        letters.append("<li class='letter_button' id='"+temp+"'>"+temp+"</li>");
    }
    letters.append("</ul>");
    
    $(".letter_button").click(function(e){
        e.preventDefault();
        var data = {
            "title": e.target.id
        };
        console.log(e.target.id);
        localStorage.dataToPost = JSON.stringify(data);
        localStorage.linkToCall = sessionStorage.baseurl+"/project2/browseByTitle";
        location.replace("movieList.html");
    });
});

jQuery.ajax({
        type: "GET",
        dataType: "json",
        url: sessionStorage.baseurl+"/project2/genreList",
        success: resultData => handleGenres(resultData)
});