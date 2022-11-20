loadEmployee(employeeId);

$('#button-order-clothes').click(function () {
    orderClothes(employeeId);
});

$('#button-view-employee').click(function () {
    loadContent($('#div-content-1'),'view-employee.html', false);
});

$('#select-department').change(function () {
    loadPositionsByDepartment($('#select-department'), $('#select-position'));
});

$('#button-edit-department-and-position').click(function () {
    let actualDepartmentId = loadedEmployee.department.id;
    let actualPositionId = loadedEmployee.position.id;
    let desiredDepartmentId = $('#select-department').val();
    let desiredPositionId = $('#select-position').val();
    if(actualDepartmentId == desiredDepartmentId && actualPositionId == desiredPositionId) {
        alert("Żeby zmienić oddział lub stanowisko, musisz wybrać inne niż obecne!")
    } else {
        $.ajax({
            url: postChangeEmployeeDepartmentAndPositionForNewEmployee(employeeId, desiredDepartmentId, desiredPositionId, userId),
            method: 'post',
            success: function (response) {
                if(response.succeed) {
                    let employee = response.entity;
                    loadedEmployee = employee;
                    displayEmployeeForAddClothesView(employee);
                } else {
                    alert(response.message);
                }
            }
        })
    }
});

function loadEmployee(employeeId) {
    $.ajax({
        url: getEmployee(employeeId),
        method: 'get',
        success: function (employee) {
            console.log(employee);
            displayEmployeeForAddClothesView(employee);
            loadedEmployee = employee;
        }
    }).done(function () {
        loadForEmployeePositionAndDepartment();
    });
}

function displayEmployeeForAddClothesView(employee) {
    let box = employee.box;
    $('#employee-info').text(
        box.locker.plant.plantNumber +
        " " + employee.position.name +
        " " + employee.lastName + " " + employee.firstName +
        " " + box.locker.lockerNumber + "/" + box.boxNumber);
    let articlesWithQuantities = employee.position.articlesWithQuantities;
    writeDataToTable(
        articlesWithQuantities,
        $('#table-of-clothes-selection'),
        writeArticleToSelectionRow);
    writeMeasuredArticles(employee.mainOrders, articlesWithQuantities);
}

function writeMeasuredArticles(mainOrders, articlesWithQuantities) {
    mainOrders = mainOrders.filter(m => m.active == true);
    for (let order of mainOrders) {
        let foundArticleToOrderId = 0;
        for (let articleWithQuantity of articlesWithQuantities) {
            let a = articleWithQuantity.availableArticles.filter(
                ava => ava.id == order.desiredClientArticle.id);
            if (a.length > 0) {
                foundArticleToOrderId = articleWithQuantity.id;
            }
        }
        if (foundArticleToOrderId != 0) {
            let $row = $('#' + foundArticleToOrderId);
            console.log("test");
            console.log($row.find('option'));
            // $row.find('option').filter(o => o.val == order.desiredClientArticle.id).attr('selected', 'selected');
            $row.find('select option[value=' + order.desiredClientArticle.id + ']')
                .attr('selected', 'selected');
            $row.find('.input-size').val(order.desiredSize);
            $row.find('.input-length-modification')
                .val(putLengthModification(order.lengthModification));
            $row.find('.input-quantity')
                .val(order.clothOrders.filter(c => c.active == true).length);
        }
    }
}

function putLengthModification(lengthModification) {
    if (lengthModification == "NONE") {
        return ""
    } else {
        return lengthModification;
    }
}

function getArticlesParameters() {
    let articlesParameters = [];
    $('#table-of-clothes-selection > tbody > tr').each(function (index, row) {
        if ($(row).attr('id') != "row-template") {
            let parameters = {
                articlesWithQuantityId: $(row).attr('id'),
                clientArticleId: $(row).find('.select-article').val(),
                size: getSizeFromTextInputForAddClothes($(row).find('.input-size').val().toUpperCase()),
                lengthModification: getLengthModificationFromInput($(row).find('.input-length-modification')),
                quantity: $(row).find('.input-quantity').val(),
                employeeId: loadedEmployee.id
            };
            console.log(parameters);
            articlesParameters.push(parameters);
        }
    });
    return articlesParameters;
}



function orderClothes(employeeId) {
    let orderParametersForNewArticles = getArticlesParameters();
    $.ajax({
        url: postOrdersForNewClothes(employeeId),
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(orderParametersForNewArticles),
        success: function (response) {
            alert(response.message);
            if(confirm("Czy chcesz przypisać rotację?")) {
                loadContent($('#div-content-1'),`view-employee.html`, false);
            } else {
                loadContent($('#div-content-1'),`measurement-list.html`, false);
            }
        }
    });
}

function getClothesQuantity(employee, clothType) {
    for (let ctq of employee.position.clothTypesWithQuantities) {
        if (ctq.clothType == clothType) {
            return ctq.quantity;
        }
    }
}