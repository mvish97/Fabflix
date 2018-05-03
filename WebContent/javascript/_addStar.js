
function processInsertion(){
    var name = $("#name input").val();
    var year = $("#dob input").val();
    
    if (!name){
        alert("Please fill the name field at least!");
        location.reload();
    }
    
    if (!year){
        year = null;
    }
    
    var dataToPost = {
        "name": name,
        "birthYear": year
    }
    
    jQuery.ajax({
        type: "POST",
        dataType: "json",
        data: dataToPost,
        url: sessionStorage.baseurl+"/project2/addNewStar",
        success: resultData => handleResponse(resultData)
    });
}

function handleResponse(resultData){
    if (resultData["result"]){
        alert("The insertion was successful!");
    }else{
        alert("Looks like there was an error while inserting the record.");
    }
}