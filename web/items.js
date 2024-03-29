const unit_price = 4.99;

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleShoppingCartResult(resultData) {
    console.log(resultData);
    let items_table_body = $("#items_table_body");
    $("#items_table_body tr").remove(); //Reset the table

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
                    "oninput='this.value = Math.abs(this.value)' id=qty'" + resultData[i]["movie_id"].toString() + "'></td>";
                res += "<td>" + "<button onclick='handleIncrementMovie(\"" + resultData[i]["movie_id"] + "\")'>+1</button>";
                res += "<td>" + "<button onclick='handleDecrementMovie(\"" + resultData[i]["movie_id"] + "\")'>-1</button>";
                res += "<td>" + "<button onclick='handleRemoveMovie(\"" + resultData[i]["movie_id"] + "\")'>Remove</button>";
                res += "</tr>";

                items_table_body.append(res);
            }
        });

        total_cost += unit_price * parseInt(resultData[i]["quantity"]);
    }

    document.getElementById("total_cost").innerText = "Total Cost: $";  // Reset total cost text
    $("#total_cost").append((Math.round(total_cost*100)/100).toString());   //Display total cost
}

function handleIncrementMovie( movie_id ){
    $.ajax({
        url: "api/items?action=increment&movie_id=" + movie_id,
        method: "POST",
        async: false
    });

    // document.location.reload();
    $("#qty" + movie_id).value++;

    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/items",
        async: false,
        success: (resultData) => handleShoppingCartResult(resultData)
    });
}

function handleDecrementMovie( movie_id ){
    $.ajax({
        url: "api/items?action=decrement&movie_id=" + movie_id,
        method: "POST",
        async: false
    });

    // document.location.reload();
    $("#qty" + movie_id).value++;

    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/items",
        async: false,
        success: (resultData) => handleShoppingCartResult(resultData)
    });
}

function handleRemoveMovie( movie_id ){
    $.ajax({
        url: "api/items?action=remove&movie_id=" + movie_id,
        method: "POST",
    });

    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/items",
        async: false,
        success: (resultData) => handleShoppingCartResult(resultData)
    });
}

$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/items",
    success: (resultData) => handleShoppingCartResult(resultData)
});

