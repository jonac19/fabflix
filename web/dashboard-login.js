let dashboard_form = $("#dashboard_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    // If login succeeds, it will redirect the user to _dashboard.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("_dashboard.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        $("#dashboard_login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitDashboardLoginForm(formSubmitEvent) {
    console.log("submit dashboard login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/dashboard-login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: dashboard_form.serialize(),
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
dashboard_form.submit(submitDashboardLoginForm);
