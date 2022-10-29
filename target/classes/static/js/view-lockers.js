getAndLoadDepartments(userId, $('#select-department'));
getAndLoadLocations(userId, $('#select-location'));
loadPlants(userId, $('#select-plant'));

loadLockers();

$("#button-filter-lockers").click(function () {
    filterLockers();
});

function loadLockers() {
    if(loadedLockers.length > 0) {
        reloadLockers(loadedLockers);
    } else {
        loadLockersByUser(userId);
    }
}

function reloadLockers(lockers) {
    loadedLockers = lockers;
    writeLockersWithSortingToTable(
        lockers,
        $('#table-of-lockers'),
        writeLockerToRowWithViewButton);
}

function loadLockersByUser(userId) {
    $.ajax({
        url: getAllLockers(userId),
        method: 'get',
        success: function (lockers) {
            loadedLockers = lockers;
            reloadLockers(lockers);
        }
    })
}

function filterLockers() {
    let plantId = $("#select-plant").val();
    let departmentId = $("#select-department").val();
    let locationId = $("#select-location").val();
    $.ajax({
        url: getLockersFiltered(plantId, departmentId, locationId),
        method: 'get',
        success: function (lockers) {
            console.log(lockers);
            loadedLockers = lockers;
            reloadLockers(lockers);
        }
    });
}



