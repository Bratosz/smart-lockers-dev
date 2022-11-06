
function checkedClothesHaveTheSameArticleNumber($tableBody) {
    let articleNumbers = [];
    $tableBody.find('input[type="checkbox"]:checked')
        .each(function () {
            let articleNumber = parseInt($(this).closest('tr')
                .find('.cell-article-number').text());
            articleNumbers.push(articleNumber);
        });
    return allElementsAreEqual(articleNumbers);
}

function validateSelectedClothes(action) {
    let $tableBody = $('#table-of-clothes-body');
    if(checkedClothesHaveTheSameArticleNumber($tableBody)) {
        action();
    } else {
        alert("Wszystkie zaznaczone ubrania muszą mieć ten sam NUMER ARTYKUŁU");
    }
}