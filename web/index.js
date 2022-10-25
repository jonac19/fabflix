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
function handleMovieListResult(resultData) {
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
            rowHTML += "<a href='index.html?browseGenre=" + resultData[i]["movie_genres"][j]["genre_id"] + "'>" +
                resultData[i]["movie_genres"][j]["genre_name"] +
                "</a>"

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

        rowHTML +="<td><button form='cart' type='submit' onclick='buttonBuy(\""
        + resultData[i]["movie_id"] + "\")'>Buy</button>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);
    }
}

function handleMovieListPaginationResult(resultData) {
    let movieListPaginationElement = jQuery("#movie_list_pagination");

    let prevHTML = "<li class='page-item ";
    if (listPage == "1") {
        prevHTML += "disabled'";
    }
    prevHTML += "'><a class='page-link' href="
        + "index.html?limit=" + listLimit
        + "&criteria=" + listCriteria
        + "&order=" + listOrder
        + "&page=" + (parseInt(listPage) - 1).toString()
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar
        + "&browseGenre=" + listBrowseGenre
        + "&browseTitle=" + listBrowseTitle;

    prevHTML += ">Previous Page</a></li>";
    movieListPaginationElement.append(prevHTML);

    let nextHTML = "<li class='page-item ";
    if (resultData.length == 0) {
        nextHTML += "disabled'";
    }
    nextHTML += "'><a class='page-link' role='button' href="
        + "index.html?limit=" + listLimit
        + "&criteria=" + listCriteria
        + "&order=" + listOrder
        + "&page=" + (parseInt(listPage) + 1).toString()
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar
        + "&browseGenre=" + listBrowseGenre
        + "&browseTitle=" + listBrowseTitle;
    nextHTML += ">Next Page</a></li>";
    movieListPaginationElement.append(nextHTML);
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
    window.location.replace("index.html?limit=" + listLimit
        + "&criteria=" + column
        + "&order=" + order
        + "&page=1"
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar
        + "&browseGenre=" + listBrowseGenre
        + "&browseTitle=" + listBrowseTitle);
}

/**
 * Changes the number of listings in the movie list according to given parameter
 * @param number Number of listings in the movie lsit
 */
function listings(number) {
    window.location.replace("index.html?limit=" + number
        + "&criteria=" + listCriteria
        + "&order=" + listOrder
        + "&page=1"
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar
        + "&browseGenre=" + listBrowseGenre
        + "&browseTitle=" + listBrowseTitle);
}

let searchForm = jQuery("#search_form");
searchForm.submit(submitSearchForm);

// Get movie list parameters from URL
let listLimit = getParameterByName("limit");
let listCriteria = getParameterByName("criteria");
let listOrder = getParameterByName("order");
let listPage = getParameterByName("page");
let listSearchTitle = getParameterByName("searchTitle");
let listSearchYear = getParameterByName("searchYear");
let listSearchDirector = getParameterByName("searchDirector");
let listSearchStar = getParameterByName("searchStar");
let listBrowseGenre = getParameterByName("browseGenre");
let listBrowseTitle = getParameterByName("browseTitle");

if (listLimit == null) {listLimit = "20"};
if (listCriteria == null) {listCriteria = "rating"};
if (listOrder == null) {listOrder = "desc"};
if (listPage == null) {listPage = "1"};
if (listSearchTitle == null) {listSearchTitle = ""};
if (listSearchYear == null) {listSearchYear = ""};
if (listSearchDirector == null) {listSearchDirector = ""};
if (listSearchStar == null) {listSearchStar = ""};
if (listBrowseGenre == null) {listBrowseGenre = ""};
if (listBrowseTitle == null) {listBrowseTitle = ""};

// Movie list table
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movie-list?limit=" + listLimit
        + "&criteria=" + listCriteria
        + "&order=" + listOrder
        + "&page=" + listPage
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar
        + "&browseGenre=" + listBrowseGenre
        + "&browseTitle=" + listBrowseTitle,
    success: (resultData) => handleMovieListResult(resultData)
});

// Movie list pagination
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movie-list?limit=" + listLimit
        + "&criteria=" + listCriteria
        + "&order=" + listOrder
        + "&page=" + (parseInt(listPage) + 1).toString()
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar
        + "&browseGenre=" + listBrowseGenre
        + "&browseTitle=" + listBrowseTitle,
    success: (resultData) => handleMovieListPaginationResult(resultData)
});

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

function buttonBuy( movieID ){
    alert("Movie added to cart");
    console.log("Pressed inline Buy button for movieID: " + movieID);
    document.getElementById("item").value = movieID;
    cart.submit(handleCartInfo());
}

// Movie list back
jQuery.ajax({
    dataType: "json",
    method: "POST",
    url: "api/back?limit=" + listLimit
        + "&criteria=" + listCriteria
        + "&order=" + listOrder
        + "&page=" + listPage
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar
        + "&browseGenre=" + listBrowseGenre
        + "&browseTitle=" + listBrowseTitle,
});

cart.submit(handleCartInfo);

