$(document).ready(function () {
    $('#input-barcode-cloth-acceptance').keypress(function (event) {
        if (pressedKeyIsENTER(event.which)) {
            let barcode = $(this).val();
            if (barcodeIsValid(barcode)) {
                let acceptanceType = getAcceptanceType();
                if(acceptanceType == "CHECK_STATUS") {
                    sendAcceptanceRequest(
                        acceptanceType,
                        barcode);
                    clearInput($(this));
                } else {
                    let exchangeType = getExchangeType(),
                        size = getSize($('input[name="size"]:checked')),
                        articleNumber = getDesiredClientArticleId();
                    sendExchangeRequest(exchangeType, barcode, size, articleNumber);
                    clearInput($(this));
                }
            } else {
                alert("Nieprawidłowy kod kreskowy!");
            }
        }
    })
});

function getWithdrawnCloth(barcode) {
    let withdrawnCloth = {
        ordinalNumber: getOrdinalNumberFromInput(),
        barcode: barcode,
        assignment: getAssignmentDateFromInput()
    };
    return withdrawnCloth;
}

function getAssignmentType() {
    return $('input[name="assignment"]:checked').val();
}

function barcodeIsValid(barCode) {
    if (barCode.length == 13) {
        return true;
    } else {
        return false;
    }
}

function getExchangeType() {
    return $('input[name="exchange"]:checked').val();
}

function getAcceptanceType() {
    return $('input[name="acceptance"]:checked').val();
}

function clearInput($input) {
    $input.val("");
}

function sendRequestForAssignWithdrawnCloth(withdrawnCloth, article, size) {
    $.ajax({
        url: `http://localhost:8080/cloth/assign-withdrawn-cloth/${userId}/${employeeId}/${article}/${size}`,
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(withdrawnCloth),
        success: function (response) {
            console.log(response);
            reloadEmployee();
        }
    })
}

function sentRequestForReleaseRotationalCloth(barcode) {
    $.ajax({
        url: getActualLocation() +
            `/rotational-cloth/release` +
            `/${barcode}/${employeeId}/${userId}`,
        method: "post",
        success: function (response) {
            console.log(response);
            reloadEmployee();
        }
    })
}

function sendAcceptanceRequest(acceptanceType, barcode) {
    $.ajax({
        url: getActualLocation() +
            `/cloth/accept` +
            `/${acceptanceType}` +
            `/${barcode}` +
            `/${userId}`,
        method: "post",
        success: function (clothResponse) {
            if(clothResponse.found) {
                if(window.confirm(clothResponse.message + "\n" +
                "Czy chesz przejść do pracownika?")) {
                    window.location.href = `view-employee.html` +
                        `?employee-id=${clothResponse.cloth.employee.id}`
                }
            } else {
                window.alert(clothResponse.message)
            }
        }
    })
}

function sendExchangeRequest(exchangeType, barcode, size, articleNumber) {
    $.ajax({
        url: getActualLocation() +
            `/cloth/exchange` +
            `/${exchangeType}` +
            `/${barcode}` +
            `/${size}` +
            `/${articleNumber}` +
            `/${userId}`,
        method: 'post',
        success: function(response) {
            window.alert(response.message);
        }
    })
}

function pressedKeyIsENTER(char) {
    let ENTERKeyValue = 13;
    if (char == ENTERKeyValue) {
        return true;
    } else {
        return false;
    }
}