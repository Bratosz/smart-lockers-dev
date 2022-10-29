loadTableForEmployeesPasting();

$(document).ready(function () {
    assignPastingBehaviourToInputs();
});

$('#button-add-10-rows').click(function () {
   extendTableByNumberOfRows($('#table-for-employees-paste-edpl'), 10);
});


$('#button-add-employees-edpl').click(function () {
   let employees = getEmployeesFromTableEDPL();
   console.log(employees);
   $.ajax({
       url: postAddPastedEmployeesEDPL(),
       method: 'post',
       contentType: 'application/json',
       data: JSON.stringify(employees),
       success: function (response) {
           alert("Dodano pracowników");
           if(response.succeed) {
               if(confirm("Czy chcesz ich przypisać?")) {
                   loadContent($('#div-content-1'),'employees-creating.html');
               }
           }


       }
   })
});

function getEmployeesFromTableEDPL() {
    let employeeName, department, position, location, employees = [];

    function rowIsFilled(row) {
        employeeName = row.find('.input-employee-name').val();
        console.log(employeeName);
        department = row.find('.input-department').val();
        position = row.find('.input-position').val();
        location = row.find('.input-location').val();
        if(!empty(employeeName) || !empty(department) || !empty(position) || !empty(location)) {
            return true;
        } else {
            return false;
        }
    }

    $('.edpl').each(function () {
        let $this = $(this), employee;
        if(rowIsFilled($this)) {
            employee = {
                employeeName: employeeName,
                department: department,
                position: position,
                location: location
            };
            employees.push(employee);
        }
    });
    return employees;
}


function assignPastingBehaviourToInputs() {
    $('input').on('paste', function (e) {
        let $this = $(this);
        $.each(e.originalEvent.clipboardData.items, function (index, value) {
            if (value.type === 'text/plain') {
                value.getAsString(function (text) {
                    let x = $this.closest('td').index();
                    let y = $this.closest('tr').index() + 1;
                    text = text.trim('\r\n');
                    console.log(text.split('\n'));
                    $.each(text.split('\n'), function (i2, v2) {
                        $.each(v2.split('\t'), function (i3, v3) {
                            // let cellNameValue = v3.trim().toUpperCase();
                            // if (isItHeaderRow(cellNameValue)) {
                            //     y -= 1;
                            //     return false;
                            // }
                            let rowIndex = y + i2;
                            let $table = $('#table-for-employees-paste-edpl');
                            if(tableIsFilled($table, rowIndex)) {
                                extendTable($table);
                            }
                            let col = x + i3;
                            $this.closest('table').find('tr:eq(' + rowIndex + ') td:eq(' + col + ') input').val(v3);
                        });
                    });
                    assignPastingBehaviourToInputs();
                });
            }
        });
    });
}

function tableIsFilled($table, rowIndex) {
    let rowsInTable = $table.find('tbody > tr').length;
    if(rowsInTable == (rowIndex)) {
        return true;
    } else {
        return false;
    }
}

function loadTableForEmployeesPasting() {
    let $table = ($('#table-for-employees-paste-edpl'));
    removeTableRows($table);
    for(let i = 0; i < 10; i++) {
        extendTable($table);
    }
}

function extendTableByNumberOfRows($table, amount) {
    for(let i = 0; i < amount; i++) {
        extendTable($table);
    }
    assignPastingBehaviourToInputs();
}

function extendTable($table) {
    addEmptyRowToTable($table);
}

