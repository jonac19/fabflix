/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleBrowseGenreResult(resultData) {
    console.log("handleBrowseGenreResult: populating browse genre row from resultData");

    let browseGenreRowElement = jQuery("#browse_genre_row");

    for (let i = 0; i < resultData.length; i++) {
        let colHTML = "";
        colHTML += "<a class='col-3' href='index.html?browseGenre=" + resultData[i]['genre_id'] + "'>" +
            resultData[i]['genre_name'] +
            "</a>";

        browseGenreRowElement.append(colHTML);
    }
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleBrowseTitleResult(resultData) {
    console.log("handleBrowseTitleResult: populating browse title row from resultData");

    let browseTitleRowElement = jQuery("#browse_title_row");

    asteriskHTML = "";
    asteriskHTML += "<a class='col-3' href='index.html?browseTitle=*'>*</a>";
    browseTitleRowElement.append(asteriskHTML);

    for (let i = 0; i < resultData.length; i++) {
        let colHTML = "";
        colHTML += "<a class='col-3' href='index.html?browseTitle=" + resultData[i]['title_letter'] + "'>" +
            resultData[i]['title_letter'] +
            "</a>";

        browseTitleRowElement.append(colHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/browse?criteria=genre",
    success: (resultData) => handleBrowseGenreResult(resultData)
});

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/browse?criteria=title",
    success: (resultData) => handleBrowseTitleResult(resultData)
});

function handleBackResult(resultData) {
    let browseBackNavElement = jQuery("#browse_back_nav_element");

    let anchorHTML = "<a class='btn btn-outline-warning' href='"
        + resultData['backURL']
        + "'>Back</a>";

    browseBackNavElement.append(anchorHTML);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/back",
    success: (resultData) => handleBackResult(resultData)
});