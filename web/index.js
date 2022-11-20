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

        rowHTML += "<td><button form='cart' type='submit' onclick='handleAddMovie(\""
        + resultData[i]["movie_id"] + "\")'>Add to Cart</button></td>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieListTableBodyElement.append(rowHTML);
    }
}

function handleAddMovie(movie_id) {
    $.ajax({
        url: "api/items?action=increment&movie_id=" + movie_id,
        method: "POST",
        success: () => {
            cart[0].reset();
            alert("Movie Added to Cart");
        }
    });

    document.location.reload();
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
        + "&orderFirst=" + listOrderFirst
        + "&orderSecond=" + listOrderSecond
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
        + "&orderFirst=" + listOrderFirst
        + "&orderSecond=" + listOrderSecond
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
 * simple waiting function. Useful for debugging
 * @param milliseconds
 */
function sleep(milliseconds) {
    const date = Date.now();
    let currentDate = null;
    do {
        currentDate = Date.now();
    } while (currentDate - date < milliseconds);
}

/**
 * Submits form content containing search title
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("ping");
    console.log("submit search form");
    sleep(1000);
    console.log("proceed");
    formSubmitEvent.preventDefault();
    window.location.replace("index.html?" + searchForm.serialize());
}

/**
 * Orders movie list according to given parameters
 * @param column Column to order the movie list by
 * @param orderFirst Order to order the first column by
 * @param orderSecond Order to order the second column by
 */
function orderBy(column, orderFirst, orderSecond) {
    window.location.replace("index.html?limit=" + listLimit
        + "&criteria=" + column
        + "&orderFirst=" + orderFirst
        + "&orderSecond=" + orderSecond
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
        + "&orderFirst=" + listOrderFirst
        + "&orderSecond=" + listOrderSecond
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
let listOrderFirst = getParameterByName("orderFirst");
let listOrderSecond = getParameterByName("orderSecond");
let listPage = getParameterByName("page");
let listSearchTitle = getParameterByName("searchTitle");
let listSearchYear = getParameterByName("searchYear");
let listSearchDirector = getParameterByName("searchDirector");
let listSearchStar = getParameterByName("searchStar");
let listBrowseGenre = getParameterByName("browseGenre");
let listBrowseTitle = getParameterByName("browseTitle");

if (listLimit == null) {listLimit = "20"};
if (listCriteria == null) {listCriteria = "rating"};
if (listOrderFirst == null) {listOrderFirst = "desc"};
if (listOrderSecond == null) {listOrderSecond = "asc"};
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
        + "&orderFirst=" + listOrderFirst
        + "&orderSecond=" + listOrderSecond
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
        + "&orderFirst=" + listOrderFirst
        + "&orderSecond=" + listOrderSecond
        + "&page=" + (parseInt(listPage) + 1).toString()
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar
        + "&browseGenre=" + listBrowseGenre
        + "&browseTitle=" + listBrowseTitle,
    success: (resultData) => handleMovieListPaginationResult(resultData)
});

// Movie list back
jQuery.ajax({
    dataType: "json",
    method: "POST",
    url: "api/back?limit=" + listLimit
        + "&criteria=" + listCriteria
        + "&orderFirst=" + listOrderFirst
        + "&orderSecond=" + listOrderSecond
        + "&page=" + listPage
        + "&searchTitle=" + listSearchTitle
        + "&searchYear=" + listSearchYear
        + "&searchDirector=" + listSearchDirector
        + "&searchStar=" + listSearchStar
        + "&browseGenre=" + listBrowseGenre
        + "&browseTitle=" + listBrowseTitle,
});



/**
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated with query=" + query)
    console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint api/movie-list
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "api/movie-list?limit=10"
        + "&criteria=rating"
        + "&orderFirst=desc"
        + "&orderSecond=asc"
        + "&page=1"
        + "&searchTitle=" + query
        + "&searchYear="
        + "&searchDirector="
        + "&searchStar="
        + "&browseGenre="
        + "&browseTitle=",
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}


/**
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = data[0]["movie_title"];
    console.log(jsonData)   // test print to prove data was fetched

    // TODO: if you want to cache the result into a global variable you can do it here

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/**
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["heroID"])
}


/**
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */

// put 'country' after the 'lookup : ' field in .autocomplete() below and Andorra will show up properly
    //    in the autocomplete. having trouble getting this to work with function()
var country = [{ value: 'Andorra', data : 'AD'}]
// $('#search-input') is to find element by the ID "autocomplete"
$('#search-input').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup:
        function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 1
});


/**
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
    //  Note: Technically project 2's implementation of submitSearchForm line 156 already does handles this TODO
}

// bind pressing enter key to a handler function
$('#search_form').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#search-input').val())
    }
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button
//  Note: Technically project 2's implementation of submitSearchForm line 156 already does handles this TODO
