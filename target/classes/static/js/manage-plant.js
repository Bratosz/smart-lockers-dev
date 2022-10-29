loadPlants();

$('#button-generate-lear').click(function () {
    if (confirm("Czy na pewno?")) {
        $.ajax({
            url: getActualLocation() + `/client/generate`,
            method: 'post',
            success: function () {
                loadPlants();
                alert("Utworzono klienta");
                window.location.href = `../index.html`;
            }
        });
    }
});

$('#button-update-clothes').click(function () {
    let plantId = $('#select-plant').val();
    if (confirm("To może chwilę potrwać, czy na pewno?")) {
        $.ajax({
            url: postUpdateClothes(plantId),
            method: 'post',
            beforeSend: function () {
                coverUp();
            },
            complete: function () {
                reveal();
            },
            success: function (response) {
                alert("Zaktualizowano ubrania.");
            }
        });
    }
})

$('#button-load-plant-without-clothes').click(function () {
    let plantId = $('#select-plant').val();
    if (confirm("To może chwilę potrwać, czy na pewno?")) {
        $.ajax({
            url: postLoadPlantWithoutClothes(plantId),
            method: 'post',
            beforeSend: function () {
                coverUp();
            },
            complete: function () {
                reveal();
            },
            success: function () {
                alert("Wczytano szafki i pracowników");
            }
        });
    }
});

$('#button-load-all-clothes').click(function () {
    let plantId = $('#select-plant').val();
    if(confirm("To może zająć kilka minut")) {
        $.ajax({
            url: postLoadAllClothes(plantId),
            method: 'post',
            beforeSend: function () {
                coverUp();
            },
            complete: function () {
                reveal();
            },
            success: function () {
              alert("Wczytano ubrania")
            }
        })
    }
});
