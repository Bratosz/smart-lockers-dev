package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.users.ManagementList;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add-employee-to-management-list/{employeeId}/{userId}")
    public StandardResponse addEmployeeToManagementList(
            @PathVariable("employeeId") long employeeId,
            @PathVariable("userId") long userId) {
        return userService.addEmployeeToManagementList(employeeId, userId);
    }

    @JsonView(Views.InternalForEmployeesForOurStaff.class)
    @GetMapping("/management-list/{userId}")
    public ManagementList getManagementList(
            @PathVariable("userId") long userId) {
        return userService.getManagementList(userId);
    }

    @JsonView(Views.Public.class)
    @PutMapping("/put-actual-client-by-plant-number/{plantNumber}/{userId}")
    public StandardResponse putClientToUser(
            @PathVariable int plantNumber,
            @PathVariable long userId) {
        return userService.putClientToUser(plantNumber, userId);
    }

}
