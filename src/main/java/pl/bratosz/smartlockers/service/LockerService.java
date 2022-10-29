package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.DepartmentsRepository;
import pl.bratosz.smartlockers.repository.LocationRepository;
import pl.bratosz.smartlockers.repository.LockersRepository;
import pl.bratosz.smartlockers.repository.PlantsRepository;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.response.UpdateResponse;
import pl.bratosz.smartlockers.service.managers.creators.LockerCreator;
import pl.bratosz.smartlockers.service.exels.plant.template.data.PlantDataContainer;
import pl.bratosz.smartlockers.service.exels.plant.template.data.TemplateLockers;
import pl.bratosz.smartlockers.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.model.Box.*;
import static pl.bratosz.smartlockers.model.Box.BoxStatus.ALL;

@Service
public class LockerService {
    private LockersRepository lockersRepository;
    private EmployeeService employeeService;
    private BoxService boxesService;
    private DepartmentService departmentService;
    private LocationService locationService;
    private DepartmentsRepository departmentsRepository;
    private LocationRepository locationRepository;
    private PlantsRepository plantsRepository;

    public LockerService(LockersRepository lockersRepository,
                         EmployeeService employeeService,
                         BoxService boxesService,
                         DepartmentService departmentService,
                         LocationService locationService,
                         DepartmentsRepository departmentsRepository,
                         LocationRepository locationRepository,
                         PlantsRepository plantsRepository) {
        this.lockersRepository = lockersRepository;
        this.employeeService = employeeService;
        this.boxesService = boxesService;
        this.departmentService = departmentService;
        this.locationService = locationService;
        this.departmentsRepository = departmentsRepository;
        this.locationRepository = locationRepository;
        this.plantsRepository = plantsRepository;
    }

    public Locker deleteLockerByNumber(Long id) {
        return lockersRepository.deleteLockerById(id);
    }

    public Locker changeLocation(Integer lockerNumber, int plantNumber,
                                 Location location, Location desiredLocation) {
        Locker locker = getLockerByParameters(lockerNumber, plantNumber, location);
        locker.setLocation(desiredLocation);
        return lockersRepository.save(locker);
    }

    public Locker getLockerByParameters(Integer lockerNumber,
                                        int plantNumber,
                                        Location location) {
        return lockersRepository.getLockerByParameters(lockerNumber, plantNumber, location);
    }

    public List<Locker> getLockersFromRange(int plantNumber, int firstLocker, int lastLocker) {
        return lockersRepository.getLockersFromRange(plantNumber, firstLocker, lastLocker);
    }

    public List<Locker> getLockers(int from, int to, long plantId) {
        return lockersRepository.getLockers(from, to, plantId);
    }

    public Locker create(
            int lockerNumber,
            int capacity,
            int plantNumber,
            String departmentName,
            String locationName) {
        Plant plant = plantsRepository.getByPlantNumber(plantNumber);
        Department department = departmentService.getByNameAndPlantNumber(departmentName, plantNumber);
        Location location = locationService.getByNameAndPlantNumber(locationName, plantNumber);

        return create(lockerNumber, capacity, plant, department, location);
    }

    public List<Locker> createFromZUSO(
            PlantDataContainer dataContainer,
            Plant plant,
            Department department,
            List<Location> locations) {
        List<TemplateLockers> templateLockerList = (List<TemplateLockers>) dataContainer.getLockers();
        Map<String, Location> locationsMap = Utils.toMapWithToStringKey(locations);
        List<Locker> lockers = new ArrayList<>();
        for (TemplateLockers templateLocker : templateLockerList) {
            int first = templateLocker.getFirstLockerNumber();
            int last = templateLocker.getLastLockerNumber();
            int capacity = templateLocker.getCapacity();
            Location location = locationsMap.get(templateLocker.getLocation());
            for (int lockerNo = first; lockerNo <= last; lockerNo++) {
                lockers.add(create(
                        lockerNo,
                        capacity,
                        plant,
                        department,
                        location));
            }
        }
        return lockers;
    }

    public Locker create(
            int lockerNumber,
            int capacity,
            Plant plant,
            Department department,
            Location location) {
        Locker locker = LockerCreator.create(
                lockerNumber, capacity, plant, department, location);
        return lockersRepository.save(locker);
    }


    public Locker createWithCustomBoxNumbers(
            int lockerNumber,
            List<Integer> boxNumbers,
            Location location,
            Department department,
            Plant plant) {
        Locker locker = LockerCreator.createWithCustomBoxNumbers(
                lockerNumber, boxNumbers, plant, department, location);
        return lockersRepository.save(locker);
    }


    public CreateResponse create(
            int startingLockerNumber,
            int endingLockerNumber,
            int capacity,
            long plantId,
            long departmentId,
            long locationId) {
        Plant plant = plantsRepository.getById(plantId);
        Department department = departmentService.getById(departmentId);
        Location location = locationService.getById(locationId);
        List<Locker> lockers = new LinkedList<>();
        for (int i = startingLockerNumber; i <= endingLockerNumber; i++) {
            lockers.add(
                    LockerCreator.create(
                            i, capacity, plant, department, location));
        }
        lockers = lockersRepository.saveAll(lockers);
        return new CreateResponse(lockers, "Utworzono szaf: " + lockers.size());
    }

    public Locker create(Locker locker) {
        return lockersRepository.save(locker);
    }

    public List<Locker> getLockersByPlantNumber(int plantNumber) {
        List<Locker> lockers = lockersRepository.findAllByPlantNumber(plantNumber);
        return lockers;
    }

    public int getAmountOfLockersByPlantId(long id) {
        return lockersRepository.getAmountOfLockersByPlantId(id);
    }


    public List<Locker> getLockersByPlantAndNumber(
            long plantId,
            int lockerNumber) {
        return lockersRepository.getLockersByPlantAndNumber(plantId, lockerNumber);
    }

    public List<Locker> getFiltered(
            long plantId,
            long departmentId,
            long locationId) {
        if (departmentId == 0 && locationId == 0) {
            return lockersRepository.getByPlantId(plantId);
        } else if (departmentId == 0) {
            return lockersRepository.getByPlantIdAndLocationId(plantId, locationId);
        } else if (locationId == 0) {
            return lockersRepository.getByPlantIdAndDepartmentId(plantId, departmentId);
        } else {
            return lockersRepository.getByPlantIdAndDepartmentIdAndLocationId(
                    plantId, departmentId, locationId);
        }
    }

    public List<Locker> getFiltered(
            long plantId,
            long departmentId,
            long locationId,
            BoxStatus boxStatus) {
        List<Locker> lockers = getFiltered(
                plantId, departmentId, locationId);
        for (Locker locker : lockers) {
            List<Box> boxes = locker.getBoxes();
            List<Box> filteredBoxes = new LinkedList<>();
            for (Box b : boxes) {
                if (boxStatus.equals(ALL) || b.getBoxStatus().equals(boxStatus)) {
                    filteredBoxes.add(b);
                }
            }
            locker.setBoxes(filteredBoxes);
        }
        if (lockers.size() <= 50) {
            return lockers;
        } else {
            return lockers.subList(0, 49);
        }
    }

    public Locker getBy(long lockerId) {
        Locker locker = lockersRepository.getLockerById(lockerId);
        List<Box> boxes = locker.getBoxes().stream()
                .limit(50)
                .collect(Collectors.toList());
        locker.setBoxes(boxes);
        return locker;
    }

    public Box getBoxByNumber(Locker locker, int boxNumber) {
        return locker.getBoxes()
                .stream()
                .filter(b -> b.getBoxNumber() == boxNumber)
                .findFirst().get();
    }


    public List<Box> getBoxesBy(long lockerId) {
        return lockersRepository.getLockerById(lockerId).getBoxes();
    }

    public UpdateResponse changeDepartmentAndLocation(
            int startingLockerNumber,
            int endLockerNumber,
            long plantId,
            long departmentId,
            long locationId) {
        Department department;
        Location location;
        List<Locker> lockers = lockersRepository.getLockers(
                startingLockerNumber, endLockerNumber, plantId);
        if (departmentId == 0 && locationId == 0) {
            return new UpdateResponse("Nie wybrano oddziału ani lokalizacji");
        } else if (locationId == 0) {
            department = departmentsRepository.getById(departmentId);
            lockers.stream()
                    .forEach(l -> l.setDepartment(department));
        } else if (departmentId == 0) {
            location = locationRepository.getById(locationId);
            lockers.stream()
                    .forEach(l -> l.setLocation(location));
        } else {
            department = departmentsRepository.getById(departmentId);
            location = locationRepository.getById(locationId);
            lockers.stream()
                    .forEach(l -> {
                        l.setDepartment(department);
                        l.setLocation(location);
                    });
        }
        lockers = lockersRepository.saveAll(lockers);
        return new UpdateResponse("Zaktualizowano szafki", lockers);
    }

    public List<Locker> getAllBy(long plantId) {
        return lockersRepository.getAllByPlantId(plantId);
    }

    public Locker create(
            int lockerNumber,
            List<Box> boxes,
            int capacity,
            Plant plant,
            Department department,
            Location location) {
        Locker l = LockerCreator.create(
                lockerNumber,
                boxes,
                capacity,
                plant,
                department,
                location);
        return lockersRepository.save(l);
    }

    public void saveLocker(Locker l) {
        lockersRepository.save(l);
    }


}
