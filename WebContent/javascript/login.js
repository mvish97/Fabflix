var form = document.getElementById("login_form");
sessionStorage.baseurl = "";

form.addEventListener("submit",function(event){
	
	$(document).ajaxError(function(e, xhr, opt){
        alert("Error requesting " + opt.url + ": " + xhr.status + " " + xhr.statusText);
    });
    
    var recapData = form["g-recaptcha-response"].value;
    
//    if(!recapData){
//        alert("Please complete the recaptcha.");
//        window.location.replace("./login.html");
//    }
    
    if (!$("#email").val() || !$("#password").val())
    {
        alert("Please fill-in the fields.");
        window.location.replace("./login.html");
    }
    
    var dataToPost = {
        "email": $("#email").val(),
        "password": $("#password").val(),
        "isMobile": false,
        "g-recaptcha-response" : recapData
    };
    
    var url = sessionStorage.baseurl+"/project2/login";
    
    console.log(url);
    console.log(dataToPost);
    
    $.ajax({
        type: "POST",
        data: dataToPost,
        url: url,
        success: (resultData) => handleLogin(resultData)
    });
    
    event.preventDefault();
    
});
    
function handleLogin(resultData){
	console.log(resultData);
    if (resultData["result"] == true){
        console.log(resultData);
        sessionStorage.setItem("is_employee", resultData.is_employee);
        sessionStorage.setItem("name", resultData.name);
        sessionStorage.setItem("id", resultData.id);
        if (sessionStorage.is_employee == "false"){
            window.location.replace("./main.html");
        }
        else{
            window.location.replace("./_dashboard.html");
        }
    }
    else{
        $("#error_message").text("Oops looks like there was an error!");
    }
}

