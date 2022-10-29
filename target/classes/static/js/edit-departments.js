getAndLoadDepartments(userId);
loadDepartmentsWithAliases(userId);

$("#button-add-alias").click(function () {
    let departmentId = $("#select-department").val();
    let alias = $("#input-department-alias").val();
    addAlias(departmentId, alias);
});

function loadDepartmentsWithAliases(userId) {
    let $table = $('#table-of-departments-with-aliases');

    $.ajax({
        url: getDepartments(userId),
        method: 'get',
        success: function (departments) {
            clearAndHideTable($table);
            if(collectionIsViable(departments)) {
                exposeTable($table);
                departments = sort(
                    departments,
                    'mainPlantNumber',
                    'name');
                writeDataToTable(departments, $table, writeDepartmentsWithAliasesToRow);
            }
        }
    })
}

function writeDepartmentWithAliasesToRow(department, $row) {
    $row.find('.cell-department-name').text(department.name);

    let aliases = department.aliases;
    if(aliases !== null) {
        aliases = sort(aliases);
        for(let a of aliases) {
            let $element = $row.find('.cell-department-aliases')
                .find('#li-template').clone();
            $element.removeAttr('id');
            $element.css('display', 'list-item');
            $element.text(a);
            $row.find('.licst').append($element);
        }
    }
    return $row;
}

function addAlias(departmentId, alias) {
    $.ajax({
        url: `http://localhost:8080/department/add_alias/${departmentId}/${alias}`,
        method: "post",
        success: function (department) {
            displayDepartment(department)
        }
    });
}