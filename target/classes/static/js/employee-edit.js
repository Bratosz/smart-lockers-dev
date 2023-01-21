loadPlants(userId, $('#select-plant'));
loadPlants(userId, $('#select-plant-for-exact-box'));
getAndLoadDepartments(userId, $('#select-department'));
getAndLoadDepartments(userId, $('#select-department-for-exact-box'));
getAndLoadLocations(userId, $('#select-location'));
getAndLoadLocations(userId, $('#select-location-for-exact-box'), "LOKALIZACJA");
loadEmployeeInfo();

function selectActualOptionsInSelects(employee) {
    let locker = employee.box.locker,
        plantId = locker.plant.id,
        departmentId = locker.department.id,
        locationId = locker.location.id;
    selectOptionById($('#select-plant'), plantId);
    selectOptionById($('#select-plant-for-exact-box'), plantId);
    selectOptionById($('#select-department'), departmentId);
    selectOptionById($('#select-department-for-exact-box'), departmentId);
    selectOptionById($('#select-location'), locationId);
}

function loadEmployeeInfo() {
    $.ajax({
        url: getEmployee(employeeId),
        method: 'get',
        success: function (employee) {
            selectActualOptionsInSelects(employee);
            writeEmployeeInfoToElement(
                employee,
                $('#info-employee'));
        }
    });
}

$('#button-change-employee-last-name').click(function () {
    let lastName = getValueFromInputTextOrDashIfEmpty($('#input-last-name'));
    $.ajax({
        url: getActualLocation() +
            `/employee/change-last-name` +
            `/${employeeId}` +
            `/${lastName}` +
            `/${userId}`,
        method: 'post',
        success: function (response) {
            writeEmployeeInfoToElement(
                response.entity,
                $('#info-employee'));
        }
    })
});

$('#button-change-employee-first-name').click(function () {
    let firstName = getValueFromInputTextOrDashIfEmpty($('#input-first-name'));
    $.ajax({
        url: getActualLocation() +
            `/employee/change-first-name` +
            `/${employeeId}` +
            `/${firstName}` +
            `/${userId}`,
        method: 'post',
        success: function (response) {
            writeEmployeeInfoToElement(
                response.entity,
                $('#info-employee'));
        }
    })
});

$('#button-relocate-employee-to-first-free-box').click(function () {
    let plantId = $('#select-plant').val(),
        departmentId = $('#select-department').val(),
        locationId = $('#select-location').val();
    $.ajax({
        url: getActualLocation() +
            `/employee/relocate` +
            `/${plantId}` +
            `/${departmentId}` +
            `/${locationId}` +
            `/${employeeId}` +
            `/${userId}`,
        method: 'post',
        success: function (response) {
            window.alert(response.message);
            if (response.succeed) {
                writeEmployeeInfoToElement(
                    response.entity,
                    $('#info-employee'));
            }
        }
    })

});

$('#button-relocate-employee-to-exact-box').click(function () {
    let plantId = $('#select-plant-for-exact-box').val(),
        lockerNumber = $('#input-locker-number').val(),
        boxNumber = $('#input-box-number').val();
    if (isEmpty(lockerNumber) || isEmpty(boxNumber)) {
        alert("Podaj pełny nr szafki");
    } else {
        $.ajax({
            url: getActualLocation() +
                `/employee/relocate-to-exact-box` +
                `/${plantId}` +
                `/${employeeId}` +
                `/${lockerNumber}` +
                `/${boxNumber}` +
                `/${userId}`,
            method: 'post',
            success: function (response) {
                console.log("przeniesono pracownika");
                console.log(response);
                window.alert(response.message);
                if (response.succeed) {
                    writeEmployeeInfoToElement(
                        response.entity,
                        $('#info-employee'));
                }
            }
        })
    }
});

$('#button-load-empty-boxes').click(function () {
    let plantId = $('#select-plant-for-exact-box').val(),
        departmentId = $('#select-department-for-exact-box').val(),
        locationId = $('#select-location-for-exact-box').val(),
        boxStatus = "FREE";
    $.ajax({
        url: getBoxesFilteredByPlantDepartmentLocationAndBoxStatus(plantId, departmentId, locationId, boxStatus),
        method: 'get',
        success: function (boxes) {
            console.log(boxes);
            if(boxes.length == 0) {
                alert("Brak szafek o takich parametrach.");
            }
            boxes = sort(boxes, "locker.lockerNumber", "boxNumber");
            let stringOfBoxes = toStringBoxes(boxes);
            $('#text-available-empty-boxes').text(stringOfBoxes);
            $('#div-available-empty-boxes').css("display", "flex");
        }
    })
});

function toStringBoxes(boxes) {
    let stringOfBoxes = "";
    for (let box of boxes) {
        stringOfBoxes += box.locker.lockerNumber + "/" + box.boxNumber + " ";
    }
    return stringOfBoxes;
}

































