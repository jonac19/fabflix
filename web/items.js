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
    // change it to html list
    let res = "";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<tr>";
        res += "<td>" + resultArray[i] + "</td>";
        res += "<td>" + unit_price + "</td>";
        res += "<td>" + 1 + "</td>";
        res += "<td>" + "remove" + "</td>";
        res += "</tr>";
    }
    res += "";

    // clear the old array and show the new array in the frontend
    items_table_body.html("");
    items_table_body.append(res);
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

// /**
//  * Handles the data returned by the API, read the jsonObject and populate data into html elements
//  * @param resultData jsonObject
//  */
// function handleItemsResult(resultData) {
//     console.log("handleItemsResult: populating items table from resultData");
//
//     let itemsTableBodyElement = jQuery("#items_table_body");
//     let rowHTML = "";
//     rowHTML += "<tr>"
//     rowHTML += "<td>1 placeholder</td>";
//     rowHTML += "<td>2 placeholder</td>";
//     rowHTML += "<td>3 placeholder</td>";
//     rowHTML += "<td>4 placeholder</td>";
//     rowHTML += "</tr>";
//     itemsTableBodyElement.append(rowHTML);
//     console.log("resultData.length = ");
//     console.log(resultData.length);
//     console.log(resultData["previousItems"]);
//     console.log(resultData["previousItems"].length);
//
//     for (let i = 0; i < resultData["previousItems"].length; i++) {
//         console.log("loop for item table entry");
//         // Concatenate the html tags with resultData jsonObject
//         let rowHTML = "";
//         rowHTML += "<tr>"
//         rowHTML += "<td>" + resultData["previousItems"][i] + "</td>";
//         rowHTML += "<td>" + unit_price + "</td>";
//         rowHTML += "<td>" + "1" + "</td>";
//         rowHTML += "<td>" + "remove" + "</td>";
//         rowHTML += "</tr>";
//
//         // Append the row created to the table body, which will refresh the page
//         itemsTableBodyElement.append(rowHTML);
//     }
// }
//
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