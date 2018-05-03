var oldQueries = {}

if (!sessionStorage.oldQueries){
    sessionStorage.setItem("oldQueries", "{}");
}else{
    oldQueries = JSON.parse(sessionStorage.oldQueries)
}

console.log(oldQueries);

$('.box form input#autocomplete').autocomplete({
    lookup: function (query, doneCallback) {
        if(query.length >= 3){
            handleLookup(query, doneCallback)
        }
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    groupBy: "category",
    deferRequestBy: 300,
});

function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	doneCallback( { suggestions: data } );
}

function handleSelectSuggestion(suggestion) {
	
	console.log("you select " + suggestion.data.id);
    saveQueries();
    if (suggestion.data.category == "Movies"){
        localStorage.setItem("movie_selected", suggestion.data.id);
        location.replace("movie.html");
    } else{
        localStorage.setItem("star_selected", suggestion.data.id);
        location.replace("star.html")
    }
}

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    
    if (query in oldQueries){
        console.log("Handled with Cache");
        var oldData = oldQueries[query]
        console.log(oldData);
        handleLookupAjaxSuccess(oldData, query, doneCallback)
    }else{
        console.log("Handled with Ajax")
        jQuery.ajax({
            "method": "GET",
            "url": "/project2/autoCompleteSearch?userInput=" + escape(query),
            "success": function(data) {
                oldQueries[query] = data;
                console.log(data);
                handleLookupAjaxSuccess(data, query, doneCallback) 
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
	   })
    }	
}

$('.box form input#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
    
	if (event.keyCode == 13) {
        event.preventDefault();
		// pass the value of the input box to the hanlder function
		handleNormalSearch($('.box form input#autocomplete').val())
	}
})

function handleNormalSearch(query){
    var dataToPost = {
        "query": "",
        "offset": 0,
        "limit": sessionStorage.n
    };
    
    dataToPost.query =  query;
    localStorage.setItem("dataToPost", JSON.stringify(dataToPost));
    localStorage.setItem("linkToCall", sessionStorage.baseurl+"/project2/normalSearch");
    saveQueries();
    location.replace("movieList.html");
}

function saveQueries(){
    sessionStorage.oldQueries = JSON.stringify(oldQueries)
}