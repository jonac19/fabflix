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
function handleMovieListResult(resultData) {
    console.log("handleMovieListResult: populating movie list table from resultData");

    // Populate the movie list table
    // Find the empty table body by id "movie_list_table_body"
    let movieListTableBodyElement = jQuery("#movie_list_table_body");

    // Iterate through resultData
    for (let i = 0; i < Math.min(resultData.length); i++) {
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

        // Concatenate the genres associated with each movie
        rowHTML += "<td>";
        for (let j = 0; j < Math.min(3, resultData[i]["movie_genres"].length); j++) {
            rowHTML += resultData[i]["movie_genres"][j]["genre_name"];

            if (j < Math.min(3, resultData[i]["movie_genres"].length) - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</td>";

        // Concatenate the stars associated with each movie
        rowHTML += "<td>";
        for (let j = 0; j < Math.min(3, resultData[i]["movie_stars"].length); j++) {
            rowHTML +=
                "<a href='star.html?id=" + resultData[i]["movie_stars"][j]["star_id"] + "'>" +
                resultData[i]["movie_stars"][j]["star_name"] +
                "</a>";

            if (j < Math.min(3, resultData[i]["movie_stars"].length) - 1) {
                rowHTML += ", ";
            }
        }
        rowHTML += "</td>";

        rowHTML += "<td>" + resultData[i]["movie_rating"] + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);
    }
}

/**
 * Submits form content containing search title
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("submit search form");
    formSubmitEvent.preventDefault();
    window.location.replace("index.html?" + searchForm.serialize());
}

/**
 * Orders movie list according to given parameters
 * @param column Column to order the movie list by
 * @param order Order to order the movie list by
 */
function orderBy(column, order) {
    console.log("sorting movie list");
    window.location.replace("index.html?criteria=" + column
        + "&order=" + order
        + "&limit=" + listLimit
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar);
}

let searchForm = jQuery("#search_form");
searchForm.submit(submitSearchForm);

// Get movie list parameters from URL
let listLimit = getParameterByName("limit");
let listCriteria = getParameterByName("criteria");
let listOrder = getParameterByName("order");
let listSearchTitle = getParameterByName("searchTitle");
let listSearchYear = getParameterByName("searchYear");
let listSearchDirector = getParameterByName("searchDirector");
let listSearchStar = getParameterByName("searchStar");

if (listLimit == null) {listLimit = ""};
if (listCriteria == null) {listCriteria = ""};
if (listOrder == null) {listOrder = ""};
if (listSearchTitle == null) {listSearchTitle = ""};
if (listSearchYear == null) {listSearchYear = ""};
if (listSearchDirector == null) {listSearchDirector = ""};
if (listSearchStar == null) {listSearchStar = ""};


jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movie-list?criteria=" + listCriteria
         + "&order=" + listOrder
         + "&limit=" + listLimit
         + "&searchTitle=" + listSearchTitle
         + "&searchYear=" + listSearchYear
         + "&searchDirector=" + listSearchDirector
         + "&searchStar=" + listSearchStar,
    success: (resultData) => handleMovieListResult(resultData)
});