const userId = 7;
let positionIsLoaded = false;
let loadedPositionId = 0;
let loadedPosition;
let boxId = 0;
let employeeId = 0;
let boxIdForNewEmployee;
let previousPages = [];
let actualPage = "empty.html";


//to DO - WHEN CHANGE CLIENT THEN RESET VALUES
//client
let clientIsLoaded = false;
let loadedClientId = 0;
let loadedClient;
let plantsAreLoaded = false;
let departmentsAreLoaded = false;
let loadedDepartments;
let loadedPlants;
let loadedLocations;
let loadedPositions;

//keys
let ctrlPressed = false;

//employye
let loadedEmployee;

//clothes
let badges = [];

//orders
let ordersToReport = [];
let actualOrderType;

//locker
let lockerId;
let loadedBoxes = [];
let loadedBoxesForLockerView = [];
let loadedLockers = [];

//qr-reader
let cameraId;
let scanner;
const config = {qrbox: 350};

$(document).ready(function () {
    console.log("start");
    loadClient();
    loadGeneralKeyBehaviour();
    loadNav();

});

if ('serviceWorker' in navigator) {
    navigator.serviceWorker
        .register('/../smart-lockers-service-worker.js')
        .then(function () {
            console.log("Service Worker Registered");
        });
}


function loadNav() {
    $('#div-nav').load('navbar.html', function () {
        loadNavKeysBehaviour();
        $('#button-load-client-by-plant-number').click(function () {
            loadClientByPlantNumber();
        });
        $('#nav-button-search-by-last-name').click(function () {
            console.log("test");
            $.when(loadContent($('#div-content-1'), 'view-boxes.html')).done(function () {
                let lastName = $('#nav-input-last-name').val();
                console.log(lastName);
                searchEmployeesByLastName(lastName);
            });
        });
        $('#nav-button-search-by-last-name-and-open-in-new-card').click(function () {
            let lastName = $('#nav-input-last-name').val();
            searchEmployeesByLastName();
        });
        $('#button-go-back').click(function () {
            goBack();
        });
        $('#button-view-lockers').click(function () {
            loadContent($('#div-content-1'), 'view-lockers.html');
        });
        $('#button-view-boxes').click(function () {
            loadContent($('#div-content-1'), 'view-boxes.html');
        });
        $('#button-measurement-list').click(function () {
            loadContent($('#div-content-1'), 'measurement-list.html');
        });
        $('#button-add-employee').click(function () {
            loadContent($('#div-content-1'), 'add-employee.html');
        });
        $('#button-add-position').click(function () {
            loadContent($('#div-content-1'), 'add-position.html');
        });
        $('#button-load-client-creation').click(function () {
            loadContent($('#div-content-1'), 'create-client.html');
        });
        $('#link-to-load-client-from-file').click(function () {
            loadContent($('#div-content-1'), 'create-client-from-file.html');
        });
        $('#link-to-add-lockers').click(function () {
            loadContent($('#div-content-1'), 'add-lockers.html');
        });
        $('#button-edit-departments').click(function () {
            loadContent($('#div-content-1'), 'edit-departments.html');
        });
        $('#button-manage-plant').click(function () {
            loadContent($('#div-content-1'), 'manage-plant.html');
        });
        $('#button-manage-lockers').click(function () {
            loadContent($('#div-content-1'), 'manage-lockers.html');
        });
        $('#button-articles').click(function () {
            loadContent($('#div-content-1'), 'articles.html');
        });
        $('#button-management-list').click(function () {
            loadContent($('#div-content-1'), 'management-list.html');
        });
        $('#button-reports').click(function () {
            loadContent($('#div-content-1'), 'orders.html');
        });
        $('#button-orders').click(function () {
            loadContent($('#div-content-1'), 'orders-view.html');
        })
        $('#button-qr-reader').click(function () {
            loadContent($('#div-content-1'), 'qr-reader.html');
        });
    });
}


function goBack() {
    actualPage = previousPages.pop();
    $('#div-content-1').load(actualPage);
}

function loadContent($element, source, saveSource) {
    if (saveSource === undefined) saveSource = true;
    if (previousPages.length > 0 && actualPage == previousPages[length - 1]) {
        //do nothing
    } else {
        previousPages.push(actualPage);
    }
    if (saveSource) actualPage = source;
    $element.load(source);
}


function loadPlants(actualUserId, $selectPlant) {
    if ($selectPlant === undefined) {
        $selectPlant = $('#select-plant');
    }
    if (actualUserId === undefined) {
        actualUserId = userId;
    }
    $.ajax({
        url: getPlantsByClient(actualUserId),
        method: 'get',
        success: function (plants) {
            appendOptionsToSelect(plants, $selectPlant);
        }
    });
}

function loadClientByPlantNumber(plantNumber) {
    if (plantNumber === undefined) {
        plantNumber = $('#input-plant-number').val();
    }
    $.ajax({
        url: putActualClientByPlantNumberToUser(plantNumber, userId),
        method: 'put',
        success: function (response) {
            if (response.succeed) {
                loadContent($('#div-content-1'), 'empty.html');
                previousPages = [];
                displayClientName(response.entity);
                loadedClientId = response.entity.id;
            }
        }
    })
}

function displayClient(client) {
    console.log(client);
    $('#client-name').text(client.name);
}

function loadClient() {
    $.ajax({
        url: getClientByUserId(userId),
        method: 'get',
        success: function (client) {
            displayClient(client);
            loadedClientId = client.id;
            loadedClient = client;
        }
    })
}

function displayClientName(client) {
    console.log(client);
    $('#client-name').text(client.name);
}


function getClients($selectClient) {
    if ($selectClient === undefined) {
        $selectClient = $('#select-client');
    }
    $.ajax({
        url: getActualLocation() +
            `/client/get-all`,
        method: 'get',
        success(clients) {
            appendCollectionToSelectWithPlaceholder(
                clients,
                $selectClient,
                "Wybierz klienta");

        }
    })
}

function loadDepartments($selectDepartment, placeholder) {
    let departments = loadedClient.departments;
    removeOptionsFromSelect($selectDepartment);
    console.log(departments);
    appendOptionsToSelect(
        sort(departments, "name"), $selectDepartment, placeholder);
}

function getAndLoadDepartments(userId, $selectDepartment, placeholder, loadPositions) {
    if ($selectDepartment === undefined) {
        $selectDepartment = $('#select-department');
    }
    $.ajax({
        url: getDepartments(userId),
        method: 'get',
        success: function (departments) {
            removeOptionsFromSelect($selectDepartment);
            console.log(departments);
            appendOptionsToSelect(
                sort(departments, "name"), $selectDepartment, placeholder);
        }
    }).done(function () {
        if (loadPositions != undefined) {
            loadPositions();
        }
    });
}

function loadPositions($select, placeholder) {
    let positions = loadedClient.positions;
    removeOptionsFromSelect($select);
    appendOptionsToSelect(
        sort(positions, "name"),
        $select,
        placeholder);
}

function getAndLoadPositions(userId, $select) {
    $.ajax({
        url: getPositions(userId),
        method: 'get',
        success: function (positions) {
            appendCollectionToSelectWithPlaceholder(
                positions, $select, "Stanowisko");
        }
    })
}

function getAndDisplayPosition(positionId) {
    $.ajax({
        url: getPosition(positionId),
        method: 'get',
        success: function (position) {
            displayPosition(position);
        }
    })
}

function loadClientArticles(clientId, $selectArticles) {
    $.ajax({
        url: getActualLocation() +
            `/client-articles/get-all` +
            `/${clientId}`,
        method: 'get',
        success: function (clientArticles) {
            console.log(clientArticles);
            $selectArticles.append(createSelectPlaceholder("Artykuł"));
            appendOptionsToSelect(
                extractArticleNumberNameAndIdFromClientArticles(clientArticles), $selectArticles);
        }
    })
}

function loadLocations($selectLocation, placeholder) {
    let locations = loadedClient.locations;
    removeOptionsFromSelect($selectLocation);
    appendOptionsToSelect(
        sort(locations, "name"),
        $selectLocation,
        placeholder);
}

function getAndLoadLocations(userId, $selectLocation, placeholder) {
    if ($selectLocation === undefined) {
        $selectLocation = $('#select-location');
    }
    $.ajax({
        url: getLocations(userId),
        method: 'get',
        success: function (locations) {
            console.log(locations);
            appendOptionsToSelect(
                sort(locations, "name"),
                $selectLocation,
                placeholder);
        }
    })
}



function loadPositionsByDepartment($selectDepartment, $selectPosition) {
    if ($selectDepartment == undefined || $selectPosition == undefined) {
        $selectDepartment = $('#select-department');
        $selectPosition = $('#select-position');
    }
    let departmentId = $selectDepartment.val();
    $selectPosition.find('option').each(function () {
        $(this).remove();
    });
    let positionsByDepartment = [];
    for(let e of loadedClient.departments) {
        if(e.id == departmentId) {
            positionsByDepartment = sort(e.positions, "name");
        }
    }
    appendOptionsToSelect(positionsByDepartment, $selectPosition, "Wybierz stanowisko");
}

function appendCollectionToSelectWithPlaceholder(collection, $select, placeholder) {
    removeOptionsFromSelect($select);
    $select.append(createSelectPlaceholder(placeholder));
    appendOptionsToSelect(collection, $select);
}

function appendOptionsToSelect(collection, $select, placeholder) {
    removeOptionsFromSelect($select);
    if (placeholder != undefined && placeholder != "") {
        $select.append(createSelectPlaceholder(placeholder));
    }
    if (collectionIsViable(collection)) {
        for (let i = 0; i < collection.length; i++) {
            appendOptionToSelect($select, collection[i]);
        }
    }
}

function appendOptionsToSelectAndSelectActual(list, $select, selected) {
    removeOptionsFromSelect($select);
    appendOptionsToSelect(list, $select);
    selectActualOptionInSelect($select, selected.id);
}

function selectActualOptionInSelect($select, actualOptionId) {
    $select.find('option').each(function () {
        if ($(this).val() == actualOptionId) {
            $(this).attr('selected', 'selected');
        }
    })
}

function appendOptionToSelect($select, entity) {
    let value = entity.id;
    let description = entity.name;
    let option = createOption(value, description);
    $select.append(option);
}

function removeOptionsFromSelect($select) {
    $select.find('option').remove();
}

function removeOptionsFromSelectWithout1stOption($select) {
    $select.find('option:not(option:nth-child(1))').remove();
}

function createOption(value, description) {
    return `<option value="${value}">${description}</option>`;
}

function createSelectPlaceholder(description) {
    return `<option value="0" selected>${description}</option>`;
}

function displayConfirmWindowForDownloadReport(response) {
    if (response.succeed)
        if (confirm("Wygenerowano raport: " +
            response.fileName +
            "\r\n" +
            "Czy chcesz go pobrać")) {
            window.open(response.fileDownloadUri)
        } else {
            window.alert("Coś poszło nie tak")
        }
}

function displayConfirmWindowForDownloadFile(response) {
    if (response.succeed) {
        if (confirm("Plik jest gotowy. Czy chcesz go pobrać?"))
            window.open(response.fileDownloadUri);
    } else {
        window.alert(response.message);
    }
}

function getPositionsByDepartment(department, departments) {
    console.log("weszło")
    let positions = [];
    if(department.surrogate) {
        return positions;
    } else {
        for(let d of departments) {
            if(d.id == department.id) {
                return d.positions;
            }
        }
        return positions;
    }
}

