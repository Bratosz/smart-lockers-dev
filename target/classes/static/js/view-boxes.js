//test comment

loadPlants(userId, $('#select-plant'));
getAndLoadDepartments(userId, $('#select-department'));
getAndLoadLocations(userId, $('#select-location'));

loadBoxes();

loadBoxByCtrlPlusNumber();

$("#button-filter").click(function () {
    let plantId = $("#select-plant").val();
    let departmentId = $("#select-department").val();
    let locationId = $("#select-location").val();
    let boxStatus = $("#select-box-status").val();
    $.ajax({
        url: getBoxesFilteredByPlantDepartmentLocationAndBoxStatus(plantId, departmentId, locationId, boxStatus),
        method: 'get',
        success: function (boxes) {
            loadedBoxes = boxes;
            reloadBoxes(boxes);
        }
    })
});

$("#button-search-by-last-name").click(function () {
    let lastName = $("#input-last-name").val();
    searchEmployeesByLastName(lastName);
});



$("#button-get-boxes-by-locker-number-and-plant-id").click(function () {
    let plantId = $("#select-plant").val();
    let lockerNumber = $("#input-locker-number").val();
    $.ajax({
        url: getBoxesByLockerNumberAndPlantId(lockerNumber, plantId),
        method: 'get',
        success: function (boxes) {
            reloadBoxes(boxes);
        }
    });
});

$("#button-input-first-name").click(function () {
    let firstName = $("#input-first-name").val();
    $.ajax({
        url: getEmployeesByFirstName(firstName, userId),
        method: 'get',
        success: function (employees) {
            console.log(employees);
            displayEmployees(employees);
        }
    })
});

