

$(".box").show();
$(".buy button").show();
$("h1.complete").hide();

function processPurchase(){
    var ccno = $("#ccno input").val();
    var fn = $("#fn input").val();
    var ln = $("#ln input").val();
    var exp = $("#exp input").val();
    
    if(!ccno || !fn || !ln || !exp){
        alert("Please fill-in all the fields.");
        return;
    }
    
    var objs = JSON.parse(sessionStorage.cart);
    var movieids = objs[0].id;
    var qtys = objs[0].qty;
    for(var i=1; i<objs.length; i++){
        movieids += "," + objs[i].id;
        qtys += "," + objs[i].qty;
    }
    
    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1;
    
    if(dd<10){
        dd='0'+dd;
    } 
    if(mm<10){
        mm='0'+mm;
    } 
    today = `${today.getFullYear()}-${mm}-${dd}`;
    
    var dataToPost = {
            id : ccno,
            firstName : fn,
            lastName : ln,
            expiration : exp,
            customerId : sessionStorage.cid,
            movieId : movieids,
            date : today,
            quantity : qtys
        }
    console.log(dataToPost);
    
    jQuery.ajax({
        type: "POST",
        dataType: "json",
        data: dataToPost,
        url: sessionStorage.baseurl+"/project2/checkout",
        success: resultData => handleResponse(resultData)
    });
}

function handleResponse(resultData){
    console.log(resultData);
    if(!resultData.valid){
        alert("Details do not match!");
        location.reload();
    }else{
        $(".box").hide();
        $(".buy button").hide();
        $("h1.complete").show();
        sessionStorage.cart = "[]";
    }
}