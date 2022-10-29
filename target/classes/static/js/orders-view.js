function loadEmployeesWithActiveOrders() {
    getAndLoad(getEmployeesWithActiveOrders, displayEmployeesWithActiveOrders);
}

function displayEmployeesWithActiveOrders(response) {
    let employees = response.entity;
    console.log(employees);
    if (employees.length == 0) {
        $('#div-employees-with-active-orders').css('display', 'none');
    } else {
        $('#div-employees-with-active-orders').css('display', 'inline');
        writeEmployeesWithActiveOrdersToTable(employees, $('#table-of-employees-with-active-orders'));
    }
}

function writeEmployeesWithActiveOrdersToTable(employees, $table) {
    writeDataToTable(
        sort(employees,
            'plantNumber',
            'lockerNumber',
            'boxNumber'),
        $table,
        writeEmployeeWithActiveOrders);
}

$('#select-order-type').change(function () {
   let orderType = $('#select-order-type').val();
   $.ajax({
       url: getEmployeesWithActiveOrdersByOrderType(orderType),
       method: 'get',
       success: function (employees) {
           writeEmployeesWithActiveOrdersToTable(employees, $('#table-of-employees-with-active-orders'));
       }
   })
});



loadEmployeesWithActiveOrders();


