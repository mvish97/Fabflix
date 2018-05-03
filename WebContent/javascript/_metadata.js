

function handleMetadata(resultData){
    var table = $("table#metadata_table");
    for(var i=0; i < resultData.length; i++){
        var right_col = make_right_col(resultData[i].columns, resultData[i].dataType)
        table.append(
            `<tr>
                <td class="left"> ${resultData[i].tableName} </td>
                <td class="right">
                    ${right_col}
                </td>
            </tr>`
        );
    }
}


function make_right_col(columns, dataTypes){
    var cols = columns.split(",");
    var types = dataTypes.split(",");
    var result = "<ul>";
    for(var i=0; i < cols.length; i++){
        result += `<li>${cols[i]} (${types[i]})</li>`
    }
    result += "</ul>"
    
    return result;
}

jQuery.ajax({
            type: "POST",
            dataType: "json",
            data: {},
            url: sessionStorage.baseurl+"/project2/getDBDetails",
            success: resultData => handleMetadata(resultData)
});