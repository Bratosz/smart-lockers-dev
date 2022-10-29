package pl.bratosz.smartlockers.service.managers.creators;


import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.service.exels.RowForBasicDataBaseUpload;

public class EmployeeCreator {
    private Client client;

    public EmployeeCreator(Client client) {
        this.client = client;
    }

    public Employee createFromRow(RowForBasicDataBaseUpload row) {
        String firstName = row.getFirstName();
        String lastName = row.getLastName();
        Department department = client.getDepartmentByName(row.getDepartmentName());
        return new Employee(firstName, lastName, department, true);

    }
}
