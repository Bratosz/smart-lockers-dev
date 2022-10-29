package pl.bratosz.smartlockers.exception;

import pl.bratosz.smartlockers.model.SimpleEmployee;

public class SkippedEmployeeException extends Exception {
    public SimpleEmployee employee;

    public SkippedEmployeeException() {
    }

    public SkippedEmployeeException(SimpleEmployee employee) {
        this.employee = employee;
    }

    public SimpleEmployee getEmployee() {
        return employee;
    }
}
