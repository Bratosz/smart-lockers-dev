package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.exception.WrongIdException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.EmployeeService;
import pl.bratosz.smartlockers.service.employees.EmployeeWithActiveOrders;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @JsonView(Views.EmployeeCompleteInfo.class)
    @GetMapping("/{id}")
    public Employee getEmployeeById(
            @PathVariable Long id) throws RuntimeException {
        if (id < 0) throw new WrongIdException("Passed id is: " + id + ". It should be higher or equal to 0");
        return employeeService.getById(id);
    }

    @JsonView(Views.EmployeeCompleteInfo.class)
    @GetMapping("/with-complete-info/{id}")
    public Employee getWithCompleteInfo(
            @PathVariable long id) {
        return employeeService.getById(id);
    }

    @JsonView(Views.Public.class)
    @GetMapping("/with-active-orders/{userId}")
    public StandardResponse getWithActiveOrders(@PathVariable long userId) {
         return StandardResponse.createForSucceed(employeeService.getWithActiveOrders(userId));
    }

    @JsonView(Views.Public.class)
    @GetMapping("/with-active-orders-by-order-type/{orderType}/{userId}")
    public List<EmployeeWithActiveOrders> getWithActiveOrders(@PathVariable long userId, @PathVariable OrderType orderType) {
        return employeeService.getWithActiveOrdersByOrderType(userId, orderType);
    }

    @JsonView(Views.EmployeeCompleteInfo.class)
    @GetMapping("/update/{employeeId}/{userId}")
    public StandardResponse update(
            @PathVariable long userId,
            @PathVariable long employeeId) {
        return employeeService.update(employeeId, userId);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/create-employee-and-add-to-box/{boxId}/{lastName}/{firstName}" +
            "/{departmentId}/{positionId}")
    public StandardResponse createEmployee(
            @PathVariable long boxId,
            @PathVariable String lastName,
            @PathVariable String firstName,
            @PathVariable long departmentId,
            @PathVariable long positionId) throws RuntimeException {
        firstName = MyString.create(firstName).get();
        lastName = MyString.create(lastName).get();
            return employeeService.createEmployee(boxId, departmentId, firstName, lastName, positionId);

    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/create-employee-and-add-to-next-free-box/{lastName}/{firstName}/{departmentId}" +
            "/{locationId}/{positionId}")
    public StandardResponse createEmployee(
            @PathVariable String lastName,
            @PathVariable String firstName,
            @PathVariable long departmentId,
            @PathVariable long locationId,
            @PathVariable long positionId) {
        lastName = MyString.create(lastName).get();
        firstName = MyString.create(firstName).get();
        return employeeService.createEmployee(lastName, firstName, departmentId, locationId, positionId);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("add/{plantNumber}/{department}/{location}")
    public Employee createEmployee(@PathVariable int plantNumber,
                                   @PathVariable Department department,
                                   @PathVariable Location location,
                                   @RequestBody Employee employee) {
        return employeeService.createEmployeeAndAssignToBox(plantNumber, department, location, employee);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find/{firstName}/{lastName}")
    public List<Employee> getByFirstNameAndLastName(@PathVariable String firstName, @PathVariable String lastName) {
        return employeeService.getByFirstNameAndLastName(firstName, lastName);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find-by-last-name/{lastName}/{clientId}")
    public List<Employee> getEmployeesByLastName(
            @PathVariable String lastName,
            @PathVariable long clientId) {
         lastName = MyString.create(lastName).get();
        return employeeService.getEmployeesByLastName(lastName, clientId);
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find-by-first-name/{firstName}/{userId}")
    public List<Employee> getEmployeesByFirstName(
            @PathVariable String firstName,
            @PathVariable long userId) {
        firstName.toUpperCase().trim();
        return employeeService.getEmployeesByFirstName(firstName, userId);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_box/{userId}/{lockerNumber}/{boxNumber}/{plantNumber}/{targetLockerNumber}/{targetBoxNumber}/{targetPlantNumber}")
    public Box changeEmployeeBox(
            @PathVariable long userId,
            @PathVariable int lockerNumber,
            @PathVariable int boxNumber,
            @PathVariable int plantNumber,
            @PathVariable int targetLockerNumber,
            @PathVariable int targetBoxNumber,
            @PathVariable int targetPlantNumber) {
        return employeeService.changeEmployeeBox(userId, lockerNumber, boxNumber, plantNumber, targetLockerNumber,
                targetBoxNumber, targetPlantNumber);

    }

    @JsonView(Views.InternalForBoxes.class)
    @PostMapping("/dismiss-by-id/{clothesReturned}/{employeeId}/{userId}")
    public StandardResponse dismissBy(
            @PathVariable boolean clothesReturned,
            @PathVariable long employeeId,
            @PathVariable long userId) {
        return employeeService.dismissBy(clothesReturned, employeeId, userId);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_last_name_by_id/{id}")
    public Employee changeEmployeeLastNameById(
            @RequestBody Employee employee, @PathVariable Long id) {
        return employeeService.changeEmployeeLastName(employee.getLastName(), id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_first_name_by_id/{id}")
    public Employee changeEmployeeFirstNameById(
            @RequestBody Employee employee, @PathVariable Long id) {
        return employeeService.changeEmployeeFirstNameById(employee.getFirstName(), id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_first_name_and_last_name_by_id/{id}")
    public Employee changeEmployeeFirstAndLastNameById(
            @RequestBody Employee employee, @PathVariable Long id) {
        return employeeService.changeEmployeeFirstNameAndLastNameById(employee, id);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/change_department/{id}")
    public Employee changeDepartment(
            @RequestBody Employee employee, @PathVariable Long id) {
        return employeeService.changeDepartment(employee.getDepartment(), id);
    }

    @PostMapping("/relocate/{plantId}/{departmentId}/{locationId}/{employeeId}")
    public StandardResponse relocate(
            @PathVariable long plantId,
            @PathVariable long departmentId,
            @PathVariable long locationId,
            @PathVariable long employeeId) {
        return employeeService.relocate(
                plantId,
                departmentId,
                locationId,
                employeeId);
    }

    @JsonView(Views.EmployeeCompleteInfo.class)
    @PostMapping("/test")
    public Employee test() {
        return employeeService.getById(40l);
    }

    @JsonView(Views.EmployeeCompleteInfo.class)
    @PostMapping("/change-department-and-position-for-new-employee/{employeeId}/{departmentId}/{positionId}/{userId}")
    public StandardResponse changeDepartmentAndPositionForNewEmployee(
            @PathVariable long employeeId,
            @PathVariable long departmentId,
            @PathVariable long positionId,
            @PathVariable long userId) {
        return employeeService.changeDepartmentAndPositionForNewEmployee(employeeId, departmentId, positionId, userId);
    }

    @JsonView(Views.EmployeeCompleteInfo.class)
    @PostMapping("/change-department-and-position-for-old-employee/{employeeId}/{departmentId}/{positionId}/{userId}")
    public StandardResponse changeDepartmentAndPositionForOldEmployee(
            @PathVariable long employeeId,
            @PathVariable long departmentId,
            @PathVariable long positionId,
            @PathVariable long userId) {
        return employeeService.changeDepartmentAndPositionForOldEmployee(employeeId, departmentId, positionId, userId);
    }


}
