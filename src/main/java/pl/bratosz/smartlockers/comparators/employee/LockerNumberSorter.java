package pl.bratosz.smartlockers.comparators.employee;

import pl.bratosz.smartlockers.model.Employee;

import java.util.Comparator;

public class LockerNumberSorter implements Comparator<Employee> {
    @Override
    public int compare(Employee o1, Employee o2) {
        return o1.getBox().getLocker().getLockerNumber() - o2.getBox().getLocker().getLockerNumber();
    }
}
