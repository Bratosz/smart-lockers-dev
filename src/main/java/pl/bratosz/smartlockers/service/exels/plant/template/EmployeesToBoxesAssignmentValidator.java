package pl.bratosz.smartlockers.service.exels.plant.template;

import pl.bratosz.smartlockers.exception.MyException;
import pl.bratosz.smartlockers.service.exels.plant.template.data.TemplateEmployee;
import pl.bratosz.smartlockers.service.exels.plant.template.data.TemplateLockers;

import java.util.*;
import java.util.stream.Collectors;

public class EmployeesToBoxesAssignmentValidator {
    private final Set<TemplateEmployee> employees;
    private final Set<TemplateLockers> templateLockers;


    public EmployeesToBoxesAssignmentValidator(Set<TemplateEmployee> employees, Set<TemplateLockers> lockers) {
        this.employees = employees;
        this.templateLockers = lockers;
    }

    public void validate() throws MyException {
        checkForDuplicatedBoxes();
        checkThatEmployeesFitInLockers();
    }

    private void checkThatEmployeesFitInLockers() throws MyException {
        Map<Integer, List<Integer>> lockers = createLockers();
        List<Employee> employeesWithMissingBoxes = employees.stream()
                .filter(e -> boxIsMissing(e, lockers))
                .map(e -> new Employee(e))
                .collect(Collectors.toList());
        if(!employeesWithMissingBoxes.isEmpty()) {
            throw new MyException("Dla tych pracowników, brakuje boxów w arkuszu SZAFY: " +
                    employeesWithMissingBoxes.toString());
        }
    }

    private boolean boxIsMissing(TemplateEmployee e, Map<Integer, List<Integer>> lockers) {
        int lockerNumber = e.getLockerNumber();
        int boxNumber = e.getBoxNumber();
        List<Integer> integers = lockers.get(lockerNumber);
        if(integers == null) {
            return true;
        } else {
            return !integers.contains(boxNumber);
        }
    }

    private Map<Integer, List<Integer>> createLockers() {
        Map<Integer, List<Integer>> lockersWithBoxes = new HashMap<>();
        for(TemplateLockers l : templateLockers) {
            lockersWithBoxes.putAll(createLockersWithBoxes(l));
        }
        return lockersWithBoxes;
    }

    private Map<Integer, List<Integer>> createLockersWithBoxes(TemplateLockers l) {
        Map<Integer, List<Integer>> lockersWithBoxes = new HashMap<>();
        for(int i = l.getFirstLockerNumber(); i <= l.getLastLockerNumber(); i++) {
            List<Integer> boxes = createBoxes(l.getCapacity());
            lockersWithBoxes.put(i, boxes);
        }
        return lockersWithBoxes;
    }

    private List<Integer> createBoxes(int capacity) {
        List<Integer> boxes = new ArrayList<>();
        for(int i = 1; i <= capacity; i++) {
            boxes.add(i);
        }
        return boxes;
    }

    private void checkForDuplicatedBoxes() throws MyException {
        List<Employee> duplicatedBoxes = employees.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getLockerNumber() + "/" + e.getBoxNumber(),
                        Collectors.toList()))
                .values()
                .stream()
                .filter(i -> i.size() > 1)
                .flatMap(j -> j.stream())
                .map(k -> new Employee(k))
                .collect(Collectors.toList());
        if(!duplicatedBoxes.isEmpty()) {
            throw new MyException("W arkuszu Pracownicy znajdują się zdublowane boxy: " +
                    duplicatedBoxes.toString());
        }

    }

    private class Employee {
        String firstName;
        String lastName;
        int boxNumber;
        int lockerNumber;

        public Employee(TemplateEmployee e) {
            this.firstName = e.getFirstName();
            this.lastName = e.getLastName();
            this.lockerNumber = e.getLockerNumber();
            this.boxNumber = e.getBoxNumber();
        }

        @Override
        public String toString() {
            return lockerNumber + "/" + boxNumber + " " + lastName + " " + firstName;
        }
    }
}
