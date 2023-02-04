function extractActiveOrders(orders) {
    let activeOrders = [];
    for (let order of orders) {
        if (order.active) {
            activeOrders.push(order)
        }
    }
    return activeOrders;
}

function sortOrdersByCreationDate(orders) {
    orders.sort(function (a, b) {
        return a.created - b.created;
    });
    return orders;
}

function getActualOrderStatus(order) {
    return getLastFromArray(order.orderStatusHistory);
}

function getClothToExchangeStatus(clothToExchange) {
    return getLastFromArray(clothToExchange.statusHistory);
}

function writeClothOrderToRow(clothOrder, $row) {
    let clothToExchange = clothOrder.clothToExchange;
    let article = clothToExchange.clientArticle.article;

    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-ordinal-number').text(clothToExchange.ordinalNumber);
    $row.find('.cell-article-number').text(article.number);
    $row.find('.cell-article-name').text(article.name);
    $row.find('.cell-size').text(
        writeSize(clothToExchange));
    if (clothToExchange.statusHistory.length > 0) {
        let clothToExchangeStatus = getClothToExchangeStatus(clothToExchange);
        $row.find('.cell-actual-status').text(clothToExchangeStatus.status.polishName);
        $row.find('.cell-status-update').text(formatDateDMY(
            clothToExchangeStatus.dateOfUpdate));
    }
    $row.find('.cell-barcode').text(clothToExchange.barcode);
    return $row;

}

function writeOrderToRow(order, $row) {
    let actualOrderStatus = getActualOrderStatus(order);
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-main-order-id').text(order.id);
    console.log(order.id);
    $row.find('.cell-main-order-type').text(order.orderType);
    $row.find('.cell-main-order-status').text(actualOrderStatus.orderStage);
    $row.find('.cell-clothes-amount').text(order.clothOrders.length);
    $row.find('.cell-status-changed-date').text(formatDateDMY(
        actualOrderStatus.dateOfUpdate));
    if (!order.reported && order.orderType == "Nowy artykuł") {
        let clientArticle = order.desiredClientArticle;
        let $select = $row.find('.select-article-in-orders-table');
        $select.css('display', 'table-row');
        $select.append(createOption(
            clientArticle.id,
            clientArticle.article.name));
        appendArticlesWithoutActual($select, clientArticle);
    } else {
        $row.find('.cell-desired-article').text(
            toStringArticle(order.desiredClientArticle));
    }
    $row.find('.input-size').val(order.desiredSize);
    if (order.lengthModification != "NONE") {
        $row.find('.input-length-modification').val(order.lengthModification);
    }
    return $row;
}

function writeOrdersToTable($table, $tbody, orders) {
    $tbody.find('tr').remove();
    orders = sortOrdersByCreationDate(orders);
    const $mainOrderRow = $table.find('#row-template-main-order');
    const $collapsableClothOrdersRow = $table.find('#row-template-collapsable-cloth-orders');
    const dataTarget = 'target';
    let i = 0;
    for (let order of orders) {
        console.log("to jest order");
        console.log(order);

        let $row = $mainOrderRow.clone();
        let $collapsableRow = $collapsableClothOrdersRow.clone();
        $collapsableRow.css('display', 'table-row');
        let collapsableDivId = dataTarget + i;

        $collapsableRow.removeAttr('id');
        $row = writeOrderToRow(order, $row);
        $tbody.append($row);
        let $button = $row.find('.view-button');
        $button.attr('data-target', "#" + collapsableDivId);
        let $div = $collapsableRow.find('#div-collapsable');
        $div.attr('id', collapsableDivId);
        writeClothOrdersToTable(
            $collapsableRow.find('#table-of-cloth-orders-body'),
            $collapsableRow.find('#row-template-cloth-order'),
            order.clothOrders
        );
        $tbody.append($collapsableRow);
        i++;
    }
    return $tbody;

    function writeClothOrdersToTable(
        $tbody,
        $rowTemplate,
        clothOrders) {
        // $tbody = $tbody.find('tr').remove();
        clothOrders = sort(clothOrders, 'clothToExchange.ordinalNumber');
        for (let order of clothOrders) {
            console.log("to jest cloth order");
            console.log(order);
            let $row = $rowTemplate.clone();
            $row = writeClothOrderToRow(order, $row);
            $tbody.append($row);
        }
    }

    return $tbody;
}