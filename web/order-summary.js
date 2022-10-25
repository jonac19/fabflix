//Make a local dictionary considered to be the truth of movie counts in session. update later w/this
var dict = new Object();
const unit_price = 4.99;


function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log("handle session response");
    console.log(resultDataJson);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let items_table_body = $("#items_table_body");
    var total_cost = 0;
    // change it to html list
    let res = "";
    items_table_body.html("");

    for (let i = 0; i < resultArray.length; i++) {
        var movieData;
        $.ajax({
            dataType: "json",
            method: "GET",
            url: "api/movie?id=" + resultArray[i],
            async: false,
            success: function (resultData){
                movieData = resultData;
            }
        });
        if ( dict[ movieData[0]["movie_id"] ] == null ){
            dict[ movieData[0]["movie_id"] ] = [ movieData[0]["movie_title"], 1 ];  //new entry in dict
        } else {
            dict[ movieData[0]["movie_id"] ][1]++;  //entry exists in dict, incr count++
        }
    }

    for (const [key, value] of Object.entries(dict)){
        res = "";
        res += "<tr>";
        res += "<td>" + value[0] + "</td>"; //movie title
        res += "<td>" + unit_price + "</td>";
        res += "<td>" + value[1] + "</td>";

        res += "</tr>";
        total_cost += unit_price * value[1];

        items_table_body.append(res);

    }
    $("#total_cost").append((Math.round(total_cost*100)/100).toString());   //Display total cost

    for (const[key, value] of Object.entries(dict)){
        $.ajax("api/items?item=" + key, {
            method: "POST"
        });
    }

}

$.ajax("api/items", {
    method: "GET",
    success: handleSessionData
});

