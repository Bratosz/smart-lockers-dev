reloadEmployee();
loadDefaultBehaviour(
    $('#modal-exchange-description'),
    $('#input-exchange-description'));

$("#refresh-employee-button").click(function () {
        updateEmployee();
});

$("#button-perform-action-on-main-orders").click(function () {
    performActionOnOrders();
});

$('#button-add-order-for-order-clothes').click(function () {
    if (confirm("Czy na pewno?")) {
        addExchangeOrder();
    }
});

$('#button-add-length-modification-order').click(function () {
    alert("Ta funkcja nie jest aktywna");
});

$('#button-add-repair-order').click(function () {
    alert("Ta funkcja nie jest aktywna");
});

$('#select-department').change(function () {
    loadPositionsByDepartment($('#select-department'), $('#select-position'));
});

$('#button-change-size').click(function () {
    setButtonAsClicked($(this), $('#button-change-article'), 'danger');
    actualOrderType = 'CHANGE_SIZE';
    let clientArticleId = $('#select-article-to-change-for-order-clothes').val();
    showFlex($('#div-order-clothes'));
    hide($('#select-desired-article-for-order-clothes'));
    selectClothesByClientArticleId(clientArticleId);
});

$('#button-length-modification').click(function () {
    addLengthModificationOrder();
});

$('#button-confirm-exchange-clothes-for-new-ones').click(function () {
        actualOrderType = "EXCHANGE_FOR_NEW_ONE";
        addExchangeOrder();
});

$('#button-change-article').click(function () {
    if(loadedEmployee.position == null) {
        alert("Pracownik nie ma przypisanego stanowiska. Przypisz je klikając przycisk zmień, a następnie wybierz ponownie zmianę artykułu.");
    } else {
        setButtonAsClicked($(this), $('#button-change-size'), 'danger');
        actualOrderType = 'CHANGE_ARTICLE';
        let clientArticleId = $('#select-article-to-change-for-order-clothes').val();
        showFlex($('#div-order-clothes'));
        showFlex($('#select-desired-article-for-order-clothes'));
        selectClothesByClientArticleId(clientArticleId);
        displayAvailableArticlesByPosition($('#select-desired-article-for-order-clothes'));
    }
});

$('#button-add-new-article').click(function () {
    if (confirm("Czy na pewno?")) {
        actualOrderType = "NEW_ARTICLE";
        addNewArticle();
    }
});

$('#select-article-to-change-for-order-clothes').change(function () {
    let clientArticleId = $('#select-article-to-change-for-order-clothes').val();
    selectClothesByClientArticleId(clientArticleId);
});

$('#button-return-rotational-clothes').click(function () {
    let barcodes = getCheckedBarcodes($('#table-of-rotational-clothes-body'));
    if(barcodes.length > 0) {
        $.ajax({
            url: postReturnRotationalClothes(userId),
            method: 'post',
            contentType: 'application/json',
            data: JSON.stringify(barcodes),
            success: function(response) {
                if(response.succeed) {
                    reloadEmployee();
                    alert("Wycofano odzież");
                } else {
                    alert("Coś poszło nie tak!");
                }
            }
        })
    } else {
        alert("Nie zaznaczono żadnej odzieży.")
    }
})

$('#button-edit-department-and-position').click(function () {
    let actualPositionId = 0;
    let actualDepartmentId = loadedEmployee.department.id;
    if (loadedEmployee.position != null) actualPositionId = loadedEmployee.position.id;
    let desiredDepartmentId = $('#select-department').val();
    let desiredPositionId = $('#select-position').val();
    if (actualDepartmentId == desiredDepartmentId && actualPositionId == desiredPositionId) {
        alert("Żeby zmienić oddział lub stanowisko, musisz wybrać inne niż obecne!")
    } else {
        $.ajax({
            url: postChangeEmployeeDepartmentAndPositionForOldEmployee(employeeId, desiredDepartmentId, desiredPositionId, userId),
            method: 'post',
            success: function (response) {
                if (response.succeed) {
                    alert("Zmieniono stanowisko");
                    loadedEmployee = response.entity;
                    reloadEmployee();
                } else {
                    alert(response.message);
                }
            }
        })
    }
});

$(document).ready(function () {
    $('#input-barcode-cloth-assignment')
        .keypress(function (event) {
            if (pressedKeyIsENTER(event.which)) {
                let barcode = $(this).val();
                if (barcodeIsValid(barcode)) {
                    const assignmentType = getAssignmentType();
                    if (assignmentType == "ASSIGN_WITHDRAWN_CLOTH") {
                        let withDrawnCloth = getWithdrawnCloth(barcode);
                        let article = getDesiredClientArticleId();
                        let size = getSize();
                        sendRequestForAssignWithdrawnCloth(withDrawnCloth, article, size)
                    } else if (assignmentType == "RELEASE_ROTATIONAL_CLOTH") {
                        sentRequestForReleaseRotationalCloth(barcode)
                    } else if (assignmentType == "ACCEPT_CLOTH") {
                        sendAutoExchange(barcode);
                    }
                    clearInput($(this));
                } else {
                    alert("Nieprawidłowy kod kreskowy!");
                }
            }
        });
});

function sendAutoExchange(barcode) {
    $.ajax({
        url: postAutoExchange(barcode),
        method: 'post',
        success: function (response) {
            alert(response.message);
        }
    })
}

function updateEmployee() {
        $.ajax({
            url: getUpdatedEmployee(employeeId),
            method: 'get',
            success: function (response) {
                console.log(response);
                if (response.succeed) {
                    window.alert(response.message);
                    displayEmployeeForEmployeeView(response.entity);
                } else {
                    window.alert(response.message);
                }
            }
        });
}

function refreshOrders() {
    $.ajax({
        url: getOrdersByEmployee(employeeId),
        method: 'get',
        success: function (mainOrders) {
            console.log(mainOrders);
            let activeOrders = extractActiveOrders(mainOrders);
            displayOrders(activeOrders);
        }
    })
}

function addNewArticle() {
    let parameters = {
        clientArticleId: getDesiredClientArticleId($('#select-article-to-add')),
        size: getSize($('#input-size-for-add-article')),
        lengthModification: getLengthModificationFromInput($('#input-length-modification-for-add-article')),
        quantity: getQuantity($('#input-quantity-for-add-article')),
        employeeId: loadedEmployee.id,
        orderType: actualOrderType
    };
    if(parameters.quantity <= 0) alert("Ilość musi być większa niż 0");
    else {
        $.ajax({
            url: postNewOrdersBy(userId),
            method: 'post',
            contentType: "application/json",
            data: JSON.stringify(parameters),
            success: function (response) {
                alert(response.message);
                reloadEmployee();
            }
        }).done(function () {
            actualOrderType = "EMPTY";
        })
    }
}

function addLengthModificationOrder() {
    let parameters = {
        barcodes: getCheckedBarcodes($('#table-of-clothes-body')),
        orderType: "LENGTH_MODIFICATION",
        lengthModification: getLengthModificationFromInput($('#input-length-modification-for-modification-only'))
    }
    if(parameters.lengthModification == "NONE") {
        alert("Podano niewłaściwą wartość!")
    } else {
        console.log(parameters);
        $.ajax({
            url: postNewOrdersBy(userId),
            method: 'post',
            contentType: 'application/json',
            data: JSON.stringify(parameters),
            success: function (response) {
                window.alert(response.message);
                reloadEmployee();
            }
        })
    }
}

function addExchangeOrder() {
    let parameters = {
        barcodes: getCheckedBarcodes($('#table-of-clothes-body')),
        clientArticleId: getDesiredClientArticleId($('#select-desired-article-for-order-clothes')),
        size: getSize($('#input-size-for-order-clothes')),
        orderType: actualOrderType,
        lengthModification: getLengthModificationFromInput($('#input-length-modification-for-order-clothes')),
        exchangeDescription: getValueFromInputText($('#input-exchange-description'))
    };
    console.log(parameters);
    $.ajax({
        url: postNewOrdersBy(userId),
        method: 'post',
        contentType: "application/json",
        data: JSON.stringify(parameters),
        success: function (response) {
            window.alert(response.message);
            reloadEmployee();
        }
    });
}

function selectClothesByClientArticleId(clientArticleId) {
    let activeClothes = loadedEmployee.clothes.filter(c => c.active);
    let foundClothes = activeClothes.filter(c => c.clientArticle.id == clientArticleId);
    if (foundClothes.length > 0) {
        let cloth = foundClothes.pop();
        let articleNumber = cloth.clientArticle.article.number;
        selectClothesByArticleNumber(articleNumber, $('#table-of-clothes'));
    }
}

function selectClothesByArticleNumber(articleNumber, $table) {
    $table.find('tr').each(function () {
        console.log(articleNumber);
        if ($(this).find('.cell-article-number').text() == articleNumber) {
            $(this).find('input').prop('checked', true);
        } else {
            $(this).find('input').prop('checked', false);
        }
    })
}

function performActionOnOrders() {
    let actionType = $('input[name="manage-order"]:checked').val();
    let orderEditInfoArray = new Array;
    $('#table-of-main-orders-body').find('input[type="checkbox"]:checked').each(function () {
        let orderId = parseInt($(this).closest('tr').find('.cell-main-order-id').text());
        let desiredSize = $(this).closest('tr').find('.input-size').val();
        let desiredClientArticleId = $(this).closest('tr').find('.select-article-in-orders-table').val();
        console.log(desiredClientArticleId);
        let lengthModification = $(this).closest('tr').find('.input-length-modification').val();
        if (desiredSize == "") desiredSize = "Taki sam";
        if (lengthModification == "") lengthModification = "NONE";
        if (desiredClientArticleId == undefined) desiredClientArticleId = 0;
        let orderEditInfo = {
            orderId: orderId,
            clothSize: desiredSize,
            lengthModification: lengthModification,
            clientArticleId: desiredClientArticleId
        };
        orderEditInfoArray.push(orderEditInfo);
    });
    $.ajax({
        url: putPerformActionOnOrders(actionType),
        method: 'put',
        contentType: "application/json",
        data: JSON.stringify(orderEditInfoArray),
        success: function (response) {
            alert(response.message);
            reloadEmployee();
        },
        error: function (e) {
            alert("Podano nieprawidłowe parametry.");
        },

    });
}

function reloadEmployee() {
    $.ajax({
        url: getEmployeeWithCompleteInfo(employeeId),
        method: 'get',
        success: function (employee) {
            console.log(employee);
            loadedEmployee = employee;
            displayEmployeeForEmployeeView(employee);
        }
    }).done(function () {
        loadForEmployeePositionAndDepartment();
    })

}

function addToManagedEmployees() {
    $.ajax({
        url: postAddEmployeeToManagementList(employeeId),
        method: 'post',
        success: function (response) {
            window.alert(response.message)
        }
    })
}

function displayArticlesToAddInAddPanel(position) {
    let $panel = $('#order-new-clothes-panel');
    let $select = $('#select-article-to-add');
    if(position != null) {
        showFlex($panel);
        displayAvailableArticlesByPosition($select);
    } else {
        hide($panel);
    }
}

function displayArticlesForChangeInExchangePanel(clientArticles) {
    let $panel = $('#div-exchange-clothes-panel');
    if(clientArticles.length > 0) {
        showFlex($panel);
        let $select = $('#select-article-to-change-for-order-clothes');
        removeOptionsFromSelect($select);
        for (let clientArticle of clientArticles) {
            $select.append(createOption(
                clientArticle.id,
                clientArticle.article.number + " " + clientArticle.article.name));
        }
        hide($('#div-change-article'));
        hide($('#div-change-size'));
    } else {
        hide($panel);
    }
}

function displayActiveClothes(clothes) {
    if (clothes.length > 0) {
        show($('#div-clothes-active'));
        writeClothesToTable($("#table-of-clothes-body"), clothes);
    } else {
        hide($('#div-clothes-active'));
    }
}

function displayClothesBeforeRelease(clothes) {
    if (clothes.length > 0) {
        show($('#div-clothes-to-release'));
        writeClothesToTable($("#table-of-clothes-to-release-body"), clothes);
    } else {
        hide($('#div-clothes-to-release'));
    }
}

function displayRotationalClothes(clothes) {
    if(clothes.length > 0) {
        show($('#div-rotational-clothes'));
        writeClothesToTable($("#table-of-rotational-clothes-body"), clothes);
    } else {
        hide($('#div-rotational-clothes'));
    }
}

function displayAcceptedClothes(clothes) {
    if(clothes.length > 0) {
        show($('#div-accepted-clothes'));
        writeClothesToTable($("#table-of-accepted-clothes-body"), clothes);
    }  else {
        hide($('#div-accepted-clothes'));
    }
}

function displayWithdrawnClothes(clothes) {
    if(clothes.length > 0) {
        show($('#div-withdrawn-clothes'));
        writeClothesToTable($("#table-of-withdrawn-clothes-body"), clothes);
    } else {
        hide($('#div-withdrawn-clothes'));
    }
}

function displayOrders(mainOrders) {
    if (mainOrders.length > 0) {
        $('#div-orders').css('display', 'inline');
        writeOrdersToTable($("#table-of-main-orders-body"), mainOrders);
    } else {
        $('#div-orders').css('display', 'none');
    }
}

