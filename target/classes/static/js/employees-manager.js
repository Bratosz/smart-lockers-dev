
function dismissEmployee(employeeId) {
    let clothesReturned = false;
    if(confirm("Czy ubrania zostały wycofane?")) {
        clothesReturned = true;
    }
    $.ajax({
        url: postDismissEmployee(clothesReturned, employeeId),
        method: "post",
        success: function (response) {
            alert(response.message);
            let box = response.entity;
            writeBoxToRow(box, $('#' + box.id));
        }
    });
}

function getEmployeeIdFromRow($row) {
    return $row.attr('id');
}

function swapNames(employeeId, $row) {
    $.ajax({
        url: postSwapEmployeeNames(employeeId),
        method: 'post',
        success: function (response) {
            alert(response.message);
            refreshRow(response.entity, $row, refreshNames);
        }
    })
}

function writeEmployeeInfoToElement(employee, $element) {
    let location,
        plantNumber,
        lockerNumber,
        boxNumber,
        lastName,
        firstName;
    location = employee.box.locker.location.name;
    plantNumber = employee.box.locker.plant.plantNumber;
    lockerNumber = employee.box.locker.lockerNumber;
    boxNumber = employee.box.boxNumber;
    lastName = employee.lastName;
    firstName = employee.firstName;
        $element.text(
            plantNumber +
            " " + lockerNumber + "/" + boxNumber +
            " " + lastName +
            " " + firstName);

}

function displayEmployeeForEmployeeView(employee) {
    let box = employee.box,
        lockerNumber = box.locker.lockerNumber,
        boxNumber = box.boxNumber,
        boxStatus = box.boxStatus,
        clothes = employee.clothes,
        rotationalClothes = employee.rotationalClothes;
        mainOrders = employee.mainOrders,
        lastName = employee.lastName,
        firstName = employee.firstName;

    $("#employee").text(lockerNumber + "/" + boxNumber
        + " " + lastName + " " + firstName);

    let beforeRelease = extractClothes("BEFORE_RELEASE", clothes);
    let inRotation = extractClothes("IN_ROTATION", clothes);
    let accepted = extractClothes("ACCEPTED", clothes);
    let withdrawn = extractClothes("WITHDRAWN", clothes);
    let activeOrders = extractActiveOrders(mainOrders);
    let clientArticlesInRotation = extractClientArticles(inRotation);

    displayArticlesForChangeInExchangePanel(clientArticlesInRotation);
    displayArticlesToAddInAddPanel(loadedEmployee.position);
    displayActiveClothes(inRotation);
    displayClothesBeforeRelease(beforeRelease);
    displayAcceptedClothes(accepted);
    displayWithdrawnClothes(withdrawn);
    displayOrders(activeOrders);
    displayRotationalClothes(rotationalClothes);
}