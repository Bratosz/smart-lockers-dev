function getRowTemplate($table) {
    return $table.find("tr:nth-child(1)");
}

function rowClickedBehaviour($row) {
    let $checkBox = $row.find('.my-check-box');
    // let rowWithoutSelects = $row.not($row.find('.form-control'));
    $row.click(function () {
        checkAndUncheck($checkBox);
    });
    $row.find('.my-check-box').click(function () {
        checkAndUncheck($checkBox);
    });
    $row.find('.select-department').change(function () {
        loadPositionsByDepartment($row.find('.select-department'), $row.find('.select-position'));
    })
}

function checkAndUncheck($checkBox) {
    if($checkBox.prop('checked')) {
        $checkBox.prop('checked', false);
    } else {
        $checkBox.prop('checked', true);
    }
}

function removeTableRows($table) {
    $table.find("tr:not(tr:nth-child(1))").remove();
    return $table;
}

function writeDataToTable(elements, $table, writingMethod) {
    show($table);
    $table = removeTableRows($table);
    for (let e of elements) {
        addStdRowToTable(e, $table, writingMethod);
    }
    return $table;
}

function getCheckedRows($table) {
    let $rows = [];
     $table.find('tbody').find('input[type="checkbox"]:checked')
         .each(function () {
             $rows.push($(this).closest('tr'));
         });
    return $rows;
}

function getCheckedBarcodes($tableBody) {
    let barcodes = [];
    $tableBody.find('input[type="checkbox"]:checked')
        .each(function () {
            let clothBarcode = parseInt($(this).closest('tr').find('.cell-barcode').text());
            barcodes.push(clothBarcode);
        });
    return barcodes;
}

function addStdRowToTable(element, $table, writingMethod) {
    let $tbody = $table.find('tbody');
    let $row = getRowTemplate($tbody).clone();
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row = writingMethod(element, $row);
    $tbody.append($row);
}

function addEmptyRowToTable($table) {
    let $tbody = $table.find('tbody');
    let $row = getRowTemplate($tbody).clone();
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $tbody.append($row);
}

function addLabeledRowToTable(label, content, $table, writingMethod) {
    let $row = getRowTemplate($table).clone();
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row = writingMethod(label, content, $row);
    $table.append($row);
}

function addRowToTable($table) {
    let $row = getRowTemplate($table).clone();
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $table.append($row);
}

function refreshRow(element, $row, writingMethod) {
    writingMethod(element, $row);
}

function refreshNames(employee, $row) {
    $row.find('.cell-last-name').text(employee.lastName);
    $row.find('.cell-first-name').text(employee.firstName);
}

function writeLockersWithSortingToTable(lockers, $table, writingMethod) {
    writeDataToTable(
        sort(lockers,
            'plant.plantNumber',
            'lockerNumber'),
        $table,
        writingMethod);
}

function writeEmployeesWithSortingToTable(employees, $table, writingMethod) {
    writeDataToTable(
        sort(employees,
            'box.locker.plant.plantNumber',
            'box.locker.lockerNumber',
            'box.boxNumber'),
        $table,
        writingMethod);
}

function clearAndHideTable($table) {
    removeTableRows($table);
    $table.css('display', 'none');
}

function exposeTable($table) {
    $table.css('display', 'table');
}