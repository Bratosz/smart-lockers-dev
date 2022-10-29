
loadManagementList();

function loadManagementList() {
    $.ajax({
        url: getManagementList(),
        method: "get",
        success: function (managementList) {
            console.log(calcTotalPrice(managementList.employees));
            console.log(managementList.employees);
            writeArticlesToTable(managementList);
        }
    })
}

function calcTotalPrice(employees) {
    let totalPrice = 0;
    for(let e of employees) {
        totalPrice += e.redemptionPrice;
    }
    return totalPrice;
}

function writeArticlesToTable(managementList) {
    writeDataToTable(
        sort(managementList.employees,
            'box.locker.plant.plantNumber',
            'box.locker.lockerNumber',
            'box.boxNumber'),
        $('#table-of-employees'),
        writeEmployeeToRow);
}

function writeEmployeeToRow(employee, $row) {
    let box = employee.box;
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-id').text(employee.id);
    $row.find('.cell-first-name').text(employee.firstName);
    $row.find('.cell-last-name').text(employee.lastName);
    $row.find('.cell-locker').text(box.locker.lockerNumber);
    $row.find('.cell-box-number').text(box.boxNumber);
    $row.find('.cell-plant-number').text(box.locker.plant.plantNumber);
    $row.find('.cell-department').text(box.locker.department.name);
    $row.find('.cell-location').text(box.locker.location.name);
    $row.find('.cell-redemption-price').text(Math.round(employee.redemptionPrice * 100)/100);
    $row.find('.button-view-employee').click(function () {
        employeeId = employee.id;
        loadContent($('#div-content-1'),'view-employee.html');
    });
    return $row;
}