loadLocker();

function loadLocker() {
    function toStringLocker(locker) {
        return locker.plant.plantNumber +
            " Szafa nr " + locker.lockerNumber;
    }
    $.ajax({
        url: getLocker(lockerId),
        method: 'get',
        success: function (locker) {
            $("#locker-info").text(toStringLocker(locker));
            loadedBoxesForLockerView = sort(locker.boxes, 'boxNumber');
            reloadBoxesForLockerView(loadedBoxesForLockerView);
            console.log(locker);
        }
    })
};

function dismissEmployeeFromLockerViewAndRefreshRow(employee) {
    if (confirm('Czy na pewno chcesz zwolnić pracownika ' +
        employee.firstName + ' ' + employee.lastName + '?')) {
        dismissEmployee(employee.id);
    };
}

function reloadBoxesForLockerView(boxes) {
    writeDataToTable(
        boxes,
        $("#table-of-boxes"),
        writeBoxToRow);
}

