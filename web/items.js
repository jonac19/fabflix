let remove = $("#remove");
const unit_price = 4.99;
//Make a local dictionary considered to be the truth of movie counts in session. update later w/this
var dict = new Object();

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
    items_table_body.html("");

    for (let i = 0; i < resultArray.length; i++) {
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
        if ( dict[ movieData[0]["movie_id"] ] == null ){
            dict[ movieData[0]["movie_id"] ] = [ movieData[0]["movie_title"], 1 ];  //new entry in dict
        } else {
            dict[ movieData[0]["movie_id"] ][1]++;  //entry exists in dict, incr count++
        }
    }

    for (const [key, value] of Object.entries(dict)){
        res = "";
        // each item will be in a bullet point
        // var movieData;
        // $.ajax({
        //     dataType: "json",
        //     method: "GET",
        //     url: "api/movie?id=" + resultArray[i],
        //     async: false,
        //     success: function (resultData){
        //         movieData = resultData;
        //     }
        // });
        // console.log("entry: " + resultArray[i]);
        res += "<tr>";
        res += "<td>" + value[0] + "</td>"; //movie title
        res += "<td>" + unit_price + "</td>";
        res += "<td>" + "<input type='number' value='" + value[1] +"' min='1' onblur='findTotal()' name='qty'" +
            "oninput='this.value = Math.abs(this.value)' " + "</td>";
        res += "<td>" + "<button form='remove' type='submit' " +
            "onclick='buttonRemove(\"" + key + "\")'>Remove</button>";

        res += "</tr>";
        total_cost += unit_price ;

        items_table_body.append(res);

    }
    $("#total_cost").append((Math.round(total_cost*100)/100).toString());   //Display total cost

    for (const[key, value] of Object.entries(dict)){
        $.ajax("api/items?item=" + key, {
            method: "POST"
        });
    }

}

function buttonRemove( movieID ){
    alert("Removed item");
    console.log("Pressed inline remove button with: remove" + movieID);
    document.getElementById("input").value = "remove" + movieID;
    remove.submit(handleRemovalRequest());
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
    document.getElementById('total_cost').innerText = "Total Cost: $";
    document.getElementById('total_cost').append((Math.round(totalCost*100)/100).toString())
}

function handleRemovalRequest(removeEvent) {
    console.log("submit removal form");
    /**
     * When users click the remove button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    // removeEvent.preventDefault();

    $.ajax("api/items", {
        method: "POST",
        data: remove.serialize()
        // success: resultDataString => {
        //     let resultDataJson = JSON.parse(resultDataString);
        //     handleCartArray(resultDataJson["previousItems"]);
        // }
    });

}
/**
 * Submit form content with POST method
 * @param cartEvent
 */

// Get item id (movie id) from URL
let movieId = getParameterByName("newItem")

$.ajax("api/items", {
    method: "GET",
    success: handleSessionData
});

function purchaseFuncFlush() {
    //Empty out the backend's session's cart contents one last time
    $.ajax("api/items?flush=true", {
        method: "POST",
        success: {purchaseFuncFill}
    });
}

function purchaseFuncFill(){
    //put dict contents into backend session one last time before going to Purchase Page
    for (const[key, value] of Object.entries(dict)){
        $.ajax("api/items?item=" + key, {
            method: "POST"
        });
    }

}