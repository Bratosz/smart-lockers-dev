getClients($('#select-client'));

$('#button-create-client').click(function () {
    let clientName = $('#input-client-name').val();
    createClient(clientName);
});

$('#button-create-plant').click(function () {
    getDataAndCreatePlant();
});

$('#button-create-location').click(function () {
    createLocation();
});

$('#button-create-department').click(function () {
    createDepartment();
});
$('#button-load-client').click(function () {
    getClient(loadClientByClient, $('#select-client').val());
});


function getDataAndCreatePlant() {
    let name = $('#input-plant-name').val();
    let plantNumber = $('#input-plant-number-for-create').val();
    let login = $('#input-plant-login').val();
    let password = $('#input-plant-password').val();
    let passwordConfirmation = $('#input-plant-password-confirmation').val();

    console.log("cokolwiek");
    console.log(plantNumber);
    if (passwordsMatch(password, passwordConfirmation)) {
        createPlant(name, plantNumber, login, password, loadedClientId);
    } else {
        alert("Podane hasła nie są takie same.");
        $('#input-plant-password').val('');
        $('#input-plant-password-confirmation').val('');
        $('#input-plant-password').focus();
    }
}

function createLocation() {
    let locationName = $('#input-location-name').val();
    let plantId = $('#select-plant-for-location').val();
    $.ajax({
        url: postNewLocation(locationName, plantId),
        method: 'post',
        success: function (response) {
            alert(response.message);
            appendLocationToLocationsList(response.entity);
        }
    });
}

function appendLocationToLocationsList(location) {
    let plantNumber = location.plant.plantNumber;
    let $row = $('.cell-plant-number').filter(function () {
        return $(this).text() == plantNumber;
    }).parent();
    let $element = $row
        .find('.cell-plant-locations')
        .find('#li-template').clone();
    $element.removeAttr('id');
    $element.css('display', 'list-item');
    $element.text(location.name);
    $row.find('.list').append($element);
}

function createDepartment() {
    let defaultPlantId = $('#select-plant-for-department').val();
    let departmentName = $('#input-department-name').val();
    $.ajax({
        url: postNewDepartment(departmentName, defaultPlantId, loadedClientId),
        method: 'put',
        success: function (response) {
            alert(response.message);
            appendDepartmentToTable(response.entity);
        }
    });
}

function createClient(clientName) {
    $.ajax({
        url: postNewClient(clientName),
        method: "post",
        success: function (response) {
            console.log(response);
            window.alert(response.message);
            loadClientByClient(response.entity);
            getClients();
        }
    })
}

function createPlant(name, number, login, password, loadedClientId) {
    let plant = {
        name: name,
        plantNumber: number,
        login: login,
        password: password
    };
    $.ajax({
        url: postNewPlant(loadedClientId),
        method: 'post',
        contentType: "application/json",
        data: JSON.stringify(plant),
        success: function (response) {
            if(response.succeed) {
                alert(response.message);
                appendPlantToTable(response.entity);
                appendPlantToSelects(response.entity);
            } else {
                alert(response.message);
            }

        }
    })
}

function loadClientByClient(client) {
    clientIsLoaded = true;
    loadedClientId = client.id;
    loadedClient = client;
    console.log(client);
    $('#div-client-name').text(client.name);
    loadPlantsForCreateClientView(client.plants);
    loadDepartmentsToTable(client.departments, $('#table-of-departments'));
}


function loadPlantsToSelects(plants) {
    if (collectionIsViable(plants)) {
        plants = sort(plants, 'plantNumber');
        appendCollectionToSelectWithPlaceholder(
            plants,
            $('#select-plant-for-department'),
            "Domyślny zakład");
        appendCollectionToSelectWithPlaceholder(
            plants,
            $('#select-plant-for-location'),
            "Wybierz zakład");
    } else {
        removeOptionsFromSelectWithout1stOption($('#select-plant-for-department'));
        removeOptionsFromSelectWithout1stOption($('#select-plant-for-location'));
    }
}

function appendPlantToSelects(plant) {
    appendOptionToSelect($('#select-plant-for-department', plant));
    appendOptionToSelect($('#select-plant-for-location', plant));
}

function loadPlantsForCreateClientView(plants) {
    loadPlantsToTable(plants, $('#table-of-plants'));
    loadPlantsToSelects(plants);
}

function loadPlantsToTable(plants, $table) {
    clearAndHideTable($table);
    if (collectionIsViable(plants)) {
        plantsAreLoaded = true;
        plants = sort(plants, 'plantNumber');
        exposeTable($table);
        writeDataToTable(plants, $table, writePlantToRow);
    } else {
        plantsAreLoaded = false;
    }
}

function loadDepartmentsToTable(departments, $table) {
    clearAndHideTable($table);
    if (collectionIsViable(departments)) {
        departmentsAreLoaded = true;
        exposeTable($table);
        departments = sort(departments, 'mainPlantNumber', 'name');
        writeDataToTable(departments, $table, writeDepartmentToRow);
    } else {
        departmentsAreLoaded = false;
    }
}

function appendPlantToTable(plant) {
    let $table = $('#table-of-plants');
    if (!plantsAreLoaded) {
        exposeTable($table);
        plantsAreLoaded = true;
    }
    addStdRowToTable(plant, $table, writePlantToRow);
}

function appendDepartmentToTable(department) {
    let $table = $('#table-of-departments');
    if (!departmentsAreLoaded) {
        exposeTable($table);
        departmentsAreLoaded = true;
    }
    addStdRowToTable(department, $table, writeDepartmentToRow);
}