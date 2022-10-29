
function loadForEmployeePositionAndDepartment() {
    let $selectDepartment = $('#select-department');
    let $selectPosition = $('#select-position');
    $.ajax({
        url: getDepartments(userId),
        method: 'get',
        success: function (departments) {
            removeOptionsFromSelect($selectDepartment);
            console.log(departments);
            appendOptionsToSelect(
                sort(departments, "name"), $selectDepartment);
        }
    }).done(function () {
        selectOptionById($selectDepartment, loadedEmployee.department.id);
        removeOptionsFromSelect($selectPosition);
        $.ajax({
            url: getAllPositions(loadedEmployee.department.id),
            method: 'get',
            success: function (positions) {
                console.log(positions);
                appendOptionsToSelect(positions, $selectPosition);
            }
        }).done(function () {
            if(loadedEmployee.position != null) {
                selectOptionById($selectPosition, loadedEmployee.position.id);
            }
        })
    })
}

