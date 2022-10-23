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
