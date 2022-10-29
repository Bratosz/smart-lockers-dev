package pl.bratosz.smartlockers.service.exels.plant.template;

import pl.bratosz.smartlockers.exception.MyException;
import pl.bratosz.smartlockers.service.exels.plant.template.data.TemplateEmployee;
import pl.bratosz.smartlockers.service.exels.plant.template.data.TemplateLockers;

import java.util.*;

public class LockersCapacityValidator {
    private final Set<TemplateLockers> lockers;
    private final Set<TemplateEmployee> employees;
    private final Set<String> locations;
    private Map<String, Integer> lockersCapacityPerLocation;
    private Map<String, Integer> employeesPerLocation;


    public LockersCapacityValidator(
            Set<TemplateLockers> lockers,
            Set<TemplateEmployee> employees) {
        this.lockers = lockers;
        this.employees = employees;
        locations = getLocationsFromLockers();
        lockersCapacityPerLocation = new HashMap<>();
        employeesPerLocation = new HashMap<>();
    }

    public void validate() throws MyException {
        countEmployeesPerLocation();
        countLockersCapacityPerLocation();
        compare();
    }

    private void compare() throws MyException {
        int lockersCapacity;
        int employeesAmount;
        for(String location : locations) {
            lockersCapacity = lockersCapacityPerLocation.get(location);
            employeesAmount = employeesPerLocation.get(location);
            if(employeesAmount > lockersCapacity) {
                throw new MyException(
                        "Zbyt mała pojemność szaf w lokalizacji: " + location
                        + ".\n Pojemność szaf: " + lockersCapacity
                        + "\n Ilość pracowników: " + employeesAmount);
            }
        }
    }

    private void countLockersCapacityPerLocation() {
        for(TemplateLockers l : lockers) {
            count(l);
        }
    }

    private void countEmployeesPerLocation() throws MyException {
        for (TemplateEmployee emp : employees) {
            checkLocation(emp);
            count(emp);
        }
    }

    private void count(TemplateLockers l) {
        String location = l.getLocation();
        if(lockersCapacityPerLocation.containsKey(location)) {
            int actualCapacity = lockersCapacityPerLocation.get(location);
            lockersCapacityPerLocation.replace(
                    location,
                    actualCapacity + computeCapacity(l));
        } else {
            lockersCapacityPerLocation.put(location, computeCapacity(l));
        }
    }
    private void count(TemplateEmployee emp) {
        String location = emp.getLocation();
        if (employeesPerLocation.containsKey(location)) {
            Integer employeesAmount = employeesPerLocation.get(location);
            employeesPerLocation.replace(
                    location,
                    ++employeesAmount);
        } else {
            employeesPerLocation.put(location, 1);
        }
    }

    private int computeCapacity(TemplateLockers l) {
        int numberOfLockers =
                (l.getLastLockerNumber() - l.getFirstLockerNumber()) + 1;
        return l.getCapacity() * numberOfLockers;
    }


    private void checkLocation(TemplateEmployee employee) throws MyException {
        if (!locations.contains(employee.getLocation())) {
            throw new MyException("Pracownik " + employee.toString() +
                    " ma przypisaną lokalizację: " + employee.getLocation() +
                    " ale nie ma żadnej szafy o takiej lokalizacji.");
        }
    }

    private Set<String> getLocationsFromLockers() {
        HashSet<String> locations = new HashSet<>();
        lockers.stream()
                .forEach(l -> locations.add(l.getLocation()));
        return locations;
    }
}
