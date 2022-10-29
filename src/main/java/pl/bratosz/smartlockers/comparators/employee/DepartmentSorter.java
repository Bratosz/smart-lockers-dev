package pl.bratosz.smartlockers.comparators.employee;

import pl.bratosz.smartlockers.model.Employee;

import java.util.Comparator;

public class DepartmentSorter implements Comparator<Employee> {
    @Override
    public int compare(Employee o1, Employee o2) {
        return o1.getDepartment().getName().compareTo(o2.getDepartment().getName());
    }
}
