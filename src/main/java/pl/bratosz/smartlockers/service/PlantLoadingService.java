package pl.bratosz.smartlockers.service;

import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.EmptyElementException;
import pl.bratosz.smartlockers.exception.MultipleBoxException;
import pl.bratosz.smartlockers.exception.SkippedEmployeeException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.response.ClothesLoadedResponse;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.update.ClothesAndEmployeesToUpdate;
import pl.bratosz.smartlockers.service.update.ScrapingService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class PlantLoadingService {
    private PlantService plantService;
    private LockerService lockerService;
    private BoxService boxService;
    private EmployeeService employeeService;
    private ClothService clothService;
    private ScrapingService scrapingService;
    private UserService userService;
    private RotationalClothService rotationalClothService;

    public PlantLoadingService(PlantService plantService,
                               LockerService lockerService,
                               BoxService boxService,
                               EmployeeService employeeService,
                               ClothService clothService, ScrapingService scrapingService, UserService userService, RotationalClothService rotationalClothService) {
        this.plantService = plantService;
        this.lockerService = lockerService;
        this.boxService = boxService;
        this.employeeService = employeeService;
        this.clothService = clothService;
        this.scrapingService = scrapingService;
        this.userService = userService;
        this.rotationalClothService = rotationalClothService;
    }

    public StandardResponse loadLockersWithBoxesAndEmployeesFromLockersRange(
            int from,
            int to,
            long plantId) {
        Plant plant = plantService.getById(plantId);
        scrapingService.connectToBoxesView(plant);
        List<Element> boxesRows = scrapingService.getBoxesRowsFromLockersRange(
                from,
                to);
        return loadLockersWithBoxesAndEmployees(boxesRows, plant);
    }

    public StandardResponse loadLockersWithBoxesAndEmployees(
            long plantId) {
        Plant plant = plantService.getById(plantId);
        scrapingService.connectToBoxesView(plant);
        List<Element> boxesRows = scrapingService.getSortedBoxesRows();
        return loadLockersWithBoxesAndEmployees(boxesRows, plant);
    }


    private StandardResponse loadLockersWithBoxesAndEmployees(
            List<Element> boxesRows,
            Plant plant) {
        Client client = plant.getClient();
        long clientId = client.getId();
        Location location = client.getLocations()
                .stream().filter(Location::isSurrogate).findFirst().get();
        Department department = client.getDepartments()
                .stream().filter(Department::isSurrogate).findFirst().get();
        int numberOfLockers = createEmployeesBoxesAndLockers(
                boxesRows,
                plant,
                department,
                location,
                clientId);
        return StandardResponse.createForSucceed("Utworzono " + numberOfLockers + " szaf.", numberOfLockers);
    }


    public ClothesLoadedResponse loadAllClothes(long plantId) {
        List<Locker> lockers = lockerService.getAllBy(plantId);
        Plant plant = plantService.getById(plantId);
        plantService.setLastUpdate(plant);
        return loadClothes(lockers, plant);
    }


    public ClothesLoadedResponse loadClothesFromLockersRange(int from, int to, long plantId) {
        List<Locker> lockers = lockerService.getLockers(from, to, plantId);
        Plant plant = plantService.getById(plantId);
        return loadClothes(lockers, plant);
    }

    public ClothesLoadedResponse loadRotationalClothes(
            int lockerNumber,
            int startingBoxNumber,
            int endBoxNumber,
            long plantId,
            long userId) {
        SimpleEmployee simpleEmployee = new SimpleEmployee();
        Set<SimpleEmployee> doubledBoxes = new HashSet<>();
        List<SimpleEmployee> rotationalEmployeesWithoutClothes = new LinkedList<>();
        Plant plant = plantService.getById(plantId);
        Client client = plant.getClient();
        User user = userService.getUserById(userId);
        Set<Box> boxes = boxService
                .getBoxesFromRange(lockerNumber, startingBoxNumber, endBoxNumber, plant);
        scrapingService.connectToBoxesView(plant);
        for (Box box : boxes) {
            try {
                simpleEmployee = scrapingService
                        .goToAndGetEmployeeBy(box);
                Employee employee = employeeService
                        .getBy(simpleEmployee, plant);
                if (employee.getClothes().isEmpty()) {
                    scrapingService.loadBox();
                    List<Cloth> clothes = scrapingService
                            .getClothesAsRotational(client);
                    clothService.loadClothes(clothes, employee, user);
                } else {
                    rotationalClothService.setClothesAsRotational(employee);
                }
            } catch (MultipleBoxException e) {
                e.getEmployees().stream()
                        .forEach(emp -> doubledBoxes.add(emp));
            } catch (SkippedEmployeeException e) {
            } catch (EmptyElementException e) {
                rotationalEmployeesWithoutClothes.add(simpleEmployee);
            }
        }
        return new ClothesLoadedResponse(
                rotationalEmployeesWithoutClothes,
                doubledBoxes);
    }

    public StandardResponse updateRotationalClothes(long plantId) {
        Plant plant = plantService.getById(plantId);
        User user = userService.getDefaultUser();
        Set<Employee> rotationalEmployees = employeeService.getRotationalEmployees(plant);
//        rotationalEmployees.forEach(e -> employeeService.updateEmployees(rotationalEmployees, user));
        employeeService.updateEmployees(rotationalEmployees, user);
        rotationalClothService.updateReleased(plantId);
        return StandardResponse.createForSucceed("Sukces");
    }

    public StandardResponse updateClothes(long plantId) {
        Plant plant = plantService.getById(plantId);
        User user = userService.getDefaultUser();
        if (plant.getLastUpdate().isBefore(LocalDate.now())) {
            ClothesAndEmployeesToUpdate clothesAndEmployees =
                    scrapingService.getClothesAndEmployeesToUpdate(plant);
            System.out.println("clothes to update: " + clothesAndEmployees.getUpdatedClothes().size());
            clothService.updateLastWashingDate(clothesAndEmployees.getUpdatedClothes());
            Set<SimpleEmployee> employeesToUpdate =
                    getEmployeesToUpdate(plant, clothesAndEmployees.getEmployeesToUpdate());
            System.out.println("employees to update: " + clothesAndEmployees.getEmployeesToUpdate().size());
            employeeService.updateEmployees(employeesToUpdate, plant, user);
        } else {
            Set<SimpleEmployee> employeesWithActiveOrders =
                    employeeService.getSimpleEmployeesWithActiveOrdersBy(plant);
            employeeService.updateEmployees(employeesWithActiveOrders, plant, user);
        }
        rotationalClothService.updateReleased(plantId);
        return StandardResponse.createForSucceed("Sukces", true);
    }

    private Set<SimpleEmployee> getEmployeesToUpdate(Plant plant, Set<SimpleEmployee> employeesToUpdate) {
        Set<SimpleEmployee> employeesWithActiveOrders = employeeService.getSimpleEmployeesWithActiveOrdersBy(plant);
        employeesToUpdate.addAll(employeesWithActiveOrders);
        return employeesToUpdate;
    }

    private ClothesLoadedResponse loadClothes(List<Locker> lockers, Plant plant) {
        User user = userService.getDefaultUser();
        SimpleEmployee lastEmployee = new SimpleEmployee("", "");
        List<SimpleEmployee> skippedEmployees = new LinkedList<>();
        List<SimpleEmployee> employeesWithoutClothes = new LinkedList<>();
        Set<SimpleEmployee> doubledBoxes = new HashSet<>();
        int lockerNumber;
        int loadedEmployees = 0;
        scrapingService.connectToBoxesView(plant);
        for (Locker l : lockers) {
            lockerNumber = l.getLockerNumber();
            scrapingService.goToLocker(lockerNumber);
            List<Element> boxesRows = scrapingService.getBoxesRows();
            for (Element row : boxesRows) {
                SimpleEmployee simpleEmployee =
                        scrapingService.getSimpleEmployee(row);
                try {
                    Employee employee = employeeService.getBy(
                            simpleEmployee, plant);
                    if (employee.getClothes().size() > 0) continue;
                    scrapingService.loadBox(row);
                    List<Cloth> clothes = scrapingService.getClothes(employee);
                    clothService.loadClothes(clothes, employee, user);
                    loadedEmployees++;
                    lastEmployee = simpleEmployee;
                } catch (SkippedEmployeeException e) {
                    skippedEmployees.add(e.getEmployee());
                } catch (MultipleBoxException e) {
                    e.getEmployees().stream()
                            .forEach(emp -> doubledBoxes.add(emp));
                } catch (EmptyElementException e) {
                    employeesWithoutClothes.add(simpleEmployee);
                }
            }
        }
        return new ClothesLoadedResponse(
                loadedEmployees,
                lastEmployee,
                employeesWithoutClothes,
                doubledBoxes,
                skippedEmployees);
    }

    private int createEmployeesBoxesAndLockers(
            List<Element> boxesRows,
            Plant plant,
            Department department,
            Location location,
            long clientId) {
        Element row;
        int lockerNumber;
        int capacity;
        List<Locker> lockers = new LinkedList<>();
        List<Box> boxes = new LinkedList<>();
        Employee employee;
        int previousLockerNumber = scrapingService.getFirstLockerNumber(boxesRows);
        for (int i = 0; i < boxesRows.size(); i++) {
            row = boxesRows.get(i);
            lockerNumber = scrapingService.getLockerNumber(row);
            if (isThisNextLocker(lockerNumber, previousLockerNumber) ||
                    isThisLastRow(boxesRows, i)) {
                if (isThisLastRow(boxesRows, i)) {
                    SimpleEmployee simpleEmployee =
                            scrapingService.getSimpleEmployee(row);
                    employee = employeeService.createEmployee(simpleEmployee, clientId);
                    boxes.add(boxService.createBox(row, employee));
                }
                capacity = scrapingService.getLockerCapacity(boxes);
                boxes = boxService.createMissingEmptyBoxes(boxes, capacity);
                lockers.add(lockerService.create(
                        previousLockerNumber,
                        boxes,
                        capacity,
                        plant,
                        department,
                        location));
                boxes.clear();
                SimpleEmployee simpleEmployee =
                        scrapingService.getSimpleEmployee(row);
                employee = employeeService.createEmployee(simpleEmployee, clientId);
                boxes.add(boxService.createBox(row, employee));
                previousLockerNumber = lockerNumber;
            } else {
                SimpleEmployee simpleEmployee =
                        scrapingService.getSimpleEmployee(row);
                employee = employeeService.createEmployee(simpleEmployee, clientId);
                boxes.add(boxService.createBox(row, employee));
            }
        }
        return lockers.size();
    }

    private boolean isThisLastRow(List<Element> boxesRows, int i) {
        return i == boxesRows.size() - 1;
    }

    private boolean isThisNextLocker(int lockerNumber, int previousLockerNumber) {
        return previousLockerNumber != lockerNumber;
    }


    public void test() {
        long plantId = 1l;
        List<Locker> allBy = lockerService.getAllBy(plantId);
        System.out.println("");
    }

}
