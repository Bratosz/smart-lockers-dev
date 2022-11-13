package pl.bratosz.smartlockers.service;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.*;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.BoxesRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.managers.creators.BoxCreator;
import pl.bratosz.smartlockers.service.managers.BoxManager;
import pl.bratosz.smartlockers.service.update.ScrapingService;
import pl.bratosz.smartlockers.utils.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.model.Box.BoxStatus.*;

@Service
public class BoxService {

    private BoxesRepository boxesRepository;
    private UserService userService;
    private User user;
    private BoxManager boxManager;
    private EmployeeService employeeService;
    private EmployeeDummyService employeeDummyService;
    private ScrapingService scrapingService;
    private UsersRepository usersRepository;

    public BoxService(BoxesRepository boxesRepository,
                      UserService userService,
                      BoxManager boxManager,
                      EmployeeDummyService employeeDummyService,
                      ScrapingService scrapingService, UsersRepository usersRepository) {
        this.boxesRepository = boxesRepository;
        this.userService = userService;
        this.boxManager = boxManager;
        this.employeeDummyService = employeeDummyService;
        this.scrapingService = scrapingService;
        this.usersRepository = usersRepository;
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void loadUser(User user) {
        this.user = user;
    }

    public Box findNextFreeBox(
            long plantId,
            long departmentId,
            long locationId) throws MyException {
        Box.BoxStatus boxStatus = FREE;
        List<Box> filtered = getFiltered(plantId, departmentId, locationId, boxStatus);
        if(filtered.isEmpty()) {
            throw new MyException("Nie znaleziono wolnej szafki o tych parametrach.");
        } else {
            return filtered.get(0);
        }
    }


    public Box findNextFreeBox(
            Department department,
            int plantNumber,
            Location location) throws BoxNotAvailableException {
        Box.BoxStatus boxStatus = FREE;
        List<Box> boxes = boxesRepository.getBoxesByParameters(department, plantNumber, location, boxStatus);
        if (boxes.size() == 0) {
            throw new BoxNotAvailableException();
        }
        return boxes.stream().findFirst().get();
    }

    public Box findNextFreeBox(Location location) throws BoxNotAvailableException {
        Box.BoxStatus boxStatus = FREE;
        List<Box> boxes = boxesRepository
                .getBoxesByParameters(location, boxStatus);
        if (boxes.size() == 0) {
            throw new BoxNotAvailableException();
        }
        return boxes.stream().findFirst().get();
    }

    public Box findNextFreeBox(Department department, Department mainDepartment, Location location) throws BoxNotAvailableException {
        Box.BoxStatus boxStatus = FREE;
        List<Box> boxes = boxesRepository
                .getBoxesByParameters(department, location, boxStatus);
        if (boxes.size() == 0) {
            boxes = boxesRepository.getBoxesByParameters(mainDepartment, location, boxStatus);
            if(boxes.size() == 0) throw new BoxNotAvailableException();
        }
        return boxes.stream().findFirst().get();
    }


    public Box releaseBox(long boxId, long userId) {
        user = userService.getUserById(userId);
        Box box = boxesRepository.getById(boxId);

        box = boxManager.release(box);
        return boxesRepository.save(box);
    }

    public Box releaseBox(Box box) {
        box = boxManager.release(box);
        return boxesRepository.save(box);
    }


    public Box setEmployee(EmployeeGeneral employee, Box box) {
        if (employee.getClass().isInstance(Employee.class)) {
            box.setEmployee(employee);
            box.setBoxStatus(OCCUPY);
            return boxesRepository.save(box);
        } else {
            return box;
        }
    }

    public List<Box> findAll() {
        return boxesRepository.findAll();
    }

    public Box getBox(int lockerNumber, int boxNumber, Location location,
                      int plantNumber) {
        return boxesRepository.getBox(lockerNumber, boxNumber, location, plantNumber);
    }

    public Box getBox(long plantId, int lockerNumber, int boxNumber) throws MyException {
        Box box = boxesRepository.getBox(plantId, lockerNumber, boxNumber);
        if(box == null) throw new MyException("Brak szafki o takim numerze");
        else return box;
    }

    public Box getBoxById(Long id) {
        return boxesRepository.getById(id);
    }

    public List<Box> createBoxesForLocker(Locker locker) {
        BoxCreator boxCreator = new BoxCreator();
        List<Box> boxes = boxCreator.createBoxes(locker);
        return boxes;
    }

    public List<Box> setActualBoxStatus() {
        List<Box> boxes = boxesRepository.findAll();
        List<Box> updatedBoxes = new LinkedList<>();

        for (Box b : boxes) {
            if (employeeIsPresent(b) && b.getBoxStatus().equals(FREE)) {
                b.setBoxStatus(OCCUPY);
                updatedBoxes.add(b);
                boxesRepository.save(b);
            } else if (!employeeIsPresent(b) && b.getBoxStatus().equals(OCCUPY)) {
                b.setBoxStatus(FREE);
                updatedBoxes.add(b);
                boxesRepository.save(b);
            }
        }
        return updatedBoxes;
    }

    private boolean employeeIsPresent(Box b) {
        EmployeeGeneral e = b.getEmployee();
        return employeeIsPresent(e);
    }

    private boolean employeeIsPresent(EmployeeGeneral e) {
        if (e.getClass().isInstance(Employee.class))
            return true;
        return false;

    }

    public List<Box> getByLocker(long lockerId) {
        return boxesRepository.getByLockerId(lockerId);
    }

    public List<Box> getFiltered(Long plantId,
                                 Long departmentId,
                                 Long locationId,
                                 Box.BoxStatus boxStatus) {
        switch(boxStatus) {
            case ALL:
                if(departmentId == 0 && locationId == 0) {
                    return boxesRepository
                            .findTop45ByLockerPlantId(plantId);
                } else if (locationId == 0) {
                    return boxesRepository
                            .findTop45ByLockerPlantIdAndLockerDepartmentId(plantId, departmentId);
                } else if (departmentId == 0) {
                    return boxesRepository
                            .findTop45ByLockerPlantIdAndLockerLocationId(plantId, locationId);
                } else {
                    return boxesRepository
                            .findTop45ByLockerPlantIdAndLockerDepartmentIdAndLockerLocationId(
                                    plantId, departmentId, locationId);
                }
            case OCCUPY:
            case FREE:
                if(departmentId == 0 && locationId == 0) {
                    return boxesRepository
                            .findTop45ByLockerPlantIdAndBoxStatus(plantId, boxStatus);
                } else if (locationId == 0) {
                    return boxesRepository
                            .findTop45ByLockerPlantIdAndBoxStatusAndLockerDepartmentId(
                            plantId, boxStatus, departmentId);
                } else if (departmentId == 0) {
                    return boxesRepository
                            .findTop45ByLockerPlantIdAndBoxStatusAndLockerLocationId(
                                    plantId, boxStatus, locationId);
                } else {
                    return boxesRepository
                            .findTop45ByLockerPlantIdAndBoxStatusAndLockerDepartmentIdAndLockerLocationId(
                                    plantId, boxStatus, departmentId, locationId);
                }
                default:
                    return boxesRepository
                            .findTop45ByLockerPlantId(plantId);
        }
    }

    public Employee extractEmployee(Box box) {
        if (box.getBoxStatus().equals(OCCUPY)) {
            Employee employeeToRelease = (Employee) box.getEmployee();
            Box releasedBox = boxManager.release(box);
            updateBox(releasedBox);
            return employeeService.release(employeeToRelease, releasedBox);
        } else {
            throw new IllegalStateException("Box is already empty");
        }
    }

    private void updateBox(Box box) {
        boxesRepository.save(box);
    }


    public Box createAdditionalDuplicatedBox(int boxNumber, Locker locker) {
        Box box = BoxCreator.createBox(boxNumber, locker);
        box.setDuplicated(true);
        return boxesRepository.save(box);
    }

    public StandardResponse hardDeleteBy(long boxId) {
        Box box = boxesRepository.getById(boxId);
        if (box.getBoxStatus().equals(OCCUPY)) {
            return StandardResponse.createForFailure("Szafka jest zajęta. Aby ją usunąć " +
                    "zwolnij najpierw pracownika, który się w niej znajduje");
        } else {
            boxesRepository.deleteHardById(box.getId());
            return StandardResponse.createForSucceed("Usunięto szafkę", box);
        }
    }

    public List<Box> getByLastName(String lastName, long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        return boxesRepository.getByLastNameAndClientId(lastName, clientId);
    }

    public List<Box> getByLockerNumberAndPlantId(int lockerNumber, long plantId) {
        return boxesRepository.getByLockerNumberAndPlantId(
                lockerNumber, plantId);
    }

    public void setDuplicatedBoxes(int boxNumber, Locker locker) {
        locker.getBoxes()
                .stream()
                .filter(b -> b.getBoxNumber() == boxNumber)
                .forEach(b -> {
                    b.setDuplicated(true);
                    boxesRepository.save(b);
                });
    }

    public List<Box> setDuplicatedBoxes(List<Box> boxes) {
        List<Integer> duplicates = Utils.getDuplicates(boxes
                .stream()
                .map(b -> b.getBoxNumber())
                .collect(Collectors.toList()));
        for (Box b : boxes) {
            if (duplicates.contains(b.getBoxNumber())) {
                b.setDuplicated(true);
            }
        }
        return boxes;
    }

    public Box create(int boxNumber, Employee employee) {
        EmployeeDummy dummy = employeeDummyService.createDummy();
        Box b = BoxCreator.createBox(boxNumber, employee, dummy);
        return boxesRepository.save(b);
    }

    public Box create(int boxNumber) {
        EmployeeDummy dummy = employeeDummyService.createDummy();
        Box b = BoxCreator.createEmptyBox(boxNumber, dummy);
        return boxesRepository.save(b);
    }

    public List<Box> createMissingEmptyBoxes(List<Box> boxes, int capacity) {
        List<Integer> sortedBoxNumbers = boxes.stream()
                .map(b -> b.getBoxNumber())
                .sorted()
                .collect(Collectors.toList());
        int firstBoxNumber = boxes.get(0).getBoxNumber();
        if (firstBoxNumber != 0) firstBoxNumber = 1;
        List<Integer> missingBoxNumbers =
                Utils.findMissingNumbersFromRange(
                        sortedBoxNumbers,
                        firstBoxNumber,
                        capacity);
        for (int i : missingBoxNumbers) {
            boxes.add(create(i));
        }
        return boxes;
    }

    public Box save(Box b) {
        return boxesRepository.save(b);
    }

    public Set<Box> getBoxesFromRange(
            int lockerNumber,
            int startingBoxNumber,
            int endBoxNumber,
            Plant plant) {
        Set<Box> boxesFromRange = boxesRepository.getBoxesFromRange(
                lockerNumber, startingBoxNumber, endBoxNumber, plant);
        return boxesFromRange;
    }

    public Set<Box> getBoxesFromRange(
            int lockerNumber,
            int startingBoxNumber,
            int endBoxNumber,
            long plantId) {
        return boxesRepository.getBoxesFromRangeByPlantId(
                lockerNumber, startingBoxNumber, endBoxNumber, plantId);
    }

    public Box createBox(Element row, Employee employee) {
        int boxNumber = scrapingService.getBoxNumber(row);
        return create(
                boxNumber, employee);
    }

    public CreateResponse update(long boxId, long userId) {
        Box box = boxesRepository.getById(boxId);
        Box.BoxStatus boxStatus = box.getBoxStatus();
        if (boxStatus.equals(OCCUPY)) {
            return new CreateResponse(
                    "Szafa jest zajęta");
        }
        User user = userService.getUserById(userId);
        scrapingService.connectToBoxesView(box);
        int status;
        try {
            scrapingService.goTo(box);
            status = scrapingService.checkBoxStatusBy(box);
        } catch (MultipleBoxException e) {
            return new CreateResponse(
                    "Znaleziono kilka szaf o tym szamym numerze");
        }
        if (status == 0) {
            return new CreateResponse(
                    "Szafa jest pusta");
        } else {
            try {
                SimpleEmployee simpleEmployee = scrapingService.getSimpleEmployee();
                Employee employee = employeeService.createEmployee(simpleEmployee, box);
                scrapingService.loadBox();
                List<Cloth> clothes = scrapingService.getClothes(box);
                employee = employeeService.updateEmployeeClothes(
                        employee,
                        clothes,
                        user);
                return new CreateResponse(
                        employee,
                        "Wczytano pracownika do szafki");
            } catch (EmptyElementException e) {
                return new CreateResponse(
                        "Pracownik nie ma ubrań");
            } catch (ClothException e) {
                return new CreateResponse(
                        "Wystąpił problem przy ładowaniu ubrań pracownika." +
                                "Sprawdź czy jego odzież nie jest zduplikowana.");
            }
        }
    }

}
