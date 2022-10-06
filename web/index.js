/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movie list table from resultData");

    // Populate the movie list table
    // Find the empty table body by id "movie_list_table_body"
    let movieListTableBodyElement = jQuery("#movie_list_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>"
        rowHTML += "<td>" +
            "<a href='movie.html?id=" + resultData[i]["movie_id"] + "'>" +
            resultData[i]["movie_title"] +
            "</a>" +
            "</td>";
        rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
        rowHTML += "<td>" + "Tempory" + "</td>";
        rowHTML += "<td>" + "Tempory" + "</td>";
        rowHTML += "<td>" + "Tempory" + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movie-list",
    success: (resultData) => handleMovieListResult(resultData)
});