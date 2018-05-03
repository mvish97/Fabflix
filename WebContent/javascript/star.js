
function splitMovies(movies, movie_ids){
    var stars = movies.split(",");
    var ids = movie_ids.split(",");
    var result = ``;
    for(var i=0; i < stars.length; i++){
        result += `<li><a class='title' id='${ids[i]}'>${stars[i]}</a></li>`;
    }
    return result;
}

function handleMovie(resultData){
    console.log(resultData);
    
    var star_info = $("#star_info");
    
    var movies = splitMovies(resultData.movies, resultData.movieIds);
    
    if (resultData.birthYear == null){
        resultData.birthYear = "Not available"
    }
    
    star_info.append(`
        <h1>${resultData.name}</h1>
        <h2>Birth Year : ${resultData.birthYear}</h2>
        <h2>Movies starred in:<ul>${movies}</ul>
        </h2>
        <hr>
        <h3>id : ${resultData.id}</h3>`)
    
    $(".title").click(function(e){
        e.preventDefault();
        localStorage.setItem("movie_selected",e.target.id);
        location.replace("movie.html");
    });
}



jQuery.ajax({
        type: "POST",
        dataType: "json",
        data: {
            "id": localStorage.star_selected
        },
        url: sessionStorage.baseurl+"/project2/getStarDetails",
        success: resultData => handleMovie(resultData)
});