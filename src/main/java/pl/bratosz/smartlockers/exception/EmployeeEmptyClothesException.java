package pl.bratosz.smartlockers.exception;

import pl.bratosz.smartlockers.model.SimpleEmployee;

public class EmployeeEmptyClothesException extends Exception {
    public SimpleEmployee employee;

    public EmployeeEmptyClothesException() {
    }

    public EmployeeEmptyClothesException(String message, SimpleEmployee employee) {
        super(message);
        this.employee = employee;
    }

    public SimpleEmployee getEmployee() {
        return employee;
    }
}
