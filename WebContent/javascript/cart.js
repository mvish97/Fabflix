
$('document').ready(function(e){
    console.log(sessionStorage.cart);
    
    $("div.cart_box").append(`
        <ul>
            ${processCart()}
        </ul>
        <span class="checkout_btn"><a href="checkout.html"><button class='checkout'>Checkout</button></a></span>`)
    
    if(JSON.parse(sessionStorage.cart).length > 0){
        $("span").show();
    }else{
        $("span").hide();
    }
    
    $(".remove").click(function(e){
        e.preventDefault();
        var objs = JSON.parse(sessionStorage.cart);
        var indexToRemove = 0;
        for(var i = 0; i < objs.length; i++) {
            if (objs[i].id == e.target.id) {
                indexToRemove = i;
                break;
            }
        }
        
        objs.splice(indexToRemove,1);
            
        sessionStorage.cart = JSON.stringify(objs);
        location.replace('cart.html');
    })
    
    $(".add").click(function(e){
        var qty = $(`input#${e.target.id}`);
        e.preventDefault();
        var objs = JSON.parse(sessionStorage.cart);
        for(var i = 0; i < objs.length; i++) {
            if (objs[i].id == e.target.id) {
                objs[i].qty += 1;
                qty.text(objs[i].qty);
                console.log(objs);
                break;
            }
        }
          
        sessionStorage.cart = JSON.stringify(objs);
        location.reload();
    })
    
    $(".sub").click(function(e){
        var qty = $(`input#${e.target.id}`);
        e.preventDefault();
        var objs = JSON.parse(sessionStorage.cart);
        for(var i = 0; i < objs.length; i++) {
            if (objs[i].id == e.target.id) {
                objs[i].qty -= 1;
                qty.text(objs[i].qty);
                if(objs[i].qty == 0){
                    objs.splice(i,1);
                }
                console.log(objs);
                break;
            }
        }
            
        sessionStorage.cart = JSON.stringify(objs);
        location.reload();
    })
});

function processCart(){
    var objs = JSON.parse(sessionStorage.cart);
    var result = ``;
    
    for (var i=0; i < objs.length; i++){
        result += 
        `<li>
            <span>${objs[i].title}</span>
            <button class='remove' id='${objs[i].id}'>x</button>
            <button class="add" id='${objs[i].id}'>+</button>
            <input id='${objs[i].id}' placeholder="Qty" value='${objs[i].qty}' class='qty_input' disabled> 
            <button class="sub" id='${objs[i].id}'>-</button>
        </li><hr>`
    }
    return result;
}