package pl.bratosz.smartlockers.comparators.employee;

import pl.bratosz.smartlockers.model.Employee;

import java.util.Comparator;

public class BoxNumberSorter implements Comparator<Employee> {
    @Override
    public int compare(Employee o1, Employee o2) {
        return o1.getBox().getBoxNumber() - o2.getBox().getBoxNumber();
    }
}
