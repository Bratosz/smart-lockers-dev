package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.EmployeeService;
import pl.bratosz.smartlockers.service.MeasurementListService;
import pl.bratosz.smartlockers.service.OrderService;

import java.util.Set;

@RestController
@RequestMapping("/measurement-list")
public class MeasurementListController {

    private MeasurementListService measurementListService;
    private EmployeeService employeeService;
    private OrderService orderService;

    public MeasurementListController(MeasurementListService measurementListService, EmployeeService employeeService, OrderService orderService) {
        this.measurementListService = measurementListService;
        this.employeeService = employeeService;
        this.orderService = orderService;
    }

    @PostMapping("/remove-employee/{employeeId}")
    @JsonView(Views.Public.class)
    public StandardResponse removeEmployee(
            @PathVariable long employeeId) {
        Employee employee = employeeService.getById(employeeId);
        return measurementListService.remove(employee);
    }

    @PostMapping("/set-measured-employees-as-assigned/{userId}")
    @JsonView(Views.Public.class)
    public StandardResponse setMeasuredEmployeesAsAssigned(
            @PathVariable long userId) {
        Set<Employee> employees = measurementListService.setMeasuredEmployeesAsAssigned(userId);
        orderService.updateReportedEmployees(employees);
        return StandardResponse.createForSucceed();
    }
}
