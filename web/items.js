let cart = $("#cart");
const unit_price = 4.99;
/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let items_table_body = $("#items_table_body");
    var total_cost = 0;
    // change it to html list
    let res = "";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        var movieData;
        $.ajax({
            dataType: "json",
            method: "GET",
            url: "api/movie?id=" + resultArray[i],
            async: false,
            success: function (resultData){
                movieData = resultData;
            }
        });
        res += "<tr>";
        res += "<td>" + movieData[0]["movie_title"] + "</td>";
        res += "<td>" + unit_price + "</td>";
        res += "<td>" + "<input type='number' value='1' min='1' onblur='findTotal()' name='qty'" +
            "oninput='this.value = Math.abs(this.value)' " + "</td>";
        res += "<td>" + "remove" + "</td>";
        res += "</tr>";
        total_cost += unit_price ;
    }
    $("#total_cost").append((Math.round(total_cost*100)/100).toString());   //Display total cost

    res += "";
    // clear the old array and show the new array in the frontend
    items_table_body.html("");
    items_table_body.append(res);
}

// Call this function every time user changes an item QTY and recompute total_cost
function findTotal() {
    console.log("Update total_cost");
    var array = document.getElementsByName('qty');
    var totalCost=0;
    for(var i=0;i<array.length;i++){
        if(parseInt(array[i].value))
            totalCost += parseInt(array[i].value) * unit_price;
    }
    console.log("New cost = ", totalCost);
    document.getElementById('total_cost').innerText = "Total Cost: ";
    document.getElementById('total_cost').append((Math.round(totalCost*100)/100).toString())
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/items", {
        method: "POST",
        data: cart.serialize()
        // success: resultDataString => {
        //     let resultDataJson = JSON.parse(resultDataString);
        //     handleCartArray(resultDataJson["previousItems"]);
        // }
    });

    // clear input form
    cart[0].reset();
}

// Get item id (movie id) from URL
let movieId = getParameterByName("newItem")

$.ajax("api/items", {
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a event handler function
cart.submit(handleCartInfo);

// jQuery.ajax({
//     dataType: "json",
//     method: "GET",
//     url: "api/items?newItem=" + movieId,
//     success: (resultData) => handleItemsResult(resultData)
// });