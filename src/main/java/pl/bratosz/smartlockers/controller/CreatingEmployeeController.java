package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.pasting.CreatingEmployeeService;
import pl.bratosz.smartlockers.service.pasting.employee.EmployeeToCreateData;
import pl.bratosz.smartlockers.service.pasting.employee.PastedEmployeeEDPL;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/create-employees")
public class CreatingEmployeeController {

    private CreatingEmployeeService creatingEmployeeService;

    public CreatingEmployeeController(CreatingEmployeeService creatingEmployeeService) {
        this.creatingEmployeeService = creatingEmployeeService;
    }

    @PostMapping
    @JsonView(Views.InternalForEmployees.class)
    public StandardResponse createEmployees(@RequestBody List<EmployeeToCreateData> employeesData) {
        return creatingEmployeeService.createTheEmployees(employeesData);
    }

    @PostMapping("/add-employees-edpl/{userId}")
    @JsonView(Views.InternalForEmployees.class)
    public StandardResponse addEmployeesEDPL(@PathVariable long userId, @RequestBody List<PastedEmployeeEDPL> employees) {
        return creatingEmployeeService.add(userId, employees);
    }

    @PostMapping("/set-department-position-location" +
            "/{departmentId}/{positionId}/{locationId}")
    @JsonView(Views.InternalForEmployees.class)
    public StandardResponse setDepartmentPositionLocation(
            @PathVariable long departmentId,
            @PathVariable long positionId,
            @PathVariable long locationId,
            @RequestBody Set<Long> employeesIds) {
        return creatingEmployeeService.setDepartmentPositionAndLocation(
                departmentId, positionId, locationId, employeesIds);
    }

    @PostMapping("/swap-names/{employeeId}")
    @JsonView(Views.InternalForEmployees.class)
    public StandardResponse swapNames(@PathVariable long employeeId) {
        return creatingEmployeeService.swapNames(employeeId);
    }



    @DeleteMapping("/delete/{userId}")
    public StandardResponse deleteEmployees(@PathVariable long userId, @RequestBody List<Long> employeesIds) {
        return creatingEmployeeService.deleteEmployeesToCreate(employeesIds, userId);
    }
}
