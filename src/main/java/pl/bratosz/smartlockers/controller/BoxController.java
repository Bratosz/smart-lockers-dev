package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.BoxService;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.List;

@RestController
@RequestMapping("/box")
public class BoxController {

    private BoxService boxesService;

    public BoxController(BoxService boxesService) {
        this.boxesService = boxesService;
    }

    @GetMapping
    public List<Box> getAll() {
        return boxesService.findAll();
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/{id}")
    public Box getBoxById(@PathVariable Long id) {
        return boxesService.getBoxById(id);
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get-by-locker-number-and-plant-id/{lockerNumber}/{plantId}")
    public List<Box> getByLockerNumberAndPlantId(@PathVariable int lockerNumber,
                                         @PathVariable long plantId) {
        return boxesService.getByLockerNumberAndPlantId(lockerNumber, plantId);
    }

    @JsonView(Views.EmployeeCompleteInfo.class)
    @GetMapping("/get-with-complete-employee-info/{id}")
    public Box getBoxWithCompleteEmployeeInfo(@PathVariable long id) {
        return boxesService.getBoxById(id);
    }


    @PostMapping("/set_actual_status")
    public List<Box> setActualStatus() {
        return boxesService.setActualBoxStatus();
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get-filtered/{plantId}/{departmentId}/{locationId}/{boxStatus}")
    public List<Box> getFiltered(@PathVariable long plantId,
                                 @PathVariable long departmentId,
                                 @PathVariable long locationId,
                                 @PathVariable Box.BoxStatus boxStatus) {
        return boxesService.getFiltered(
                plantId, departmentId, locationId, boxStatus);
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get-by-locker/{lockerId}")
    public List<Box> getByLocker(@PathVariable long lockerId) {
        return boxesService.getByLocker(lockerId);
    }


    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get-by-last-name/{lastName}/{userId}")
    public List<Box> getByLastName(
            @PathVariable String lastName,
            @PathVariable long userId) {
        lastName = MyString.create(lastName).get();
        return boxesService.getByLastName(lastName, userId);
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/getClothesAndEmployeesToUpdate-by-locker-number-and-plant" +
            "/{lockerNumber}/{plantId}")
    public List<Box> getByLockerNumberAndPlant(
            @PathVariable int lockerNumber,
            @PathVariable long plantId) {
        return boxesService.getByLockerNumberAndPlantId(
                lockerNumber, plantId);
    }

    @JsonView(Views.Public.class)
    @DeleteMapping("/delete/{boxId}")
    public StandardResponse deleteBy(@PathVariable long boxId) {
        return boxesService.hardDeleteBy(boxId);
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/update/{boxId}/{userId}")
    public CreateResponse update(
            @PathVariable long boxId,
            @PathVariable long userId) {
        return boxesService.update(boxId, userId);
    }

}
