getAndLoadDepartments(userId, $('#select-department'));
getAndLoadLocations(userId, $('#select-location'));
loadPlants(userId, $('#select-plant'));

$("#button-add-lockers").click(function () {
    console.log("start");
    let startingLockerNumber = $("#input-starting-locker-number").val();
    let endingLockerNumber = $("#input-ending-locker-number").val();
    let capacity = $("#select-capacity").val();
    let plantId = $("#select-plant").val();
    let departmentId = $("#select-department").val();
    let locationId = $("#select-location").val();

    $.ajax({
        url: postNewLockers(
            startingLockerNumber, endingLockerNumber, capacity, plantId, departmentId, locationId),
        method: "post",
        success: function (response) {
            alert(response.message);
        }
    })
});


