let add_star_form = $("#add_star_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    $("#add_star_message").text(resultDataJson["message"]);
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

    $.ajax(
        "api/dashboard", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: add_star_form.serialize() + "&action=addStar",
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
add_star_form.submit(submitAddStarForm);