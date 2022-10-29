reloadAddingPosition();

$('#button-load-position').click(function () {
    loadedPositionId = $('#select-position').val();
    positionIsLoaded = true;
    getAndDisplayPosition(loadedPositionId);
});

$('#button-position-delete').click(function () {
    let positionId = $('#select-position').val();
    deletePositionById(positionId);
});


$('#button-create-position').click(function () {
    let positionName = $('#input-position-name').val();
    console.log("click");
    createPosition(positionName);
});

$('#button-add-trousers-with-quantity').click(function () {
    let articleId = $('#select-trousers').val();
    let quantity = $('#input-quantity-trousers').val();
    addArticleWithQuantity(articleId, quantity);
    console.log(loadedPosition);
});

$('#button-add-shirt-with-quantity').click(function () {
    let articleId = $('#select-shirt').val();
    let quantity = $('#input-quantity-shirt').val();
    addArticleWithQuantity(articleId, quantity);
    console.log(loadedPosition);
});


$('#button-add-sweatshirt-with-quantity').click(function () {
    let articleId = $('#select-sweatshirt').val();
    let quantity = $('#input-quantity-sweatshirt').val();
    addArticleWithQuantity(articleId, quantity);
    console.log(loadedPosition);
});

$('#button-add-other-article-with-quantity').click(function () {
    let articleId = $('#select-other-article').val();
    let quantity = $('#input-quantity-other-article').val();
    addArticleWithQuantity(articleId, quantity);
    console.log(loadedPosition);
});

function reloadAddingPosition() {
    getAndLoadDepartments(userId, $('#select-department'), "Oddział");
    getAndLoadPositions(userId, $('#select-position'));
    loadArticlesToSelect(userId, $('#select-trousers'), "Spodnie");
    loadArticlesToSelect(userId, $('#select-shirt'), "Koszulka");
    loadArticlesToSelect(userId, $('#select-sweatshirt'), "Bluza");
    loadArticlesToSelect(userId, $('#select-other-article'), "Inne");
}

function addArticleWithQuantity(articleId, quantity) {
    $.ajax({
        url: postArticleWithQuantityForPosition(articleId, quantity, loadedPositionId),
        method: 'post',
        success: function (response) {
            window.alert(response.message);
            displayPosition(response.entity);
        }
    })
}

$('#button-add-department').click(function () {
    let departmentId = $('#select-department').val();
    addDepartment(departmentId);
});


$('#button-add-cloth-type-with-quantity').click(function () {
    let clothType = $('#select-cloth-type option:selected').val();
    let clothQuantity = $('#input-clothes-quantity').val();
    addClothTypeWithQuantity(clothType, clothQuantity, loadedPositionId);
});

$('#button-add-article-by-number').click(function () {
    let articleNumber = $('#input-article-number').val();
    addArticle(articleNumber, loadedPositionId);
});

function createPosition(positionName) {
    $.ajax({
        url: postNewPosition(positionName),
        method: 'post',
        success: function (response) {
            console.log(response);
            window.alert(response.message);
            displayPosition(response.entity);
            getAndLoadPositions(userId, $('#select-position'));
        }
    })
}

function addClothTypeWithQuantity(clientArticleId, quantity, positionId) {
    $.ajax({
        url: postClothTypeWithQuantity(clientArticleId, quantity, positionId),
        method: 'post',
        success: function (response) {
            displayPosition(response.entity);
        }
    })
}

function addAnotherArticle(clientArticleId, articleWithQuantityId, positionId) {
    $.ajax({
        url: postAnotherArticle(clientArticleId, articleWithQuantityId, positionId),
        method: 'post',
        success: function (response) {
            displayPosition(response.entity);
        }
    })

}

function addDepartment(departmentId) {
    $.ajax({
        url: postPositionAndAddDepartment(loadedPositionId, departmentId),
        method: 'post',
        success: function (response) {
            displayPosition(response.entity);
        }
    })
}

function displayPosition(position) {
    positionIsLoaded = true;
    loadedPositionId = position.id;
    loadedPosition = position;
    console.log(position);
    removeTableRows($('#table-of-position'));
    addLabeledRowToTableOfPosition("Stanowisko", position.name);
    if (position.departments != null)
        addLabeledRowToTableOfPosition("Oddziały", displayDepartments(position.departments));
    let articlesWithQuantities = position.articlesWithQuantities;
    console.log("Artykuły");
    console.log(articlesWithQuantities);
    if(articlesWithQuantities != null) {
        for(let ato of articlesWithQuantities) {
            addStdRowToTable(
                ato,
                $('#table-of-position'),
                addRowWithArticleAndQuantityAndSelection);
        }
    }
}

function deletePositionById(positionId) {
    $.ajax({
        url: deletePosition(positionId),
        method: 'delete',
        success: function (response) {
            alert(response.message);
            if(response.entity.id == loadedPositionId) {
                removeTableRows($('#table-of-position'));
            }
        },
        complete: function () {
          getAndLoadPositions(userId, $('#select-position'));
        }
    })
}

function getArticles(articles) {
    let result = "";
    if (articles != null) {
        let article;
        for (let a of articles) {
            // console.log(a);
            article = a.article;
            result = result + article.clothType + " " + article.number + ", ";
        }
    }
    return result;
}

function getClothTypeFromArticlesWithQuantities(clientArticlesWithQuantity) {
    let clientArticles = clientArticlesWithQuantity.availableArticles;
    for(let a of clientArticles) {
        return a.article.clothType;
    }
}

function addLabeledRowToTableOfPosition(label, content) {
    addLabeledRowToTable(label, content, $('#table-of-position'), writeLabeledRow);
}

function displayDepartments(departments) {
    let result = "";
    for (department of departments) {
        result = result + department.name + ", ";
    }
    return result;
}

