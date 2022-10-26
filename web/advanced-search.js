let advancedSearchForm = $("#advanced_search_form");

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitAdvancedSearchForm(formSubmitEvent) {
    console.log("submit advanced search form");
    formSubmitEvent.preventDefault();
    window.location.replace("index.html?" + advancedSearchForm.serialize());
}

// Bind the submit action of the form to a handler function
advancedSearchForm.submit(submitAdvancedSearchForm);

function handleBackResult(resultData) {
    let advancedSearchBackNavElement = jQuery("#advanced_search_back_nav_element");

    let anchorHTML = "<a class='btn btn-outline-warning' href='"
        + resultData['backURL']
        + "'>Back</a>";

    advancedSearchBackNavElement.append(anchorHTML);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/back",
    success: (resultData) => handleBackResult(resultData)
});
