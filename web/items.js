console.log("Running items.js");
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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleItemsResult(resultData) {
    console.log("handleItemsResult: populating items table from resultData");

    let itemsTableBodyElement = jQuery("#items_table_body");
    let rowHTML = "";
    rowHTML += "<tr>"
    rowHTML += "<td>quantity placeholder</td>";
    rowHTML += "<td>remove placeholder</td>";
    rowHTML += "<td>quantity placeholder</td>";
    rowHTML += "<td>remove placeholder</td>";
    rowHTML += "</tr>";
    itemsTableBodyElement.append(rowHTML);

    for (let i = 0; i < resultData.length; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>"
        rowHTML += "<td>" + resultData[i]["movie_title"] + "</td>";
        rowHTML += "<td>" + unit_price + "</td>";
        rowHTML += "<td>" + "quantity placeholder" + "</td>";
        rowHTML += "<td>" + "remove placeholder" + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        itemsTableBodyElement.append(rowHTML);
    }
}

// Get item id (movie id) from URL
let movieId = getParameterByName("newItem")

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/items?newItem=" + movieId,
    success: (resultData) => handleItemsResult(resultData)
});