package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.LockersRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.response.UpdateResponse;
import pl.bratosz.smartlockers.service.LockerService;


import java.util.List;

@RestController
@RequestMapping("/locker")
public class LockerController {

    private LockersRepository lockersRepository;
    private LockerService lockersService;
    private UsersRepository usersRepository;

    public LockerController(LockersRepository lockersRepository,
                            LockerService lockersService, UsersRepository usersRepository) {
        this.lockersRepository = lockersRepository;
        this.lockersService = lockersService;
        this.usersRepository = usersRepository;
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/and_boxes/{clientId}")
    public List<Locker> getAllWithBoxes(@PathVariable long clientId) {
        List<Locker> lockers = lockersRepository.getAllByClientId(clientId);
        if (lockers.size() <= 3) {
            return lockers;
        } else {
        }
        return lockers.subList(0, 2);
    }

    @JsonView(Views.LockersWithoutBoxes.class)
    @GetMapping("/get-all/{userId}")
    public List<Locker> getAllByClientId(
            @PathVariable long userId) {
        long clientId = usersRepository.getById(userId).getActualClientId();
        return lockersRepository.getAllByClientId(clientId);
    }

    @JsonView(Views.LockersWithoutBoxes.class)
    @GetMapping("/get-by-plant/{plantId}")
    public List<Locker> getByAllByPlant(
            @PathVariable long plantId) {
        return lockersService.getAllBy(plantId);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{plantId}/{lockerNumber}")
    public List<Locker> getLockersByPlantAndNumber(
            @PathVariable long plantId,
            @PathVariable int lockerNumber) {
        return lockersService.getLockersByPlantAndNumber(plantId, lockerNumber);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/get/{lockerId}")
    public Locker getBy(@PathVariable long lockerId) {
        return lockersService.getBy(lockerId);
    }

    @JsonView(Views.InternalForBoxes.class)
    @GetMapping("/get-boxes/{lockerId}")
    public List<Box> getBoxesBy(@PathVariable long lockerId) {
        return lockersService.getBoxesBy(lockerId);
    }

    @JsonView(Views.LockersWithoutBoxes.class)
    @GetMapping("/get-filtered/{plantId}/{departmentId}/{locationId}")
    public List<Locker> getFiltered(
            @PathVariable long plantId,
            @PathVariable long departmentId,
            @PathVariable long locationId) {
        return lockersService.getFiltered(
                plantId,
                departmentId,
                locationId);
    }

    @JsonView(Views.LockersWithoutBoxes.class)
    @PostMapping("/change-department-and-location" +
            "/{startingLockerNumber}" +
            "/{endLockerNumber}" +
            "/{plantId}" +
            "/{departmentId}" +
            "/{locationId}")
    public UpdateResponse changeDepartmentAndLocation(
            @PathVariable int startingLockerNumber,
            @PathVariable int endLockerNumber,
            @PathVariable long plantId,
            @PathVariable long departmentId,
            @PathVariable long locationId) {
        return lockersService.changeDepartmentAndLocation(
                startingLockerNumber,
                endLockerNumber,
                plantId,
                departmentId,
                locationId);
    }

    @PostMapping("/createWithDepartmentPositionAndLocation" +
            "/{startingLockerNumber}" +
            "/{endLockerNumber}" +
            "/{capacity}" +
            "/{plantId}" +
            "/{departmentId}" +
            "/{locationId}")
    @JsonView(Views.InternalForLockers.class)
    public CreateResponse create(
            @PathVariable int startingLockerNumber,
            @PathVariable int endLockerNumber,
            @PathVariable int capacity,
            @PathVariable long plantId,
            @PathVariable long departmentId,
            @PathVariable long locationId) {
        return lockersService.create(
                startingLockerNumber,
                endLockerNumber,
                capacity,
                plantId,
                departmentId,
                locationId);
    }

    @PostMapping("/create-locker-test")
    public void createTestingLocker() {
        lockersService.create(1, 2, 10, 1,1,1);
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/filter/{plantId}/{departmentId}/{locationId}/{boxStatus}")
    public List<Locker> getFiltered(@PathVariable long plantId,
                                 @PathVariable long departmentId,
                                 @PathVariable long locationId,
                                 @PathVariable Box.BoxStatus boxStatus) {
        return lockersService.getFiltered(plantId, departmentId, locationId, boxStatus);
    }

    @PostMapping("/create/{lockerNumber}/{capacity}/{plantNumber}/{department}/{location}")
    public Locker createLocker(
            @PathVariable int lockerNumber,
            @PathVariable int capacity,
            @PathVariable int plantNumber,
            @PathVariable String department,
            @PathVariable String location) {
        return lockersService.create(
                lockerNumber, capacity, plantNumber, department, location);
    }

    @JsonView(Views.InternalForLockers.class)
    @PostMapping("/change_location/{lockerNumber}/{plantNumber}/{location}/{desiredLocation}")
    public Locker changeLocation(@PathVariable Integer lockerNumber, @PathVariable int plantNumber,
                                 @PathVariable Location location, @PathVariable Location desiredLocation) {
        return lockersService.changeLocation(lockerNumber, plantNumber, location, desiredLocation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        lockersRepository.deleteById(id);
    }

    @DeleteMapping("/deleteLockerById/{id}")
    public Locker deleteLockerByNumber(@PathVariable Long id) {
        return lockersService.deleteLockerByNumber(id);
    }
}
