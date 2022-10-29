package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.service.DepartmentService;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    private DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @JsonView(Views.Public.class)
    @GetMapping("/get-all/{userId}")
    public List<Department> getAll(@PathVariable long userId) {
        return departmentService.getAll(userId);
    }

    @GetMapping("/byName/{departmentName}/{mainPlantNumber}")
    public Department getByName(@PathVariable String departmentName, @PathVariable int mainPlantNumber) {
        return departmentService.getByNameAndPlantNumber(departmentName, mainPlantNumber);
    }

    @JsonView(Views.Public.class)
    @PostMapping("/add_alias/{departmentId}/{alias}")
    public Department addAlias(@PathVariable long departmentId,
                               @PathVariable String alias) {
        alias = MyString.create(alias).get();
        return departmentService.addAlias(departmentId, alias);

    }

    @JsonView(Views.Public.class)
    @PutMapping("/create/{departmentName}/{defaultPlantId}/{clientId}")
    public CreateResponse create(@PathVariable String departmentName,
                                 @PathVariable long defaultPlantId,
                                 @PathVariable long clientId) {
        departmentName = MyString.create(departmentName).get();
        return departmentService.createWithResponse(departmentName, defaultPlantId, clientId);
    }

    public Department create(String departmentName,
                             long clientId,
                             int mainPlantNumber,
                             boolean surrogate) {
        departmentName = MyString.create(departmentName).get();
        return departmentService.create(departmentName, clientId, mainPlantNumber, surrogate);
    }

    @PostMapping("/add_plant/{departmentId}/{plantNumber}")
    public Department addPlant(@PathVariable long departmentId, @PathVariable int plantNumber) {
        return departmentService.addPlant(departmentId, plantNumber);
    }
}
