

function processInsertion(){
    var m_title = $("#m_title input").val();
    var m_year = $("#m_year input").val();
    var m_director = $("#m_director input").val();
    var s_name = $("#s_name input").val();
    var s_year = $("#s_year input").val();
    var g_name = $("#g_name input").val();
    
    if (!m_title || !m_year || !m_director){
        alert("Please fill all the required details");
        return;
    }
    
    var dataToPost = {
        "m_title" : m_title,
        "m_year" : m_year,
        "m_director" : m_director,
        "s_name": s_name,
        "s_year": s_year,
        "g_name": g_name
    }
    
    jQuery.ajax({
        type: "POST",
        dataType: "json",
        data: dataToPost,
        url: sessionStorage.baseurl+"/project2/addMovie",
        success: resultData => handleResponse(resultData)
    });
}

function handleResponse(resultData){
    if (resultData.result){
        alert("The insertion was successful!");
    }else{
        alert(resultData["err"]);
    }
}