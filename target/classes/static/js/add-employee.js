
getAndLoadDepartments(userId, $('#select-department'), "", loadPositionsByDepartment);
getAndLoadLocations(userId, $('#select-location'));


$('#input-last-name').focus(function() {
    $('#button-assign-clothes').css('display', 'none')
    }
);

$('#input-first-name').focus(function() {
        $('#button-assign-clothes').css('display', 'none')
    }
);

$('#select-department').change(function () {
    loadPositionsByDepartment($('#select-department'), $('#select-position'));
});

$('#button-assign-clothes').click(function () {
    loadContent($('#div-content-1'),'add-clothes.html', false);
});

$('#button-add-employee-to-next-free-box').click(function () {
    let lastName = $('#input-last-name').val();
    let firstName = getValueFromInputText($('#input-first-name'));
    let departmentId = $('#select-department').val();
    let locationId = $('#select-location').val();
    let positionId = $('#select-position').val();
    $.ajax({
        url: postNewEmployeeToNextFreeBox(
            lastName,
            firstName,
            departmentId,
            locationId,
            positionId),
        method: 'post',
        success: function (response) {
            window.alert(response.message);
            if (response.succeed) {
                employeeId = response.entity.id;
                $('#button-assign-clothes').css('display', 'initial');
            }
        }
    })
});

