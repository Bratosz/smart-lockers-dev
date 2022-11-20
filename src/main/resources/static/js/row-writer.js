function writeLockerToRow(locker, $row) {
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-locker-number').text(locker.lockerNumber);
    $row.find('.cell-capacity').text(locker.capacity);
    $row.find('.cell-plant-number').text(locker.plant.plantNumber);
    $row.find('.cell-department').text(locker.department.name);
    $row.find('.cell-location').text(locker.location.name);
    $row.find('.cell-id').text(locker.id);
    return $row;
}

function writeLockerToRowWithViewButton(locker, $row) {
    $row.removeAttr("id");
    $row.css("display", "table-row");
    $row.find(".cell-id").text(locker.id);
    $row.find(".cell-locker-number").text(locker.lockerNumber);
    $row.find(".cell-locker-number").css("font-weight", "700");
    $row.find(".cell-capacity").text(locker.capacity);
    $row.find(".cell-plant-number").text(locker.plant.plantNumber);
    $row.find(".cell-department").text(locker.department.name);
    $row.find(".cell-location").text(locker.location.name);
    $row.find(".button-view-locker").click(function () {
        lockerId = locker.id;
        loadContent($('#div-content-1'),'view-locker.html');
    });
    return $row;
}

function writeArticleToSelectionRow(articlesWithQuantity, $row) {
    let articles = articlesWithQuantity.availableArticles;
    $row.removeAttr("id");
    $row.attr('id', articlesWithQuantity.id);
    $row.css("display", "table-row");
    sort(articles, "article.number");
    for (let a of articles) {
        $row.find('.select-article').append(
            createOption(a.id,
                a.article.number + " " + a.article.name));
    }
    $row.find('.input-quantity').val(articlesWithQuantity.quantity);
    return $row;
}

function writeEmployeeToRowWithViewAndAddClothesButton(employee, $row) {
    $row.removeAttr("id");
    $row.css("display", "table-row");
    $row.find(".cell-id").text(employee.id);
    $row.find(".cell-last-name").text(employee.lastName);
    $row.find(".cell-first-name").text(employee.firstName);
    $row.find(".cell-department").text(employee.department.name);
    $row.find(".cell-location").text(employee.box.locker.location.name);
    $row.find(".cell-plant-number").text(employee.box.locker.plant.plantNumber);
    $row.find(".cell-locker-number").text(employee.box.locker.lockerNumber);
    $row.find(".cell-box-number").text(employee.box.boxNumber);
    $row.find(".button-view-locker").click(function () {
        window.location.href = `view-locker.html?id=${locker.id}`;
    });
    return $row;
}

function writeEmployeeWithActiveOrders(employee, $row) {
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.attr('id', employee.id);
    $row.find('.cell-department').text(employee.departmentName);
    $row.find('.cell-plant-number').text(employee.plantNumber);
    $row.find('.cell-locker-number').text(employee.lockerNumber);
    $row.find('.cell-box-number').text(employee.boxNumber);
    $row.find('.cell-last-name').text(employee.lastName);
    $row.find('.cell-first-name').text(employee.firstName);
    $row.find('.cell-active-orders').text(employee.activeOrders);
    $row.find('.button-view-employee').click(function () {
        employeeId = employee.id;
        loadContent($('#div-content-1'),'view-employee.html', false);
    });
    return $row;
}


function writePositionToRow(position, $row) {
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-position-name').text(position.name);
    $row.find('.button-edit-position').click(function () {
        displayPosition(position);
    });
    $row.find('.button-delete-position').click(function () {
        deletePositionById(position.id);
    });
    return $row;
}

function writeEmployeeToCreateToRow(employee, $row) {
    let $selectDepartment = $row.find('.select-department');
    let $selectPosition = $row.find('.select-position');
    let $selectLocation = $row.find('.select-location');
    $row.removeAttr('id');
    $row.attr('id', employee.id);
    $row.css('display', 'table-row');
    $row.find('.cell-last-name').text(employee.lastName);
    $row.find('.cell-first-name').text(employee.firstName);
    loadSelectInRowForEmployeeToCreate(
        $selectDepartment, loadedClient.departments, employee.department, "Wybierz oddział");
    loadPositionsForEmployeeToCreate(
        $selectPosition,
        getPositionsByDepartment(employee.department, loadedClient.departments),
        employee.position,
        "Wybierz stanowisko");
    loadSelectInRowForEmployeeToCreate(
        $selectLocation, loadedClient.locations, employee.location, "Wybierz lokalizację");
    $row.find('.button-swap-names').click(function () {
       swapNames(employee.id, $row);
    });
    $row.find('.select-department').change(function () {
        let $selectDepartment = $row.find('.select-department');
        let $selectPosition = $row.find('.select-position');
        loadPositionsByDepartment($selectDepartment, $selectPosition);
    });
    return $row;
}

function writeBoxToRow(box, $row) {
    let employee = box.employee;
    $row.removeAttr("id");
    $row.attr("id", box.id);
    $row.css("display", "table-row");
    $row.find(".cell-last-name").text(employee.lastName);
    $row.find(".cell-first-name").text(employee.firstName);
    if(box.locker != undefined) {
        $row.find(".cell-locker-number").text(box.locker.lockerNumber);
        $row.find(".cell-locker-number").css("font-weight", "700");
        $row.find(".cell-plant-number").text(box.locker.plant.plantNumber);
        $row.find(".cell-department").text(box.locker.department.name);
        $row.find(".cell-location").text(box.locker.location.name);
    }
    $row.find(".cell-box-number").text(box.boxNumber);
    $row.find(".cell-box-number").css("font-weight", "700");
    $row.find(".cell-status").text(box.boxStatus);
    if (box.boxStatus == "Zajęta") {
        $row.find(".button-view-employee").css("display", "table-cell");
        $row.find(".button-update-box").css("display", "none");
        $row.find(".button-view-employee").click(function () {
            employeeId = employee.id;
            loadContent($('#div-content-1'),'view-employee.html', false);
        });
        $row.find('.button-employee-edit').css('display', 'table-cell');
        $row.find('.button-employee-import').css('display', 'none');
        $row.find('.button-employee-edit').click(function () {
            employeeId = employee.id;
            loadContent($('#div-content-1'),'employee-edit.html', false);
        });
        $row.find(".button-dismiss-employee").css('display', 'table-cell');
        $row.find(".button-add-employee").css('display', 'none');
        $row.find(".button-dismiss-employee").click(function () {
            dismissEmployeeFromBoxesView(employee);
        });
    } else {
        $row.find('.button-update-box').css('display', 'table-cell');
        $row.find('.button-view-employee').css('display', 'none');
        $row.find('.button-update-box').click(function () {
            updateBox(box.id);
        });
        $row.find('.button-employee-import').css('display', 'table-cell');
        $row.find('.button-employee-edit').css('display', 'none');
        $row.find('.button-employee-import').click(function () {
            alert("Ta funkcja nie jest aktywna.");
        });
        $row.find(".button-add-employee").css("display", "table-cell");
        $row.find(".button-dismiss-employee").css("display", "none");
        $row.find(".button-add-employee").click(function () {
            boxIdForNewEmployee = box.id;
            loadContent($('#div-content-1'),'add-employee-to-box.html', false);
        });
    }
    $row.find(".button-delete-box").click(function () {
        if (confirm('Czy na pewno chcesz usunąć ten box nr ?' + box.boxNumber)) {
            deleteBox(box.id);
        }
    });
    return $row;
}

function writeLabeledRow(label, content, $row) {
    $row.find('.cell-label').text(label);
    $row.find('.cell-content').text(content);
    return $row;
}

function writeDepartmentToRow(department, $row) {
    $row.find('.cell-department-name').text(department.name);
    $row.find('.cell-main-plant-number').text(department.mainPlantNumber);
    return $row;
}

function writePlantToRow(plant, $row) {
    console.log(plant);
    $row.find('.cell-plant-number').text(plant.plantNumber);
    $row.find('.cell-plant-name').text(plant.name);
    $row.find('.cell-plant-login').text(plant.login);

    let locations = plant.locations;
    if (locations !== null) {
        locations = sort(locations, 'name');
        for (let l of locations) {
            let $element = $row.find('.cell-plant-locations').find('#li-template').clone();
            $element.removeAttr('id');
            $element.css('display', 'list-item');
            $element.text(l.name);
            $row.find('.list').append($element);
        }
    }
    return $row;
}

function writeOrdersWithEmployeesToTable(order, $row) {
    let employee = order.employee;
    let box = employee.box;
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.attr('id', order.id);
    $row.find('.cell-department-name').text(employee.department.name);
    $row.find('.cell-plant-number').text(box.locker.plant.plantNumber);
    $row.find('.cell-locker-and-box-number').text(box.locker.lockerNumber + "/" + box.boxNumber);
    $row.find('.cell-employee').text(employee.lastName + " " + employee.firstName);
    $row.find('.cell-clothes-ordinal-numbers').text(collectionToString(getOrdinalNumbers(order)));
    $row.find('.cell-order-type').text(order.orderType);
    $row.find('.cell-desired-size').text(order.desiredSize);
    $row.find('.cell-desired-article-name').text(order.desiredClientArticle.article.name);
    $row.find('.cell-desired-article-number').text(order.desiredClientArticle.article.number);
    if(order.orderType == "Zmiana artykułu") {
        $row.find('.cell-actual-article-name').text(order.previousClientArticle.article.name);
        $row.find('.cell-actual-article-number').text(order.previousClientArticle.article.number);
    }
    $row.find('.cell-creation-date').text(formatDateDMY(employee.created));
    $row.find('.button-set-order-as-reported').click(function () {
       let mainOrderId = order.id;
       reportOrder(mainOrderId);
    });
    return $row;
}

function writeEmployeeToRowOfMeasurementList(employee, $row) {
    let box = employee.box;
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.attr('id', employee.id);
    $row.find('.cell-full-name').text(employee.lastName + " " + employee.firstName);
    $row.find('.cell-position').text(employee.position.name);
    $row.find('.cell-plant-number').text(box.locker.plant.plantNumber);
    $row.find('.cell-department').text(employee.department.name);
    $row.find('.cell-location').text(box.locker.location.name);
    $row.find('.cell-locker-number-and-box-number').text(box.locker.lockerNumber + "/" + box.boxNumber);
    $row.find('.cell-creation-date').text(formatDateDMY(employee.created));
    if (isEmployeeMeasured(employee)) {
        $row.find('.button-edit-employee').css('display', 'table-cell');
        $row.find('.button-edit-employee').click(function () {
            employeeId = employee.id;
            loadContent($('#div-content-1'),'view-employee.html', false);
        })
    } else {
        if(somethingLeftToMeasure(employee)) {
            $row.find('.btn-danger').css('display', 'table-cell');
            $row.find('.btn-danger').click(function () {
                employeeId = employee.id;
                loadContent($('#div-content-1'),'add-clothes.html', false);
            });
        } else {
            $row.find('.btn-outline-danger').css('display', 'table-cell');
            $row.find('.btn-outline-danger').click(function () {
                employeeId = employee.id;
                loadContent($('#div-content-1'),'add-clothes.html', false);
            });
        }
    }
    $row.find('.button-relocate-employee').click(function () {
        employeeId = employee.id;
        loadContent($('#div-content-1'),'employee-edit.html', false);
    });
    $row.find('.button-dismiss-employee').click(function () {
        if (confirm('Czy na pewno chcesz zwolnić pracownika ' +
            employee.firstName + ' ' + employee.lastName + '?')) {
            dismissEmployeeFromMeasurementListView(employee.id);
        }
    });
    $row.find('.button-delete-from-list').click(function () {
        removeEmployeeFromMeasurementList(employee.id);
    });
    return $row;
}

function addRowWithArticleAndQuantityAndSelection(awq, $row) {
    let articles = getArticles(awq.availableArticles);
    let quantity = awq.quantity;
    let awqId = awq.id;
    let clothType = getClothTypeFromArticlesWithQuantities(awq);
    $row.find('.cell-label').text(articles);
    $row.find('.cell-content').text(quantity);
    $row.find('.cell-button').css("display", "table-cell");
    $row.find('.cell-select').css("display", "table-cell");
    articles = getArticlesByClothType(clientArticlesArray, clothType);
    articles = sort(articles, 'article.number');
    console.log(clothType);
    for (let a of articles) {
        $row.find('.select-alternate-article').append(
            createOption(a.id,
                a.article.number + " " +
                a.article.name));
    }
    $row.find('.button-add-alternate-article').click(function () {
        addAnotherArticle(
            $row.find('.select-alternate-article').val(),
            awqId,
            loadedPositionId);
    });
    return $row;
}

