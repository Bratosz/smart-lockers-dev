package pl.bratosz.smartlockers.validators;

import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Employee;

public class EmployeeValidator {

    public static boolean isEmployeeCorrect(Employee employee) {
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();

        if (!StringValidator.isStringContainLettersAndSpacesOnly(firstName)) return false;
        if (!StringValidator.isStringContainLettersDashesAndSpacesOnly(lastName)) return false;

        return true;
    }
}
