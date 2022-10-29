//test

function getEmployee(employeeId) {
    return getActualLocation() +
        `/employee/with-complete-info` +
        `/${employeeId}`;
}

function postAddPastedEmployeesEDPL() {
    return getActualLocation() +
        `/create-employees/add-employees-edpl` +
        `/${userId}`;
}

function postSetDepartmentPositionLocationForEmployeesToCreate(
    departmentId, positionId, locationId) {
    return getActualLocation() +
        `/create-employees/set-department-position-location` +
        `/${departmentId}` +
        `/${positionId}` +
        `/${locationId}`;
}

function postCreateEmployees() {
    return getActualLocation() +
        `/create-employees`;
}

function deleteEmployeesToCreate() {
    return getActualLocation() +
        `/create-employees/delete` +
        `/${userId}`;
}

function postOrdersForNewClothes(employeeId) {
    return getActualLocation() +
        `/order/place-for-new-clothes` +
        `/${employeeId}` +
        `/${userId}`;
}

function putPerformActionOnOrders(actionType) {
    return getActualLocation() +
        `/order/perform-action` +
        `/${actionType}/${userId}`;
}

function getClientByUserId(userId) {
    return getActualLocation() +
        `/client/get-by-user` +
        `/${userId}`;
}

function getManagementList() {
    return getActualLocation() + `/user/management-list` +
        `/${userId}`;
}

function postSwapEmployeeNames(employeeId) {
    return getActualLocation() +
        `/create-employees/swap-names` +
        `/${employeeId}`;
}


function postNewEmployee(boxId, lastName, firstName, departmentId, positionId) {
    return getActualLocation() +
        `/employee/create-employee-and-add-to-box` +
        `/${boxId}` +
        `/${lastName}` +
        `/${firstName}` +
        `/${departmentId}` +
        `/${positionId}`;
}

function postNewEmployeeToNextFreeBox(lastName, firstName, departmentId, locationId, positionId) {
    return getActualLocation() +
        `/employee/create-employee-and-add-to-next-free-box` +
        `/${lastName}` +
        `/${firstName}` +
        `/${departmentId}` +
        `/${locationId}` +
        `/${positionId}`;
}

function postNewLockers(
    startingLockerNumber, endingLockerNumber, capacity, plantId, departmentId, locationId) {
    return getActualLocation() +
        `/locker/create` +
        `/${startingLockerNumber}` +
        `/${endingLockerNumber}` +
        `/${capacity}` +
        `/${plantId}` +
        `/${departmentId}` +
        `/${locationId}`
}

function postArticleWithQuantityForPosition(articleId, quantity, positionId) {
    return getActualLocation() +
        `/position/add-article-with-quantity` +
        `/${articleId}` +
        `/${quantity}` +
        `/${positionId}`;
}

function postNewPosition(positionName) {
    return getActualLocation() +
        `/position/create` +
        `/${positionName}` +
        `/${userId}`;
}

function deletePosition(positionId) {
    return getActualLocation() +
        `/position/delete` +
        `/${positionId}`;
}

function getAllPositions(departmentId) {
    return getActualLocation() +
        `/position/get-all-by-department` +
        `/${departmentId}`;
}

function postPositionAndAddDepartment(positionId, departmentId) {
    return getActualLocation() +
        `/position/add-department` +
        `/${departmentId}` +
        `/${positionId}`;
}

function getPosition(positionId) {
    return getActualLocation() +
        `/position/get-one` +
        `/${positionId}`;
}

function postClothTypeWithQuantity(clientArticleId, quantity, positionId) {
    getActualLocation() +
    `/position/add-article-with-quantity` +
    `/${clientArticleId}` +
    `/${quantity}` +
    `/${positionId}`;
}

function postAnotherArticle(clientArticleId, articleWithQuantityId, positionId) {
    return getActualLocation() +
        `/position/add-another-article` +
        `/${clientArticleId}` +
        `/${articleWithQuantityId}` +
        `/${positionId}`;
}

function postNewLocation(locationName, plantId) {
    return getActualLocation() +
        `/location/create` +
        `/${locationName}/${plantId}`;
}

function postNewDepartment(departmentName, defaultPlantId, loadedClientId) {
    return getActualLocation() +
        `/department/create` +
        `/${departmentName}/${defaultPlantId}/${loadedClientId}`;
}

function getClient(loadMethod, clientId) {
    $.ajax({
        url: getActualLocation() +
            `/client/get-by-id` +
            `/${clientId}`,
        method: 'get',
        success: function (client) {
            loadMethod(client);
        }
    })
}

function getClientByUser(userId) {
    return getActualLocation() +
        `/client/get-by-user` +
        `/${userId}`;
}

function postNewClient(clientName) {
    return getActualLocation() +
        `/client/create` +
        `/${clientName}`;
}

function putClientArticlesDepreciationPeriod(period) {
    return getActualLocation() + `/client-article/set-depreciation-period/for-all/` +
        `/${period}` +
        `/${userId}`;
}

function putClientArticlesPercentageCap(percentageCap) {
    return getActualLocation() + `/client-article/set-percentage-cap/for-all/` +
        `/${percentageCap}` +
        `/${userId}`;
}

function putClientArticlePrice(price, clientArticleId) {
    return getActualLocation() + `/client-article/update-price` +
        `/${price}` +
        `/${clientArticleId}`;
}

function postNewPlant(loadedClientId) {
    return getActualLocation() +
        `/plant/create` +
        `/${loadedClientId}`;
}

function getReportForAllOrders() {
    return getActualLocation() +
        `/report/generate` +
        `/${userId}`;
}

function getOrdersToReport() {
    return getActualLocation() +
        `/order/get-to-report` +
        `/${userId}`;
}

function getEmployeesWithActiveOrders() {
    return getActualLocation() +
        `/employee/with-active-orders` +
        `/${userId}`;
}

function getEmployeesWithActiveOrdersByOrderType(orderType) {
    return getActualLocation() +
        `/employee/with-active-orders-by-order-type` +
        `/${orderType}/${userId}`;
}


function getReportForNewEmployees() {
    return getActualLocation() +
        `/report/generate-for-new-employees` +
        `/${userId}`;
}

function getReportOfEmployeesWithClothesQuantities() {
    return getActualLocation() +
        `/report/get-employees-with-clothes-quantities` +
        `/${userId}`;
}

function postReportOrder(mainOrderId) {
    return getActualLocation() +
        `/order/report` +
        `/${mainOrderId}` +
        `/${userId}`;
}

function postSetMeasuredEmployeesAsAssigned() {
    return getActualLocation() +
        `/measurement-list/set-measured-employees-as-assigned` +
        `/${userId}`;
}

function getAllArticles() {
    return getActualLocation() +
        `/article/get-all`;
}

function postAddClientArticle(articleId, badgeNumber, userId) {
    return getActualLocation() +
        `/client-article/add` +
        `/${articleId}/${badgeNumber}/${userId}`;
}

function postCreateArticleAndAddClientArticle(
    articleNumber, articleName, badgeNumber, userId) {
    return getActualLocation() +
        `/client-article/create-article-and-add-client-article` +
        `/${articleNumber}` +
        `/${articleName}` +
        `/${badgeNumber}` +
        `/${userId}`;
}

function getTemplateForCreateClient(templateType) {
    return getActualLocation() +
        `/report/get-template-for-create-client` +
        `/${templateType}` +
        `/${userId}`;
}

function postCreateClient(clientName, plantNumber) {
    return getActualLocation() +
        `/client/create-for-load-from-file` +
        `/${clientName}` +
        `/${plantNumber}` +
        `/${userId}`;
}

function getAllClientArticles() {
    return getActualLocation() +
        `/client-article/get-all` +
        `/${userId}`;
}

function getPlantsByClient(clientId) {
    return getActualLocation() + `/plant/get-all/${clientId}`;
}

function getLockersByPlant(plantId) {
    return getActualLocation() + `/locker/get-by-plant/${plantId}`;
}

function getBoxesBy(lockerId) {
    return getActualLocation()
        + `/box/get-by-locker/${lockerId}`
}

function getLocker(lockerId) {
    return getActualLocation() +
        `/locker/get` +
        `/${lockerId}`;
}

function postDismissEmployee(clothesReturned, employeeId) {
    return getActualLocation() +
        `/employee/dismiss-by-id` +
        `/${clothesReturned}/${employeeId}/${userId}`;
}

function getAllLockers(userId) {
    return getActualLocation() + `/locker/get-all/${userId}`;
}

function getBoxesFilteredByPlantDepartmentLocationAndBoxStatus(plantId, departmentId, locationId, boxStatus) {
    return getActualLocation() + `/box/get-filtered` +
        `/${plantId}` +
        `/${departmentId}` +
        `/${locationId}` +
        `/${boxStatus}`;
}

function postAutoExchange(barcode) {
    return getActualLocation() +
        `/cloth/auto-exchange` +
        `/${barcode}/${userId}`;
}

function postNewOrdersBy(userId) {
    return getActualLocation() +
        `/order/place-many/${userId}`
}

function getEmployeeWithCompleteInfo(employeeId) {
    return getActualLocation() +
        `/employee/with-complete-info` +
        `/${employeeId}`;
}

function postAddEmployeeToManagementList(employeeId) {
    return getActualLocation() +
        `/user/add-employee-to-management-list` +
        `/${employeeId}` +
        `/${userId}`;
}

function getUpdatedEmployee(employeeId) {
    return getActualLocation() + `/employee/update` +
        `/${employeeId}/${userId}`;
}

function getOrdersByEmployee(employeeId) {
    return getActualLocation() +
        `/order/get-by-employee` +
        `/${employeeId}`;
}

function getBoxesByLastName(lastName, userId) {
    return getActualLocation() + `/box/get-by-last-name` +
        `/${lastName}` +
        `/${userId}`;
}

function getBoxesByLockerNumberAndPlantId(lockerNumber, plantId) {
    return getActualLocation() + `/box/get-by-locker-number-and-plant-id` +
        `/${lockerNumber}` +
        `/${plantId}`;
}

function postUpdateBox(boxId, userId) {
    return getActualLocation() +
        `/box/update` +
        `/${boxId}/${userId}`;
}

function getEmployeesByFirstName(firstName, userId) {
    return getActualLocation() +
        `/employee/find-by-first-name` +
        `/${firstName}` +
        `/${userId}`;
}

function getEmployeesToCreate() {
    return getActualLocation() +
        `/client/get-employees-to-create` +
        `/${userId}`;
}

function getEmployeesToAssign() {
    return getActualLocation() +
        `/client/get-employees-to-assign` +
        `/${userId}`;
}

function getEmployeesToMeasure() {
    return getActualLocation() +
        `/client/get-employees-to-measure` +
        `/${userId}`;
}

function getEmployeesToRelease() {
    return getActualLocation() +
        `/client/get-employees-to-release` +
        `/${userId}`;
}

function postRemoveEmployeeFromList(employeeId) {
    return getActualLocation() +
        `/measurement-list/remove-employee` +
        `/${employeeId}`;
}

function getLockersFiltered(plantId, departmentId, locationId) {
    return getActualLocation() + `/locker/get-filtered` +
        `/${plantId}` +
        `/${departmentId}` +
        `/${locationId}`;
}

function postLockersChangeDepartmentAndLocation(
    startingLockerNumber, endLockerNumber, plantId, departmentId, locationId) {
    return getActualLocation() +
        `/locker/change-department-and-location` +
        `/${startingLockerNumber}` +
        `/${endLockerNumber}` +
        `/${plantId}` +
        `/${departmentId}` +
        `/${locationId}`;
}

function putActualClientByPlantNumberToUser(plantNumber, userId) {
    return getActualLocation() + `/user/put-actual-client-by-plant-number` +
        `/${plantNumber}` +
        `/${userId}`;
}

function getDepartments(userId) {
    return getActualLocation() +
        `/department/get-all/${userId}`;
}

function getPositions(userId) {
    return getActualLocation() +
        `/position/get-all` +
        `/${userId}`;
}

function postChangeEmployeeDepartmentAndPositionForNewEmployee(employeeId, departmentId, positionId, userId) {
    return getActualLocation() +
        `/employee/change-department-and-position-for-new-employee` +
        `/${employeeId}/${departmentId}/${positionId}/${userId}`;
}

function postChangeEmployeeDepartmentAndPositionForOldEmployee(employeeId, departmentId, positionId, userId) {
    return getActualLocation() +
        `/employee/change-department-and-position-for-old-employee` +
        `/${employeeId}/${departmentId}/${positionId}/${userId}`;
}

function postReturnRotationalClothes(userId) {
    return getActualLocation() +
        `/rotational-cloth/return-rotational-clothes` +
        `/${userId}`;
}


function getClientArticles(userId) {
    return getActualLocation() +
        `/client-article/get-all` +
        `/${userId}`;
}

function getLocations(userId) {
    return getActualLocation() +
        `/location/get-all` +
        `/${userId}`
}

function postLoadPlantWithoutClothes(plantId) {
    return getActualLocation() +
        `/load/plant-without-clothes` +
        `/${plantId}`;
}

function postUpdateClothes(plantId) {
    return getActualLocation() +
        `/load/update-clothes` +
        `/${plantId}`;
}

function postLoadAllClothes(plantId) {
    return getActualLocation() +
        `/load/all-clothes` +
        `/${plantId}`;
}





