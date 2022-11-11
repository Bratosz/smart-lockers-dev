loadPlants(userId, $('#select-plant'));
getAndLoadDepartments(userId, $('#select-department'));
getAndLoadLocations(userId, $('#select-location'));
loadPlants(userId, $('#select-plant-for-assign-by-numbers'));
loadEmployeeInfo();
loadMask();


//PRZENOSZENIE PRACOWNIKA DO INNEJ SZAFKI NA BAZIE PARAMETRÓW I PO NUMERZE SZAFKI



function loadEmployeeInfo() {
    $.ajax({
        url: getEmployee(employeeId),
        method: 'get',
        success: function(employee) {
            writeEmployeeInfoToElement(
                employee,
                $('#info-employee'));
        }
    });
}

$('#button-change-employee-name').click(function () {
   let lastName = getValueFromInputTextOrDashIfEmpty($('#input-last-name'));
   let firstName = getValueFromInputTextOrDashIfEmpty($('#input-first-name'));
   $.ajax({
       url: getActualLocation() +
           `/employee/change-name` +
           `/${employeeId}` +
           `/${lastName}` +
           `/${firstName}`,
       method: 'post',
       success: function(response) {
           writeEmployeeInfoToElement(
               response.entity,
               $('#info-employee'));
       }
   })
});

$('#button-relocate-employee').click(function() {
    let plantId = $('#select-plant').val(),
        departmentId = $('#select-department').val(),
        locationId = $('#select-location').val();
    $.ajax({
        url: getActualLocation() +
            `/employee/relocate` +
            `/${plantId}` +
            `/${departmentId}` +
            `/${locationId}` +
            `/${employeeId}`,
        method: 'post',
        success: function(response) {
            window.alert(response.message);
            if(response.succeed) {
                goBack();
            }
        }
    })
});

function loadMask() {
    console.log("test input mask");
    // $('#input-box-number').inputmask({
    //     mask: "9{1,3}/9{1,3}",
    //     greedy: false,
    //     oncomplete: function () {
    //         console.log("complete");
    //     }
    // });
    // // $('#input-box-number').mask('0-00/000');
    $('#input-box-number').inputmask("9{1,4}/9{1,4}");
}






































