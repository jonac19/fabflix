let add_star_form = $("#add_star_form");
let add_movie_form = $("#add_movie_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleAddStarResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    $("#add_star_message").text(resultDataJson["message"]);
}

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleAddMovieResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    $("#add_movie_message").text(resultDataJson["message"]);
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitAddStarForm(formSubmitEvent) {
    console.log("submit add star form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax({
            url: "../api/dashboard",
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_star_form.serialize() + "&action=addStar",
            success: handleAddStarResult
        }
    );
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitAddMovieForm(formSubmitEvent) {
    console.log("submit add star form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax({
            url: "../api/dashboard",
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_movie_form.serialize() + "&action=addMovie",
            success: handleAddMovieResult
        }
    );
}

// Bind the submit action of the form to a handler function
add_star_form.submit(submitAddStarForm);
add_movie_form.submit(submitAddMovieForm)

$.ajax({
    dataType: "json",
    method: "GET",
    url: "../api/dashboard",
    success: (resultData) => handleDashboardResult(resultData)
});

function handleDashboardResult(resultData) {
    let dashboardTablesElement = $("#dashboard_tables");

    // Iterate through resultData
    for (let i = 0; i < resultData.length; i++) {
        let table = resultData[i];
        let tableHTML = "<div class='m-4'>" +
                            "<h4>Table: " + table["table_name"] + "</h4>" +
                            "<table class='table table-striped table-dark'>" +
                                "<thead>" +
                                    "<tr>" +
                                        "<th>Attribute</th>" +
                                        "<th>Type</th>" +
                                    "</tr>" +
                                "</thead>" +
                                "<tbody>";
        for (let j = 0; j < table["table_columns"].length; j++) {
            let table_column = table["table_columns"][j]
            let rowHTML = "";
            rowHTML += "<tr>"
            rowHTML += "<td>" + table_column["table_field"] + "</td>";
            rowHTML += "<td>" + table_column["table_type"] + "</td>";
            rowHTML += "</tr>";

            // Append the row created to the table body, which will refresh the page
            tableHTML += rowHTML;
        }
        tableHTML += "</tbody></table></div>";
        dashboardTablesElement.append(tableHTML);
    }
}