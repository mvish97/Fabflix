//if (!sessionStorage.id){
//    location.replace('login.html');
//}

console.log(localStorage.movie_selected);

function splitActors(actors, star_ids){
    var stars = actors.split(",");
    var ids = star_ids.split(",");
    var result = ``;
    for(var i=0; i < stars.length; i++){
        result += `<li><a class='stars' id='${ids[i]}'>${stars[i]}</a></li>`;
    }
    return result;
}

function splitGenres(genres){
    var listOfGenres = genres.split(",");
    var result = "<a class='genre'id='"+ listOfGenres[0] +"'>" + listOfGenres[0] + "</a>";
    for(var i=1; i < listOfGenres.length; i++){
        result += " | <a class='genre' id='"+ listOfGenres[i] +"'>" + listOfGenres[i] + "</a>";
    }
    return result;
}

function handleMovie(resultData){
    console.log(resultData);
    
    var movie_info = $("#movie_info");
    
    var stars = splitActors(resultData[0].actors, resultData[0].star_ids);
    var genres = splitGenres(resultData[0].genres);
    
    movie_info.append(`<h1>${resultData[0].title} (${resultData[0].year})</h1>
        <h2>${genres}, <span class="rating">Rating : ${resultData[0].rating}</span></h2>
        <h2>This movie was directed by <i>${resultData[0].director}</i>.</h2>
        <hr>
        <h2><b>${resultData[0].title}</b> had the following stars:
            <ul>${stars}</ul>
        </h2>
        <hr>
        <h3> id: ${resultData[0].id} </h3>
        <div class="cart_section"><button class='cart_btn' id='${resultData[0].id}' name='${resultData[0].title}'>Add to cart</button></div>
    `)
    
    $(".stars").click(function(e){
        e.preventDefault();
        localStorage.setItem("star_selected",e.target.id);
        location.replace("star.html")
    });
    
    $(".title").click(function(e){
        e.preventDefault();
        localStorage.setItem("movie_selected",e.target.id);
        location.replace("movie.html");
    });
    
    $(".genre").click(function(e){
        e.preventDefault();
        var data = {
            "genre": e.target.id,
            "offset": 0,
            "limit": 10
        };
        console.log(e.target.id);
        localStorage.dataToPost = JSON.stringify(data);
        localStorage.linkToCall = sessionStorage.baseurl+"/project2/browseByGenre";
        location.replace("movieList.html");
    });
    
    $(".cart_btn").click(function(e){
        e.preventDefault();
        var objs = JSON.parse(sessionStorage.cart);
        var found = false;
        for(var i = 0; i < objs.length; i++) {
            if (objs[i].id == e.target.id) {
                found = true;
                objs[i].qty += 1;
                break;
            }
        }
        
        if (!found){
           var data = {
                "title": e.target.name,
                "id": e.target.id,
                "qty": 1
            }
            objs.push(data); 
        }
            
        sessionStorage.cart = JSON.stringify(objs);
        alert(`Added '${e.target.name}' in your cart`);
    })
}



jQuery.ajax({
        type: "POST",
        dataType: "json",
        data: {
            "id": localStorage.movie_selected
        },
        url: sessionStorage.baseurl+"/project2/getMovieDetails",
        success: resultData => handleMovie(resultData)
});