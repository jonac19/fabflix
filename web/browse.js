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

    for (let i = 0; i < resultData.length; i++) {
        let colHTML = "";
        colHTML += "<a class='col-3' href='index.html?browseTitle='" + resultData[i]['title_letter'] + ">" +
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