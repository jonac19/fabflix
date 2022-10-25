let cart = $("#cart");
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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 1 entries
    for (let i = 0; i < Math.min(1, resultData.length); i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>"
        rowHTML += "<td>" + resultData[i]["movie_title"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";

        // Concatenate the genres associated with each movie
        rowHTML += "<td>";
        for (let j = 0; j < resultData[i]["movie_genres"].length; j++) {
            rowHTML += resultData[i]["movie_genres"][j]["genre_name"];

            if (j < resultData[i]["movie_genres"].length - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</td>";

        // Concatenate the stars associated with each movie
        rowHTML += "<td>";
        for (let j = 0; j < resultData[i]["movie_stars"].length; j++) {
            rowHTML +=
                "<a href='star.html?id=" + resultData[i]["movie_stars"][j]["star_id"] + "'>" +
                resultData[i]["movie_stars"][j]["star_name"] +
                "</a>";

            if (j < resultData[i]["movie_stars"].length - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</td>";

        rowHTML += "<td>" + resultData[i]["movie_rating"] + "</td>";

        rowHTML += "<td>";
        rowHTML += "<button id='button" + i.toString() + "' form='cart' type='submit'>Buy</button>";
        rowHTML += "</td>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);

        // On CLICK, set the Value in the Form object and Submit it.
        // Since this func is jQuery, must be done AFTER rowHTML has been appended to frontend display
        $('#button' + i.toString()).click(function(){
            console.log("Pressed inline purchase button");
            document.getElementById("item").value = resultData[i]["movie_id"];
            document.getElementById("cart").click();
        })

        //OLD: Used for setting the Input's Value on a single static Form object before user Clicks Submit
        // set the Buy Movie button "value" to be id of this movie
        //document.getElementById("item").value = resultData[i]["movie_id"];
    }
}

// function handleBuyEvent(buyEvent) {
//     console.log("submit buy form");
//     /**
//      * When users click the buy button, the browser will not direct
//      * users to the url defined in HTML form. Instead, it will call this
//      * event handler when the event is triggered.
//      */
//     buyEvent.preventDefault();
//
//     $.ajax("api/items", {
//         method: "POST",
//         data: buy.serialize()
//         // success: resultDataString => {
//         //     let resultDataJson = JSON.parse(resultDataString);
//         //     handleCartArray(resultDataJson["previousItems"]);
//         // }
//     });
//     buyEvent.preventDefault();
// }


// Get movie id from URL
let movieId = getParameterByName("id")

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movie?id=" + movieId,
    success: (resultData) => handleMovieResult(resultData)
});

// let buy = $("#buy");
// buy.submit(handleBuyEvent);

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

cart.submit(handleCartInfo);