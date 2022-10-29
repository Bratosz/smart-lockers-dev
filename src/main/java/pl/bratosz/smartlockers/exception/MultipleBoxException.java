package pl.bratosz.smartlockers.exception;

import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.SimpleEmployee;

import java.util.LinkedList;
import java.util.List;

public class MultipleBoxException extends Exception {
    private List<SimpleEmployee> employees;

    public MultipleBoxException() {
    }

    public MultipleBoxException(List<SimpleEmployee> employees) {
        this.employees = employees;
    }

    public MultipleBoxException(SimpleEmployee employee) {
        this.employees = new LinkedList<>();
        employees.add(employee);
    }

    public List<SimpleEmployee> getEmployees() {
        return employees;
    }
}
