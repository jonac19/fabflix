const unit_price = 4.99;

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleShoppingCartResult(resultData) {
    console.log(resultData);
    let items_table_body = $("#items_table_body");

    let total_cost = 0;
    for (let i = 0; i < resultData.length; i++) {
        $.ajax({
            dataType: "json",
            method: "GET",
            url: "api/movie?id=" + resultData[i]["movie_id"],
            success: (movieData) => {
                let res = "";
                res += "<tr>";
                res += "<td>" + movieData[0]["movie_title"] + "</td>"; //movie title
                res += "<td>" + (Math.round(unit_price * parseInt(resultData[i]["quantity"]) *100) / 100).toString() + "</td>";
                res += "<td>" + "<input type='number' value='" + resultData[i]["quantity"] +"' min='1' name='qty' disabled=true " +
                    "oninput='this.value = Math.abs(this.value)' " + "</td>";

                items_table_body.append(res);
            }
        });

        total_cost += unit_price * parseInt(resultData[i]["quantity"]);
    }

    $("#total_cost").append((Math.round(total_cost*100)/100).toString());   //Display total cost
}


$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/items",
    success: (resultData) => handleShoppingCartResult(resultData)
});
