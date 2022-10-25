let payment_form = $("#payment_form");

/**
 * Handle the data returned by PaymentServlet
 * @param resultDataString jsonObject
 */
function handlePaymentResult(resultDataString) {
    console.log(resultDataString);
    let resultDataJson = JSON.parse(resultDataString);

    // If payment succeeds, it will redirect the user to order-summary.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("order-summary.html");
    } else {
        // If payment fails, the web page will display
        // error messages on <div> with id "payment_error_message"
        $("#payment_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the payment form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}

payment_form.submit(submitPaymentForm);