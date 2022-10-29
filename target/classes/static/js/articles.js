loadArticlesPage();

$('#button-add-client-article').click(function () {
    let articleId = $('#select-article').val();
    let badgeNumber = $('#select-badge-number').val();
    if(articleId != 0) {
        addClientArticle(articleId, badgeNumber, userId);
    } else {
        alert("Najpierw wybierz artykuł");
    }
});

$('#button-create-article-and-add-client-article').click(function () {
    let articleNumber = $('#input-article-number').val();
    let articleName = $('#input-article-name').val();
    let badgeNumber = $('#select-badge-number-for-new-client-article').val();
    if(!articleNumberIsValid(articleNumber)) {
        alert("Numer musi być czterocyfrowy");
    } else if (!articleNameIsValid(articleName)) {
        alert("Nazwa jest za krótka.");
    } else {
        $.ajax({
            url: postCreateArticleAndAddClientArticle(
                articleNumber,
                articleName.trim(),
                badgeNumber,
                userId),
            method: 'post',
            success: function (response) {
                if(response.succeed) {
                    loadArticlesPage();
                }
                alert(response.message);
            }
        })
    }
});

$('#button-set-depreciation-period').click(function () {
    let period = $('#select-depreciation-period').val();
    $.ajax({
        url: putClientArticlesDepreciationPeriod(period),
        method: 'post',
        success: function (clientArticles) {
            writeArticlesToTable(clientArticles);
        }
    })
});

$('#button-set-depreciation-cap').click(function () {
    let percentageCap = $('input[name="percentage-cap"]:checked').val();
    $.ajax({
        url: putClientArticlesPercentageCap(percentageCap),
        method: 'put',
        success: function(clientArticles) {
            // console.log(clientArticles);
            writeArticlesToTable(clientArticles);
        }
    })
});

function loadArticlesPage() {
    loadAvailableArticlesToSelect();
    loadClientArticlesToTable();
}

function loadAvailableArticlesToSelect() {
    $.ajax({
        url: getAllArticles(),
        method: 'get',
        success: function (articles) {
            let $select = $('#select-article');
            removeOptionsFromSelect($select);
            if(articles.length > 0) {
                appendOptionsToSelect(extractArticleNumberNameAndIdFromArticles(articles), $select);
            } else {
                appendOptionToSelect(($select), {id: 0, name: "Brak artykułów"});
            }
        }
    })
}

function loadClientArticlesToTable() {
    hide($('#div-articles-parameters'));
    hide($('#table-of-articles'));
    $.ajax({
        url: getAllClientArticles(),
        method: "get",
        success: function (clientArticles) {
            if(clientArticles.length > 0) {
                show($('#div-articles-parameters'));
                writeArticlesToTable(clientArticles);
            }
        }
    })
}

function addClientArticle(articleId, badgeNumber, userId) {
    $.ajax({
        url: postAddClientArticle(articleId, badgeNumber, userId),
        method: 'post',
        success: function (response) {
            if(response.succeed) {
                loadArticlesPage();
            }
            alert(response.message);
        }
    })
}


// function loadBadges() {
//     $.ajax({
//         url: getBadges(),
//         method: 'get',
//         success: function (response) {
//             console.log("badges");
//             console.log(response.entity);
//             badges = response.entity;
//         }
//     })
// }

function writeArticlesToTable(clientArticles) {
    writeDataToTable(
        sort(clientArticles,
            'article.clothType',
            'article.number'),
        $('#table-of-articles'),
        writeArticleToRow);
}

function writeArticleToRow(clientArticle, $row) {
    let article = clientArticle.article;
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-id').text(clientArticle.id);
    $row.find('.cell-article-number').text(article.number);
    $row.find('.cell-article-name').text(article.name);
    $row.find('.cell-cloth-type').text(article.clothType);
    $row.find('.cell-badge-number').text(clientArticle.badge.number);
    $row.find('.cell-depreciation-period').text(clientArticle.depreciationPeriod);
    $row.find('.cell-depreciation-cap').text(clientArticle.depreciationPercentageCap + "%");
    $row.find('.cell-article-redemption-price').text(clientArticle.redemptionPrice);
    $row.find('.button-save').click(function () {
        let price = $row.find('.input-new-redemption-price').val();
        $.ajax({
                url: putClientArticlePrice(price, clientArticle.id),
                method: "put",
                success: function (actualArticle) {
                    refreshRow(actualArticle, $row, writeArticleToRow);
                }
            },
        )
    });
    return $row;
}

function articleNumberIsValid(articleNumber) {
    if(articleNumber > 999 && articleNumber < 10000) {
        return true;
    } else {
        return false;
    }
}

function articleNameIsValid(name) {
    if(!valueIsValid(name)) {
        return false;
    }
    name.trim();
    if(name.length < 5) {
        return false;
    } else {
        return true;
    }
}