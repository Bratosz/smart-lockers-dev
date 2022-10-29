function loadPositionsToTable(clientId) {
    let $table = $('#table-of-positions');
    $.ajax({
        url: getActualLocation() +
            `/positions` +
            `/${clientId}`,
        method: 'get',
        success: function (positions) {
            console.log(positions);
            writeDataToTable(positions, $table, writePositionToRow);
        }
    })
}