getAndLoadDepartments(userId, $('#select-department'), "", loadPositionsByDepartment);
loadBoxInfo(boxIdForNewEmployee);

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

$('#button-add-employee-to-box').click(function () {
    const departmentId = $('#select-department').val();
    const firstName = $('#input-first-name').val();
    const lastName = $('#input-last-name').val();
    let positionId = $('#select-position').val();
    $.ajax({
        url: postNewEmployee(boxIdForNewEmployee, lastName, firstName, departmentId, positionId),
        method: 'post',
        success: function (response) {
            alert(response.message);
            employeeId = response.entity.id;
            $('#button-assign-clothes').css('display', 'initial');
        }
    })
});


function loadBoxInfo(boxId) {
    if (boxId != undefined && boxId > 0) {
        $.ajax({
            url: getActualLocation() + `/box` +
                `/${boxId}`,
            method: 'get',
            success: function (box) {
                writeBoxInfoToElement(box, $('#box-info'));
            }
        });
    }
}