loadOrders();


function loadOrders() {
    loadOrdersToReport();
}

function loadOrdersToReport(orders) {
    if(orders === undefined) {
        $.ajax({
            url: getOrdersToReport(),
            method: 'get',
            success: function (response) {
                ordersToReport = response.entity;
                console.log(ordersToReport);
                if (ordersToReport.length == 0) {
                    $('#div-orders-to-report').css('display', 'none');
                } else {
                    $('#div-orders-to-report').css('display', 'inline');
                    displayOrdersForOrdersList(ordersToReport, $('#table-of-orders-to-report'));
                }
            }
        })
    } else {
        if (orders.length == 0) {
            $('#div-orders-to-report').css('display', 'none');
        } else {
            $('#div-orders-to-report').css('display', 'inline');
            displayOrdersForOrdersList(orders, $('#table-of-orders-to-report'));
        }
    }
}

$('#button-filter-orders').click(function () {
        let orderType = $('#select-order-type').val();
        let filteredOrders = filterOrders(ordersToReport, orderType);
        loadOrdersToReport(filteredOrders);
    }
);

function filterOrders(orders, orderType) {
    let filteredOrders = [];
    let orderNewArticleName = "Nowy artykuł";
    if(orderType == "REPORTS") {
        for(let o of orders) {
            if(o.orderType != orderNewArticleName) {
                filteredOrders.push(o);
            }
        }
        return filteredOrders;
    } else if (orderType == "NEW_ARTICLES") {
        for(let o of orders) {
            if(o.orderType == orderNewArticleName) {
                filteredOrders.push(o);
            }
        }
        return filteredOrders;
    } else {
        return orders;
    }
}

$('#button-get-report-for-all-orders').click(function () {
    $.ajax({
        url: getReportForAllOrders(),
        method: 'get',
        success: function (response) {
            displayConfirmWindowForDownloadReport(response);
        }
    });

});

$('#button-get-report-for-new-employees').click(function () {
    $.ajax({
        // url: getReportOfEmployeesWithClothesQuantities(),
        url: getReportForNewEmployees(),
        method: 'get',
        success: function (response) {
            displayConfirmWindowForDownloadReport(response);
        }
    })
});


$('#button-get-report-with-employees-and-clothes-with-quantities').click(function () {
    $.ajax({
        url: getReportOfEmployeesWithClothesQuantities(),
        method: 'get',
        success: function (response) {
            displayConfirmWindowForDownloadReport(response);
        }
    })
});

function reportOrder(mainOrderId) {
    $.ajax({
        url: postReportOrder(mainOrderId),
        method: 'post',
        success: function (response) {
            if(response.succeed) {
                $('#' + mainOrderId).remove();
                ordersToReport = removeObjectFromArrayByFieldValue(
                    ordersToReport,
                    'id',
                    mainOrderId);
            }
        }
    })
}


function displayOrdersForOrdersList(orders, $table){
    // console.log(orders);
    writeDataToTable(
        sort(orders,
            'employee.box.locker.plant.plantNumber',
            'employee.box.locker.lockerNumber',
            'employee.box.boxNumber'),
        $table,
        writeOrdersWithEmployeesToTable);
}


function getOrdinalNumbers(order) {
    let ordinalNumbers = [];
    for(let o of order.clothOrders) {
        if(o.active) ordinalNumbers.push(o.clothToExchange.ordinalNumber);
    }
    return sort(ordinalNumbers);
}


