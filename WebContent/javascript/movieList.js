
$("#next_prev").hide();
$("#not_found_message").hide();

var n = parseInt(sessionStorage.n);
var lastPageNumber = -1;
var reset = true;

window.onbeforeunload = function() {
    if (reset) {
        sessionStorage.page = 1;
    }       
}

function compareTitleAsc(a,b) {
  if (a.title < b.title)
    return -1;
  if (a.title > b.title)
    return 1;
  return 0;
}

function compareYearDsc(a,b) {
  if (a.year < b.year)
    return -1;
  if (a.year > b.year)
    return 1;
  return 0;
}

function compareTitleDsc(a,b) {
  if (a.title > b.title)
    return -1;
  if (a.title < b.title)
    return 1;
  return 0;
}

function compareYearAsc(a,b) {
  if (a.year > b.year)
    return -1;
  if (a.year < b.year)
    return 1;
  return 0;
}

function handleList(resultData) {
    var movie_list = $("#movie_list");
    
    if (resultData.length > 0){
        $("#next_prev").show();
        if (lastPageNumber == -1){
            if(resultData[0].size % n != 0){
                lastPageNumber = parseInt(resultData[0].size/n + 1);
            }else{
                lastPageNumber = parseInt(resultData[0].size/n);
            }
            handlePageNumber();
        }
    }else{
        $("#not_found_message").show();
        return;
    }
    
    var sortingOption = sessionStorage.sorting;
    
    if(sortingOption == "asc_title"){
        resultData.sort(compareTitleAsc);
    }else if(sortingOption == "dsc_title"){
        resultData.sort(compareTitleDsc);
    }else if(sortingOption == "asc_year"){
        resultData.sort(compareYearAsc);     
    }else{
        resultData.sort(compareYearDsc);
    }
    
    for(var i=0; i < Math.min(resultData.length, n); i++){
        var stars = splitActors(resultData[i].actors, resultData[i].star_ids);
        var genres = splitGenres(resultData[i].genres)
        movie_list.append(`<div class="movie_box">
            <ul class="row-1">
                <li class='title'>${i+n*(parseInt(sessionStorage.page)-1)+1}. <a id='${resultData[i].id}'>${resultData[i].title}</a> (${resultData[i].year})</li>
                <li>${genres}</li>
            </ul>
            <ul class="row-2">
                <li> Director: ${resultData[i].director} </li>
                <li> Stars: ${stars} </li>
            </ul>
            <ul class="row-3">
                <li> Ratings: ${resultData[i].rating}, id: ${resultData[i].id} </li>
            </ul>
            <div class="cart_section"><button class='cart_btn' id='${resultData[i].id}' name='${resultData[i].title}'>Add to cart</button></div>
        </div>`);
    }
    
    if(sessionStorage.page == 1){
        $(".prev").hide();
    }else{
        $(".prev").show();
    }
    
    if(parseInt(sessionStorage.page) == lastPageNumber){
        $(".next").hide();
    }else{
        $(".next").show();
    }
    
    setupClicks();
    setupPaginationClicks();
};

function handlePageNumber(){
    $("#page_info").text(`${sessionStorage.page} of ${lastPageNumber}`);
}
            
function splitActors(actors, star_ids){
    var stars = actors.split(",");
    var ids = star_ids.split(",");
    var result = `<a class='stars' id='${ids[0]}'>${stars[0]}</a>`;
    for(var i=1; i < stars.length; i++){
        result += `, <a class='stars' id='${ids[i]}'>${stars[i]}</a>`;
    }
    return result;
}

function splitGenres(genres){
    var listOfGenres = genres.split(",");
    var result = "<a class='genre' id='"+ listOfGenres[0] +"'>" + listOfGenres[0] + "</a>";
    for(var i=1; i < listOfGenres.length; i++){
        result += ", <a class='genre' id='"+ listOfGenres[i] +"'>" + listOfGenres[i] + "</a>";
    }
    return result;
}

function setupClicks(){
    $(".stars").click(function(e){
        e.preventDefault();
        localStorage.setItem("star_selected",e.target.id);
        location.replace("star.html")
    });
    
    $(".title").click(function(e){
        e.preventDefault();
        localStorage.setItem("movie_selected",e.target.id);
        console.log(e.target.id);
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
                           
function setupPaginationClicks(){
    $("button.next").click(function(e){
        sessionStorage.page = 1 + parseInt(sessionStorage.page);
        var data = JSON.parse(localStorage.dataToPost);
        data.offset = parseInt(sessionStorage.page) - 1;
        data.limit = n;
        localStorage.dataToPost = JSON.stringify(data);
        reset = false;
        location.replace("movieList.html");
    });
                           
    $("button.prev").click(function(e){
        sessionStorage.page = parseInt(sessionStorage.page) - 1;
        var data = JSON.parse(localStorage.dataToPost);
        data.offset = parseInt(sessionStorage.page) - 1;
        data.limit = n;
        localStorage.dataToPost = JSON.stringify(data);
        reset = false;
        location.replace("movieList.html");
    });
}

$("document").ready(function(e){
    var alphanums = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(',');
    var letters = $("#letter_list");
    
    for (var i=0; i<alphanums.length; i++){
        var temp = alphanums[i];
        letters.append("<li class='letter_button' id='"+temp+"'>"+temp+"</li>");
    }
    
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
    
    $(".sortOption").click(function(e){
        e.preventDefault();
        sessionStorage.sorting = e.target.id;
        location.replace("movieList.html");
    });
    
    $(".results").click(function(e){
        e.preventDefault();
        sessionStorage.n = e.target.id;
        var data = JSON.parse(localStorage.dataToPost);
        data.offset = 0;
        data.limit = sessionStorage.n;
        localStorage.dataToPost = JSON.stringify(data);
        location.replace("movieList.html");
    });
});

if (localStorage.linkToCall && localStorage.dataToPost){
    jQuery.ajax({
            type: "POST",
            dataType: "json",
            data: JSON.parse(localStorage.dataToPost),
            url: localStorage.linkToCall,
            success: resultData => handleList(resultData)
    });
}else{
    $("#not_found_message").show();
}


    