
function extractActiveOrders(orders) {
    let activeOrders = [];
    for(let order of orders) {
        if(order.active) {
            activeOrders.push(order)
        }
    }
    return activeOrders;
}

function sortOrdersByCreationDate(orders) {
    orders.sort(function(a,b) {
        return a.created - b.created;
    });
    return orders;
}

function getActualOrderStatus(order) {
    let actualOrderIndex = order.orderStatusHistory.length - 1;
    return order.orderStatusHistory[actualOrderIndex];
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
    if(!order.reported && order.orderType == "Nowy artykuł") {
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
    if(order.lengthModification != "NONE") {
        $row.find('.input-length-modification').val(order.lengthModification);
    }
    return $row;

}

function writeOrdersToTable($table, orders) {
    $table = removeTableRows($table);
    orders = sortOrdersByCreationDate(orders);
    const $rowTemplate = getRowTemplate($table);
    for(let order of orders) {
        let $row = $rowTemplate.clone();
        $row = writeOrderToRow(order, $row);
        $table.append($row);
    }
    return $table;
}