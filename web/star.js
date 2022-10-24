/*
   Front-back end separation is followed in this file
   Steps of this .js
    1) Get parameter from request URL so it know which id to look for
    2) Use jQuery to talk to backend API to get the json data.
    3) Populate the data to correct html elements.
*/


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName( target ) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "//$&"); //what

    // Use regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if ( !results ) return null;
    if ( !results[2] ) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleResult( resultData ) {
    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>" +
        "<p>Date of Birth: " + resultData[0]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for ( let i=0; i< Math.min( 10, resultData.length ); i++ ){
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" +
            "<a href='movie.html?id=" + resultData[i]["movie_id"] + "'>" +
            resultData[i]["movie_title"] +
            "</a>" +
            "</td>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append( rowHTML );
    }
}

function handleBackResult(resultData) {
    let starBackNavElement = jQuery("#star_back_nav_element");

    let anchorHTML = "<a class='btn btn-outline-warning' href='"
        + resultData['backURL']
        + "'>Back</a>";

    starBackNavElement.append(anchorHTML);
}

// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",   // Set return data type
    method: "GET",      // Set request method
    url: "api/star?id=" + starId,    // Set request url, mapped by StarsServlet in Stars.java
    success: ( resultData ) => handleResult( resultData )   // Set callback function to handle data returned
                                                            //  successfully by the SingleStarServlet
});

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/back",
    success: (resultData) => handleBackResult(resultData)
});