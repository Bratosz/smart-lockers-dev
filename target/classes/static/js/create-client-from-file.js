
$('#button-create-client').click(function () {
    let clientName = $('#input-client-name-ccff').val();
    let plantNumber = $('#input-plant-number-ccff').val();
    if(!clientNameIsValid(clientName)) {
        alert("Nazwa jest za krótka.");
    } else if(!plantNumberIsValid(plantNumber)) {
        console.log(plantNumber);
        alert("Numer zakładu musi być 3-cyfrowy.");
    } else {
        $.ajax({
            url: postCreateClient(clientName, plantNumber),
            method: 'post',
            success: function(response) {
                alert(response.message);
                loadClient();
                loadContent($('#div-content-1'), 'create-client-from-file.html', false);
            }
        });
    }
});

$('#button-add-articles').click(function () {
    loadContent($('#div-content-2'), 'articles.html', false);
});

$('#button-display-drop-box').click(function () {
    $('#div-drop-box').css('display', 'inline');
});


$('#button-download-template-for-create-client').click(function () {
    let templateType = $('#select-template-type').val();
    if(templateType == "NOT_SELECTED") {
        alert("Musisz najpierw wybrać rodzaj szablonu");
    } else {
        $.ajax({
            url: getTemplateForCreateClient(templateType),
            method: 'get',
            success: function (response) {
                if(response.succeed) {
                    displayConfirmWindowForDownloadFile(response);
                } else {
                    alert(response.message);
                }
            }
        })
    }
});

$('#drop-box').bind('dragover drop', function(event) {
    let file;
    event.stopPropagation();
    event.preventDefault();
    if (event.type == 'drop')  {
        let clientName = $('#input-client-name-ccff').val();
        let plantNumber = $('#input-plant-number-ccff').val();
        file = event.originalEvent.dataTransfer.files[0];
        var formData = new FormData();
        formData.append("file", file, file.name);
        $.ajax({
            url: getActualLocation() +
                `/file/load-new-client` +
                `/${userId}`,
            method: 'post',
            timeout: 0,
            processData: false,
            mimeType: 'multipart/form-data',
            contentType: false,
            data: formData,
            success: function (response) {
                let resp = JSON.parse(response);
                alert(resp.message);
                if(resp.succeed) {
                    let plantNumber = resp.entity.plantNumber;
                    loadClientByPlantNumber(plantNumber);
                }
            }
        });
    }
});

function clientNameIsValid(clientName) {
    if(!valueIsValid(clientName)) {
        return false;
    }
    clientName.trim();
    if(clientName.length < 3) {
        return false;
    } else {
        return true;
    }
}

function plantNumberIsValid(plantNumber) {
    if(plantNumber > 99 && plantNumber < 1000) {
        console.log(plantNumber);
        return true;
    } else {
        return false;
    }
}
