package pl.bratosz.smartlockers.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.comparators.employee.BoxNumberSorter;
import pl.bratosz.smartlockers.comparators.employee.LockerNumberSorter;
import pl.bratosz.smartlockers.comparators.employee.PlantNumberSorter;
import pl.bratosz.smartlockers.exception.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.EmployeeGeneralRepository;
import pl.bratosz.smartlockers.repository.EmployeesRepository;
import pl.bratosz.smartlockers.repository.PositionsRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.employees.EmployeeWithActiveOrders;
import pl.bratosz.smartlockers.service.exels.plant.template.data.TemplateEmployee;
import pl.bratosz.smartlockers.service.managers.EmployeeManager;
import pl.bratosz.smartlockers.service.pasting.employee.EmployeeToCreate;
import pl.bratosz.smartlockers.service.update.ScrapingService;
import pl.bratosz.smartlockers.utils.Utils;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.*;

import static pl.bratosz.smartlockers.model.Box.BoxStatus.OCCUPY;

@Service
public class EmployeeService {
    private EmployeesRepository employeesRepository;
    private EmployeeGeneralRepository employeeGeneralRepository;
    private BoxService boxService;
    private PlantService plantService;
    private DepartmentService departmentService;
    private UserService userService;
    private ClothService clothService;
    private SimpleBoxService simpleBoxService;
    private User user;
    private ScrapingService scrapingService;
    private LocationService locationService;
    private MeasurementListService measurementListService;
    private EmployeeManager employeeManager;
    private UsersRepository usersRepository;
    private PositionsRepository positionsRepository;
    private RotationalClothService rotationalClothService;
    private PositionService positionService;

    public EmployeeService(EmployeesRepository employeesRepository,
                           EmployeeGeneralRepository employeeGeneralRepository,
                           PlantService plantService,
                           DepartmentService departmentService,
                           UserService userService,
                           ClothService clothService,
                           SimpleBoxService simpleBoxService,
                           EmployeeManager employeeManager,
                           ScrapingService scrapingService,
                           LocationService locationService,
                           MeasurementListService measurementListService, UsersRepository usersRepository, PositionsRepository positionsRepository, RotationalClothService rotationalClothService, PositionService positionService) {
        this.employeesRepository = employeesRepository;
        this.employeeGeneralRepository = employeeGeneralRepository;
        this.plantService = plantService;
        this.departmentService = departmentService;
        this.userService = userService;
        this.clothService = clothService;
        this.simpleBoxService = simpleBoxService;
        this.employeeManager = employeeManager;
        this.scrapingService = scrapingService;
        this.locationService = locationService;
        this.measurementListService = measurementListService;
        this.usersRepository = usersRepository;
        this.positionsRepository = positionsRepository;
        this.rotationalClothService = rotationalClothService;
        this.positionService = positionService;
    }


    public BoxService getBoxService() {
        return boxService;
    }

    @Autowired
    public void setBoxService(BoxService boxService) {
        this.boxService = boxService;
    }

    private void loadUser(User user) {
        this.user = user;
    }

    private void loadUser(long userId) {
        User user = userService.getUserById(userId);
        this.user = user;
    }

    public List<Employee> getAllEmployees() {
        return employeesRepository.getAll();
    }

    public List<Employee> getEmployeesByLastName(String lastName, long clientId) {
        return employeesRepository.getEmployeesByLastName(lastName, clientId);
    }

    public StandardResponse createEmployee(long boxId,
                                           long departmentId,
                                           String firstName,
                                           String lastName,
                                           long positionId) {
        Department department = departmentService.getById(departmentId);
        Position position = departmentService.getPosition(department, positionId);
        Box box = boxService.getBoxById(boxId);
        if (box.getBoxStatus().equals(OCCUPY)) {
            return StandardResponse.createForFailure("Brak wolnych szafek, podaj inny oddział lub lokalizację");
        }
        Employee employee = create(firstName, lastName, position, box, department);
        return StandardResponse.createForSucceed("Dodano pracownika do szafki " +
                        box.getLocker().getPlant().getPlantNumber() +
                        " " + box.getLocker().getLockerNumber() + "/" + box.getBoxNumber(),
                employee);
    }

    public Set<Employee> getRotationalEmployees(Plant plant) {
        Position rotational = positionService.getRotational(plant);
        return employeesRepository.getByPlantAndPosition(plant, rotational);
    }


    public List<Employee> create(
            List<TemplateEmployee> templateEmployees,
            List<Position> positions,
            List<Location> locations,
            List<Department> departments,
            Department mainDepartment) {
        List<Employee> employees = new LinkedList<>();
        Map<String, Position> positionsMap = Utils.toMapWithToStringKey(positions);
        Map<String, Location> locationsMap = Utils.toMapWithToStringKey(locations);
        Map<String, Department> departmentsMap = Utils.toMapWithToStringKey(departments);

        for (TemplateEmployee e : templateEmployees) {
            Position position = positionsMap.get(e.getPosition().getName());
            Location location = locationsMap.get(e.getLocation());
            Department department = departmentsMap.get(e.getDepartment());
            try {
                employees.add(createEmployeeAndAssignToBox(
                        e.getFirstName(), e.getLastName(), position, location, department, mainDepartment));
            } catch (BoxNotAvailableException ex) {
            }
        }
        return employees;
    }

    private Employee createEmployeeAndAssignToBox(
            String firstName,
            String lastName,
            Position position,
            Location location,
            Department department,
            Department mainDepartment) throws BoxNotAvailableException {
        Box box = boxService.findNextFreeBox(department, mainDepartment, location);
        return create(firstName, lastName, position, box, department);
    }

    public Employee create(
            String firstName,
            String lastName,
            Position position,
            Box box,
            Department department) {
        Employee employee = new Employee(firstName, lastName, department, position, true);
        employee.addToBox(box);
        employee = employeesRepository.save(employee);
        measurementListService.add(employee, department.getClient());
        return employee;
    }


    public StandardResponse createEmployee(
            String lastName,
            String firstName,
            long departmentId,
            long locationId,
            long positionId) {
        Department department = departmentService.getById(departmentId);
        Location location = locationService.getById(locationId);
        Position position = departmentService.getPosition(department, positionId);
        try {
            return createEmployee(lastName, firstName, department, position, location);
        } catch (BoxNotAvailableException e) {
            return StandardResponse.createForFailure("Brak wolnych szafek, podaj inny oddział lub lokalizację");
        }
    }

    public StandardResponse createEmployee(EmployeeToCreate e) throws BoxNotAvailableException {
        return createEmployee(
                e.getLastName(), e.getFirstName(), e.getDepartment(), e.getPosition(), e.getLocation());
    }

    private StandardResponse createEmployee(
            String lastName,
            String firstName,
            Department department,
            Position position,
            Location location) throws BoxNotAvailableException {
        Box box = boxService.findNextFreeBox(location);
        Employee employee = create(firstName, lastName, position, box, department);
        return StandardResponse.createForSucceed("Dodano pracownika do szafki " +
                        box.getLocker().getPlant().getPlantNumber() +
                        " " + box.getLocker().getLockerNumber() + "/" + box.getBoxNumber(),
                employee);
    }

    public Employee create(
            String firstName,
            String lastName,
            Box box,
            String departmentName) throws BoxNotAvailableException {
        if (box.getBoxStatus().equals(OCCUPY)) {
            throw new BoxNotAvailableException("Szafka jest zajęta");
        }
        Department department = departmentService.getBy(departmentName, box);
        Employee employee = new Employee(firstName, lastName, department, true);
        employee.addToBox(box);
        return employeesRepository.save(employee);
    }


    public Employee createEmployee(SimpleEmployee e,
                                   long clientId) {
        Department department = departmentService.getByAliasAndClientId(
                e.getDepartmentAlias(),
                clientId);
        return create(
                e.getFirstName(),
                e.getLastName(),
                e.getComment(),
                department);
    }

    public Employee createEmployee(SimpleEmployee e,
                                   Box box) {
        long clientId = box.getLocker().getPlant().getClient().getId();
        Employee employee = createEmployee(e, clientId);
        employee.addToBox(box);
        return employeesRepository.save(employee);
    }

    public Employee createEmployeeAndAssignToBox(int plantNumber,
                                                 Department department,
                                                 Location location,
                                                 Employee employee) {
        Box emptyBox;
        //change location if there is no free boxes
        try {
            emptyBox = boxService.findNextFreeBox(department, plantNumber, location);
        } catch (BoxNotAvailableException e) {
            e.getMessage();
            return null;
        }
        emptyBox.setBoxStatus(OCCUPY);
        employee.addToBox(emptyBox);

        return employeesRepository.save(employee);
    }

    public Employee getById(Long id) {
        return employeesRepository.getEmployeeById(id);
    }

    public EmployeeGeneral getGeneralEmployeeById(long id) {
        return employeeGeneralRepository.getById(id);
    }

    public List<Employee> getByFirstNameAndLastName(String firstName,
                                                    String lastName) {
        List<Employee> filteredEmployees = new LinkedList<>();
        List<Employee> employees = employeesRepository.getByFirstNameAndLastName(firstName, lastName);
        for (Employee e : employees) {
            if (e.isActive()) {
                filteredEmployees.add(e);
            }
        }
        return filteredEmployees;
    }

    public List<Employee> sortByPlantBoxAndLocker(List<Employee> employeesToSort) {
        Collections.sort(employeesToSort, new PlantNumberSorter()
                .thenComparing(new LockerNumberSorter())
                .thenComparing(new BoxNumberSorter()));

        return employeesToSort;
    }


    public StandardResponse dismissBy(
            boolean clothesReturned,
            long employeeId,
            long userId) {
        loadUser(userId);
        Employee employee = getById(employeeId);
        if (employee.isActive()) {
            Box box = null;
            employee = employeeManager.dismiss(employee, clothesReturned, user);
            employeesRepository.save(employee);
            if (employee.getBox() != null) {
                box = boxService.getBoxById(employee.getBox().getId());
            }
            return StandardResponse.createForSucceed("Zwolniono pracownika.", box);
        } else {
            return StandardResponse.createForFailure("Pracownik został wcześniej zwolniony.");
        }
    }

    public Employee save(Employee employee) {
        return employeesRepository.save(employee);
    }

    public StandardResponse changeName(Long employeeId, String lastName, String firstName) {
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        lastName = MyString.create(lastName).get();
        firstName = MyString.create(firstName).get();
        if(!lastName.equals("-")) employee.setLastName(lastName);
        if(!firstName.equals("-")) employee.setFirstName(firstName);
        employee = employeesRepository.save(employee);
        return StandardResponse.createForSucceed(employee);
    }

    public StandardResponse changeFirstName(Long employeeId, String firstName) {
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        firstName = MyString.create(firstName).get();
        employee.setFirstName(firstName);
        employee = employeesRepository.save(employee);
        return StandardResponse.createForSucceed(employee);
    }

    public StandardResponse changeLastName(Long employeeId, String lastName) {
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        lastName = MyString.create(lastName).get();
        employee.setLastName(lastName);
        employee = employeesRepository.save(employee);
        return StandardResponse.createForSucceed(employee);
    }


    public Integer deleteEmployeeById(Long id) {
        return employeesRepository.deleteEmployeeById(id);
    }


    public Employee changeEmployeeFirstNameAndLastNameById(Employee updatedEmployee,
                                                           Long id) {
        Employee employee = getById(id);
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        return employeesRepository.save(employee);
    }

    public Employee changeDepartment(Department department, Long id) {
        Employee employee = getById(id);
        employee.setDepartment(department);
        return employeesRepository.save(employee);
    }

    public Employee getEmployeeByFullNameAndFullBoxNumber(
            String firstName, String lastName, int lockerNo, int boxNo) {
        List<Employee> employees =
                employeesRepository.getByFirstNameAndLastName(firstName, lastName);
        return filterEmployeesByFullBoxNumber(lockerNo, boxNo, employees);

    }

    private Employee filterEmployeesByFullBoxNumber(int lockerNo, int boxNo, List<Employee> employees) {
        if (employees.size() == 1) {
            return employees.get(0);
        } else if (employees.size() > 1) {
            for (Employee e : employees) {
                if (e.getBox().getLocker().getLockerNumber() == lockerNo && e.getBox().getBoxNumber() == boxNo) {
                    return e;
                }
            }
        }
        return null;
    }

    public Employee getOneEmployee(String firstName, String lastName) {
        List<Employee> employees = getByFirstNameAndLastName(firstName, lastName);
        if (employees.size() == 1) {
            return employees.get(0);
        } else {
            return employeesRepository.getEmployeeById((long) 1);
        }
    }

    public List<Employee> getEmployeesByFirstName(String firstName, long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        return employeesRepository.getEmployeesByFirstName(firstName, clientId);
    }

    public Box changeEmployeeBox(long userId, int lockerNumber, int boxNumber, int plantNumber, int targetLockerNumber, int targetBoxNumber, int targetPlantNumber) {
        return new Box();
    }


    public Employee release(Employee employeeToRelease, Box box) {
        employeeToRelease.setAsPastBox(
                simpleBoxService.createSimpleBox(box, employeeToRelease));
        employeeToRelease.setBox(null);
        return employeesRepository.save(employeeToRelease);
    }

    public StandardResponse relocate(
            long plantId,
            long departmentId,
            long locationId,
            long employeeId) {
        Box newBox = boxService.findNextFreeBox(plantId, departmentId, locationId);
        if (newBox.getLocker() == null) {
            return StandardResponse.createForFailure("Nie znaleziono wolnej szafki");
        }
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        Box releasedBox = boxService.releaseBox(employee.getBox());
        SimpleBox simpleBox = new SimpleBox(releasedBox);
        employee.setAsPastBox(simpleBox);
        assignToBox(employee, newBox);
        return StandardResponse.createForSucceed("Przeniesiono do szafki " + newBox.toString());
    }

    private Employee assignToBox(Employee employee, Box freeBox) {
        freeBox.setBoxStatus(OCCUPY);
        employee.addToBox(freeBox);
        return employeesRepository.save(employee);
    }

    public void create(
            String firstName,
            String lastName,
            String comment,
            Department department,
            Box box) {
        Employee employee = Employee.create(
                firstName,
                lastName,
                comment,
                department,
                box);
        employeesRepository.save(employee);
    }

    public Employee create(
            String firstName,
            String lastName,
            String comment,
            Department department) {
        Employee e = Employee.createWithoutBox(
                firstName,
                lastName,
                comment,
                department);
        return employeesRepository.save(e);
    }

    public Employee getBy(
            SimpleEmployee simpleEmployee,
            Plant plant) throws SkippedEmployeeException, MultipleBoxException {
        int lockerNumber = simpleEmployee.getLockerNumber();
        int boxNumber = simpleEmployee.getBoxNumber();
        Department department = departmentService.getByAliasAndClientId(
                simpleEmployee.getDepartmentAlias(), plant.getClient().getId());
        List<Employee> employees = employeesRepository.getBy(
                lockerNumber,
                boxNumber,
                plant);
        if (employees.size() > 1) {
            throw new MultipleBoxException(simpleEmployee);
        } else if (employees.size() == 0) {
            System.out.println(simpleEmployee.toString());
            throw new SkippedEmployeeException(simpleEmployee);
        } else {
            Employee employee = employees.stream().findFirst().get();
            if (employee.getLastName().equals(simpleEmployee.getLastName())
                    && employee.getFirstName().equals(simpleEmployee.getFirstName())) {
                if (employee.getDepartment().getId() == department.getId()) {
                    return employee;
                } else {
                    employee.setDepartment(department);
                    return employeesRepository.save(employee);
                }
            } else {
                throw new SkippedEmployeeException(simpleEmployee);
            }
        }
    }

    private void update(Employee previousEmployee, SimpleEmployee actualEmployee) {

    }

    public StandardResponse update(
            SimpleEmployee e, Plant plant, User user) throws MultipleBoxException, SkippedEmployeeException {
        System.out.println(e.toString());
        long employeeId = getBy(e, plant).getId();
        return update(employeeId, user.getId());
    }

    public StandardResponse update(long employeeId, long userId) {
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        User user = userService.getUserById(userId);
        scrapingService.connectToBoxesView(employee);
        int status;
        try {
            scrapingService.goTo(employee);
            status = scrapingService.checkBoxStatusBy(employee);
        } catch (MultipleBoxException e) {
            try {
                scrapingService.goToEmployeeByLastNameAndBoxNoAndLockerNo(employee);
                status = scrapingService.checkBoxStatusBy(employee);
            } catch (MultipleBoxException e1) {
                return StandardResponse.createForFailure(
                        "Znaleziono kilka szaf o tym samym numerze");
            }
        }
        if (status == 1) {
            try {
//                SimpleEmployee simpleEmployee = scrapingService.getSimpleEmployee();
//                update(employee, simpleEmployee);
                scrapingService.loadBox();
                List<Cloth> actualClothes = scrapingService.getClothes(employee);
                try {
                    clothService.updateClothes(
                            actualClothes, employee, user);
                } catch (ClothException e) {
                    e.printStackTrace();
                }
                employee = employeesRepository.getEmployeeById(employee.getId());
                return StandardResponse.createForSucceed(
                        "Zaktualizowano ubrania", employee);
            } catch (EmptyElementException e) {
                return StandardResponse.createForFailure(
                        "Pracownik nie ma ubrań");
            }
        } else if (status == 0) {
            return StandardResponse.createForFailure(
                    "Szafka jest pusta");
        } else {
            try {
                SimpleEmployee simpleEmployee =
                        scrapingService.goToAndGetEmployeeBy(employee.getBox());
                return StandardResponse.createForFailure(
                        "Inny pracownik w szafce: " +
                                simpleEmployee.getLastName() + " " +
                                simpleEmployee.getFirstName() + " " +
                                simpleEmployee.getDepartmentAlias(),
                        simpleEmployee);
            } catch (MultipleBoxException e) {
                return StandardResponse.createForFailure(
                        "Znaleziono kilka szaf o tym samym numerze");
            }
        }
    }

    public Employee updateEmployeeClothes(Employee employee, List<Cloth> clothes, User user) throws ClothException {
        clothService.updateClothes(clothes, employee, user);
        return employeesRepository.getEmployeeById(employee.getId());
    }

    public void save(List<Employee> employees) {
        employeesRepository.saveAll(employees);
    }

    public Set<Employee> getEmployeesWithActiveOrders(long clientId) {
        return employeesRepository.getEmployeesWithActiveOrders(clientId);
    }

    public Set<Employee> getEmployeesWithActiveOrdersThatAreNotReported(long clientId) {
        return employeesRepository.getEmployeesWithActiveAndNotReportedOrders(clientId);
    }


    public boolean isReported(Employee employee) {
        long amountOfNotReportedOrders = employee.getMainOrders().stream()
                .filter(o -> !o.isReported())
                .filter(o -> o.isActive())
                .count();
        if (amountOfNotReportedOrders == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Set<Employee> getAllActiveEmployees(long clientId) {
        return employeesRepository.getAllActive(clientId);
    }

    public StandardResponse changeDepartmentAndPositionForOldEmployee(
            long employeeId, long departmentId, long positionId, long userId) {
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        return changeDepartmentAndPosition(employee, departmentId, positionId);
    }

    public StandardResponse changeDepartmentAndPositionForNewEmployee(
            long employeeId, long departmentId, long positionId, long userId) {
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        if (employee.getClothes().size() == 0) {
            return changeDepartmentAndPosition(employee, departmentId, positionId);
        } else {
            return StandardResponse.createForFailure("Pracownik ma już przypisane ubrania," +
                    "jeśli chcesz zmienić stanowisko, musisz najpierw usunąć zamówione sztuki.");
        }
    }

    private StandardResponse changeDepartmentAndPosition(
            Employee employee, long departmentId, long positionId) {
        Department department = departmentService.getById(departmentId);
        Position position = positionsRepository.getById(positionId);
        employee.setDepartment(department);
        employee.setPosition(position);
        if (position.getName().equals("ROTACJA")) {
            rotationalClothService.setAsRotational(employee);
        }
        employee = employeesRepository.save(employee);
        return StandardResponse.createForSucceed(employee);

    }

    public Set<SimpleEmployee> getSimpleEmployeesWithActiveOrdersBy(Plant plant) {
        HashSet<SimpleEmployee> simpleEmployees = new HashSet<>();
        Set<SimpleEmployee> employeesWithActiveOrdersBy = employeesRepository.getSimpleEmployeesWithActiveOrdersBy(plant);
        simpleEmployees.addAll(employeesWithActiveOrdersBy);
        return simpleEmployees;
    }

    public void updateEmployees(Set<Employee> employeesToUpdate, User user) {
        employeesToUpdate.forEach(e -> update(e.getId(), user.getId()));
    }

    public void updateEmployees(Set<SimpleEmployee> employeesToUpdate, Plant plant, User user) {
        ArrayList<SimpleEmployee> simpleEmployeesToUpdate = new ArrayList<>(employeesToUpdate);
        Collections.sort(simpleEmployeesToUpdate);
        int counter = 0;
        for (SimpleEmployee e : simpleEmployeesToUpdate) {
            System.out.println(counter++);
            try {
                update(e, plant, user);
            } catch (MultipleBoxException e1) {
                e1.printStackTrace();
            } catch (SkippedEmployeeException e1) {
                e1.printStackTrace();
            }
        }
    }

    public List<EmployeeWithActiveOrders> getWithActiveOrders(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        return employeesRepository.getWithActiveOrders(clientId);
    }

    public List<EmployeeWithActiveOrders> getWithActiveOrdersByOrderType(long userId, OrderType orderType) {
        long clientId = usersRepository.getActualClientId(userId);
        if (orderType.equals(OrderType.ALL)) {
            return employeesRepository.getWithActiveOrders(clientId);
        } else {
            return employeesRepository.getWithActiveOrdersByOrderType(clientId, orderType);
        }
    }

    public StandardResponse changeEmployeeName(Long employeeId, String lastName, String firstName) {
        Employee employee = employeesRepository.getEmployeeById(employeeId);
        lastName = MyString.create(lastName).get();
        firstName = MyString.create(firstName).get();
        if (!lastName.isEmpty()) employee.setLastName(lastName);
        if (!firstName.isEmpty()) employee.setFirstName(firstName);
        employee = employeesRepository.save(employee);
        return StandardResponse.createForSucceed(employee);
    }
}
