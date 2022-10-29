getAndLoadDepartments(userId, $('#select-department'));
getAndLoadDepartments(userId, $('#select-department-for-change'));
getAndLoadLocations(userId, $('#select-location'));
getAndLoadLocations(userId, $('#select-location-for-change'));
loadPlants(userId, $('#select-plant'));

loadLockers(userId);

$('#button-filter').click(function () {
    let plantId = $('#select-plant').val();
    let departmentId = $('#select-department').val();
    let locationId = $('#select-location').val();

    $.ajax({
        url: getLockersFiltered(plantId, departmentId, locationId),
        method: 'get',
        success: function (lockers) {
            writeLockersWithSortingToTable(
                lockers,
                $('#table-of-lockers'),
                writeLockerToRow);
        }
    })
});

$('#button-change-department-and-location').click(function () {
    let startingLockerNumber = $('#input-starting-locker-number').val();
    let endLockerNumber = $('#input-end-locker-number').val();
    let plantId = $('#select-plant').val();
    let departmentId = $('#select-department-for-change').val();
    let locationId = $('#select-location-for-change').val();
    $.ajax({
        url: postLockersChangeDepartmentAndLocation(
            startingLockerNumber, endLockerNumber, plantId, departmentId, locationId),
        method: 'post',
        success: function (response) {
            if(response.succeed) {
                writeLockersWithSortingToTable(
                    response.list,
                    $('#table-of-lockers'),
                    writeLockerToRow);
            } else {
                alert(response.message);
            }
        }
    })
});

function loadLockers(userId) {
    $.ajax({
        url: getPlantsByClient(userId),
        method: 'get',
        success: function(plants) {
            let plantId = plants[0].id;
            $.ajax({
                url: getLockersByPlant(plantId),
                method: 'get',
                success: function (lockers) {
                    writeLockersWithSortingToTable(
                        lockers,
                        $('#table-of-lockers'),
                        writeLockerToRow);
                }
            })
        }
    })
}





