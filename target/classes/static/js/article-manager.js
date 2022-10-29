let clientArticlesArray = [];
let loadedArticlesClientId = 0;

function loadArticlesToSelect(userId, $select, clothType) {
    if (clientArticlesArray.length == 0 || loadedArticlesClientId != loadedClientId) {
        $.ajax({
            url: getClientArticles(userId),
            method: 'get',
            success: function (clientArticles) {
                console.log(clientArticles);
                loadedArticlesClientId = loadedClientId;
                clientArticlesArray = sort(clientArticles, 'article.number');
                clientArticles = getArticlesByClothType(clientArticlesArray, clothType);
                appendOptionsToSelect(extractArticleNumberNameAndIdFromClientArticles(clientArticles), $select);
            }
        });
    } else {
        let clientArticles = getArticlesByClothType(clientArticlesArray, clothType);
        appendOptionsToSelect(extractArticleNumberNameAndIdFromClientArticles(clientArticles), $select);
    }
}

function displayAvailableArticlesByPosition($select) {
    let clientArticles = getClientArticlesFromEmployeePosition();
    removeOptionsFromSelect($select);
    for (let a of clientArticles) {
        console.log(a);
        $select.append(createOption(
            a.id,
            a.article.number + " " + a.article.name));
    }
}

function appendArticlesWithoutActual($select, actualClientArticle) {
    let clientArticles = getClientArticlesFromEmployeePosition(),
        actualArticle = actualClientArticle.article,
        clothType = actualArticle.clothType;
    for(let a of clientArticles) {
        if(a.article.number != actualArticle.number
            && a.article.clothType == clothType) {
            $select.append(createOption(
                a.id,
                a.article.number + " " + a.article.name));
        }
    }
}

function getClientArticlesFromEmployeePosition() {
    let articlesWithQuantities = loadedEmployee.position.articlesWithQuantities;
    let availableArticles = [];
    let clientArticles = [];
    for (let awq of articlesWithQuantities) {
        availableArticles = awq.availableArticles;
        for (let a of availableArticles) {
            clientArticles.push(a);
        }
    }
    clientArticles = sort(clientArticles, 'article.number');
    return clientArticles;
}

function getArticlesByClothType(clientArticles, clothType) {
    let articlesByClothType = [];
    if (clothType != "Inne") {
        for (let a of clientArticles) {
            if (a.article.clothType == clothType) {
                articlesByClothType.push(a);
            }
        }
        return articlesByClothType;
    } else {
        for (let a of clientArticles) {
            clothType = a.article.clothType;
            if (clothType != "Spodnie" &&
                clothType != "Koszulka" &&
                clothType != "Bluza") {
                articlesByClothType.push(a);
            }
        }
        return articlesByClothType;
    }
}

function extractArticleNumberNameAndIdFromClientArticles(clientArticles) {
    let articlesForSelect = [];
    for (let a of clientArticles) {
        let article = {
            id: a.id,
            name: a.article.number + " " + a.article.name
        };
        articlesForSelect.push(article);
    }
    return articlesForSelect;
}

function extractArticleNumberNameAndIdFromArticles(articles) {
    let extractedArticles = [];
    for (let a of articles) {
        let article = {
            id: a.id,
            name: a.number + " " + a.name
        };
        extractedArticles.push(article);
    }
    return extractedArticles;
}