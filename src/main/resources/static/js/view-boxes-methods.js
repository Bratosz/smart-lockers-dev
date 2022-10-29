function loadBoxes() {
    if(loadedBoxes.length > 0) {
        reloadBoxes(loadedBoxes);
    }
}

function dismissEmployeeFromBoxesView(employee) {
    if (confirm('Czy na pewno chcesz zwolnić pracownika ' +
        employee.firstName + ' ' + employee.lastName + '?')) {
        dismissEmployee(employee.id);
    };
}

function searchEmployeesByLastName(lastName) {
    $.ajax({
        url: getBoxesByLastName(lastName, userId),
        method: 'get',
        success: function (boxes) {
            reloadBoxes(boxes);
        }
    })
}

function reloadBoxes(boxes) {
    loadedBoxes = boxes;
    $('#amount-of-boxes').text(boxes.length);
    writeDataToTable(
        sort(boxes, 'locker.plant.plantNumber', 'locker.lockerNumber', 'boxNumber'),
        $('#table-of-boxes'),
        writeBoxToRow);
}

function displayEmployees(employees) {
    let boxes = getBoxesFromEmployees(employees);
}

function updateBox(boxId) {
    $.ajax({
        url: postUpdateBox(boxId, userId),
        method: 'post',
        success: function (response) {
            if(response.succeed) {
                if (confirm("Załadowano pracownika, czy chcesz przejść do jego szafki?")) {
                   employeeId = response.entity.id;
                   loadContent('view-employee.html', false);
                }
            } else {
                alert(response.message);
            }
        }
    })
}




