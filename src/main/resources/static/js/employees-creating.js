loadEmployeesToCreate();
loadDepartments($('#select-department'));
loadLocations($('#select-location'));
loadPositionsByDepartment();

$('#select-department').change(function () {
    loadPositionsByDepartment();
});

$('#button-post-employees-to-create').click(function () {
    let employeesToCreate = [];
    getCheckedRows($('#table-of-employees-to-create')).forEach(
        r => {
            let etc = {
                employeeId: r.attr('id'),
                departmentId: r.find('.select-department').val(),
                positionId: r.find('.select-position').val(),
                locationId: r.find('.select-location').val()
            };
            employeesToCreate.push(etc);
        }
    )
    $.ajax({
        url: postCreateEmployees(),
        method: 'post',
        contentType: 'application/json',
        data: JSON.stringify(employeesToCreate),
        success: function () {
        }
    }).done(function () {
        $.ajax({
            url: getEmployeesToCreate(),
            method: 'get',
            success: function (response) {
                if (response.entity.length > 0) {
                    loadEmployeesToCreate(response.entity);
                } else {
                    loadContent($('#div-content-1'), 'measurement-list.html');
                }
            }
        })
    })
});

$('#button-select-all-rows').click(function () {
    $('#table-of-employees-to-create').find('tr:not(tr:nth-child(1))').each(function () {
        $(this).find('input[type="checkbox"]').prop('checked', true);
    });
});

$('#button-unselect-all-rows').click(function () {
    $('#table-of-employees-to-create').find('input[type="checkbox"]').each(function () {
        $(this).prop('checked', false);
    });
});

$('#button-set-dep-pos-loc').click(function () {
    let departmentId = $('#select-department').val(),
        positionId = $('#select-position').val(),
        locationId = $('#select-location').val(),
        employeesIds = [];
    getCheckedRows($('#table-of-employees-to-create')).forEach(
        r => employeesIds.push(getEmployeeIdFromRow(r)));
    $.ajax({
        url: postSetDepartmentPositionLocationForEmployeesToCreate(
            departmentId, positionId, locationId),
        method: 'post',
        contentType: 'application/json',
        data: JSON.stringify(employeesIds),
        success: function (response) {
            if (!response.succeed) alert("Coś poszło nie tak");
        }
    }).done(function () {
        loadEmployeesToCreate();
    })
});


$('#button-delete-employees-to-create').click(function () {
    let employeesIds = [];
    getCheckedRows($('#table-of-employees-to-create')).forEach(
        r => employeesIds.push(getEmployeeIdFromRow(r)));
    $.ajax({
        url: deleteEmployeesToCreate(),
        method: 'delete',
        contentType: 'application/json',
        data: JSON.stringify(employeesIds),
        success: function (response) {
            if (response.entity.length > 0) {
                loadEmployeesToCreate(response.entity);
            } else {
                loadContent($('#div-content-1'), 'measurement-list.html');
            }
        }
    })
});

function loadEmployeesToCreate(employees) {
    if (employees == undefined) {
        $.ajax({
            url: getEmployeesToCreate(),
            method: 'get',
            success: function (response) {
                if (response.succeed) {
                    displayEmployeesToCreate(response.entity);
                }
            }
        })
    } else {
        displayEmployeesToCreate(employees);
    }
}

function displayEmployeesToCreate(employeesToCreate) {
    writeDataToTable(
        sort(employeesToCreate, "gender"),
        $('#table-of-employees-to-create'),
        writeEmployeeToCreateToRow
    );
    console.log(employeesToCreate);
}

function loadSelectInRowForEmployeeToCreate($select, options, actual, placeholder) {
    if (actual.surrogate) {
        appendOptionsToSelect(options, $select, placeholder);
    } else {
        appendOptionsToSelectAndSelectActual(
            sort(options, "name"),
            $select,
            actual);
    }
}

function loadPositionsForEmployeeToCreate($selectPosition, positions, actualPosition, placeholder) {
    if(actualPosition.surrogate || positions.length == 0) {
        appendOptionsToSelect(positions, $selectPosition, placeholder);
    } else {
        appendOptionsToSelectAndSelectActual(
            sort(positions, "name"),
            $selectPosition,
            actualPosition
        );
    }
}


