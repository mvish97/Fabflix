

$(document).ready( function(){
    $("#search").click(function(e){
        e.preventDefault();
        window.location.replace("./search.html");
    });
});

if (sessionStorage.name){
    if(!sessionStorage.page){
        sessionStorage.setItem("page", parseInt(1));
    }
    if(!sessionStorage.sorting){
        sessionStorage.setItem("sorting", "asc_title");
    }
    if(!sessionStorage.n){
        sessionStorage.setItem("n", 10);
    }
    if(!sessionStorage.cart){
        sessionStorage.setItem("cart","[]");
    }

    $("#welcome").text("Welcome back " + sessionStorage.name + "!");
}
