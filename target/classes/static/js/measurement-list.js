loadEmployees();

$('#button-load-employees').click(function () {
    let listType = $('#select-employees-list-type').val();
    if (listType == "TO_RELEASE") {
        loadEmployeesToCreate();
    } else if (listType == "TO_MEASURE") {
        loadEmployeesToMeasure();
    }
});

$('#button-go-to-add-employee').click(function () {
    loadContent($('#div-content-1'),'add-employee.html');
});

$('#button-go-to-employees-pasting').click(function () {
    loadContent($('#div-content-1'), 'employees-paste.html');
});

$('#button-go-to-employees-pasted').click(function () {
    $.ajax({
        url: getEmployeesToCreate(),
        method: 'get',
        success: function (response) {
            if(response.entity.length > 0) {
                loadContent($('#div-content-1'), 'employees-creating.html');
            } else {
                loadContent($('#div-content-1'), 'employees-paste.html');
            }
        }
    })
});

$('#button-download-measurement-list').click(function () {
    $.ajax({
        url: getActualLocation() +
            `/report/get-employees-to-measure-list` +
            `/${userId}`,
        method: 'get',
        success: function (response) {
            displayConfirmWindowForDownloadReport(response);
        }
    })
});

$('#button-download-report-of-measured-employees').click(function () {
    $.ajax({
        url: getReportForNewEmployees(),
        method: 'get',
        success: function (response) {
            displayConfirmWindowForDownloadReport(response);
        }
    });
});


$('#button-set-measured-employees-as-assigned').click(function () {
    console.log("Klik");
    if(confirm("Czy pracownicy zostali przypisani do systemu?")) {
        $.ajax({
            url: postSetMeasuredEmployeesAsAssigned(),
            method: 'post',
            success: function(response) {
                if(response.succeed) {
                    loadEmployeesToCreate();
                    loadEmployeesToRelease();
                }
            }
        })
    }
})

function loadEmployees() {
    // connectToDB();
    loadEmployeesToMeasure();
    loadEmployeesToCreate();
    loadEmployeesToRelease();

}

function connectToDB() {
    if (!window.indexedDB) {
        alert("Twoja przeglądarka nie obsługuje indeksowanej bazy danych!");
    } else {
        let db;
        let request = window.indexedDB.open("Test db");
        request.onerror = function (event) {
            alert("Udziel przeglądarce pozwolenia na utworzenie lokalnej bazy dancyh, lub skontaktuj się z dostawcą oprogramowania.")
        }
        request.onsuccess = function (event) {
            db = event.target.result;
            db.onerror = function (event) {
                console.error("DB error: " + event.target.errorCode);
            }
            console.log(event.target.result);
            console.log("Udałosie");
        }
    }
}

function loadEmployeesToCreate() {
    $.ajax({
        url: getEmployeesToAssign(),
        method: 'get',
        success: function (employees) {
            if (employees.length == 0) {
                $('#div-employees-to-assign').css('display', 'none');
            } else {
                $('#div-employees-to-assign').css('display', 'inline');
                displayEmployeesForMeasurementList(employees, $('#table-of-employees-to-assign'));
            }
        }
    })
}

function loadEmployeesToRelease() {
    $.ajax({
        url: getEmployeesToRelease(),
        method: 'get',
        success: function (employees) {
            if(employees.length == 0) {
                $('#div-employees-to-release').css('display', 'none');
            } else {
                $('#div-employees-to-release').css('display', 'inline');
                displayEmployeesForMeasurementList(employees, $('#table-of-employees-to-release'));
            }
        }
    })
}

function loadEmployeesToMeasure() {
    $.ajax({
        url: getEmployeesToMeasure(),
        method: 'get',
        success: function (employees) {
            console.log(employees);
            if (employees.length == 0) {
                $('#div-employees-to-measure').css('display', 'none');
                $('#button-download-measurement-list').css('display', 'none')
            } else {
                $('#div-employees-to-measure').css('display', 'inline');
                $('#button-download-measurement-list').css('display', 'initial');
                displayEmployeesForMeasurementList(employees, $('#table-of-employees-to-measure'));
            }
        }
    })
}


function displayEmployeesForMeasurementList(employees, $table) {
    writeDataToTable(
        sort(employees,
            'box.locker.plant.plantNumber',
            'box.locker.lockerNumber',
            'box.boxNumber'),
        $table,
        writeEmployeeToRowOfMeasurementList);
}

function isEmployeeMeasured(employee) {
    let mainOrders = employee.mainOrders;
    if (mainOrders == undefined ||
        mainOrders.length == 0 ||
        somethingLeftToMeasure(employee)) {
        return false;
    } else {
        return true;
    }
}

function somethingLeftToMeasure(employee) {
    let articlesToMeasure = employee.position.articlesWithQuantities.length;
    let activeMainOrders = countActiveMainOrders(employee.mainOrders);
    if((articlesToMeasure > activeMainOrders) && (activeMainOrders > 0))
        return true;
    else
        return false;
}

function countActiveMainOrders(mainOrders) {
    let counter = 0;
    for(let o of mainOrders) {
        if(o.active) counter++;
    }
    return counter;
}

function removeEmployeeFromMeasurementList(employeeId) {
    $.ajax({
        url: postRemoveEmployeeFromList(employeeId),
        method: 'post',
        success: function (responseWithEmployee) {
            $('#' + employeeId).remove();
            if (!responseWithEmployee.succeed) {
                alert(responseWithEmployee.message);
            }
        }
    })
}

function dismissEmployeeFromMeasurementListView(employeeId) {
    let clothesIsReturned = true;
    $.ajax({
        url: postDismissEmployee(clothesIsReturned, employeeId),
        method: 'post',
        success: function (response) {
            loadEmployees();
        }
    });
}