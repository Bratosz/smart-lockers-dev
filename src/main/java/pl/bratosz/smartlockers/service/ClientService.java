package pl.bratosz.smartlockers.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.*;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.response.DataLoadedResponse;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.creators.ClientCreator;
import pl.bratosz.smartlockers.service.exels.DataBaseLoader;
import pl.bratosz.smartlockers.service.exels.LoadType;
import pl.bratosz.smartlockers.service.pasting.employee.EmployeeToCreate;
import pl.bratosz.smartlockers.utils.string.MyString;
import pl.bratosz.smartlockers.service.exels.plant.template.data.PlantDataContainer;

import java.util.List;
import java.util.Set;

@Service
public class ClientService {
    private ClientRepository clientRepository;
    private EmployeesRepository employeesRepository;
    private MeasurementListService measurementListService;
    private LockersRepository lockersRepository;
    private UsersRepository usersRepository;
    private PlantService plantService;
    private EmployeeService employeeService;
    private LockerService lockerService;
    private ClientArticleService clientArticleService;
    private PositionService positionService;
    private DepartmentService departmentService;
    private LocationService locationService;
    private UserService userService;
    private EmployeesToCreateRepository employeesToCreateRepository;

    public ClientService(ClientRepository clientRepository,
                         EmployeesRepository employeesRepository,
                         MeasurementListService measurementListService,
                         LockersRepository lockersRepository,
                         UsersRepository usersRepository,
                         PlantService plantService,
                         EmployeeService employeeService,
                         LockerService lockerService, ClientArticleService clientArticleService, PositionService positionService, DepartmentService departmentService, LocationService locationService, UserService userService, EmployeesToCreateRepository employeesToCreateRepository) {
        this.clientRepository = clientRepository;
        this.employeesRepository = employeesRepository;
        this.measurementListService = measurementListService;
        this.lockersRepository = lockersRepository;
        this.usersRepository = usersRepository;
        this.plantService = plantService;
        this.employeeService = employeeService;
        this.lockerService = lockerService;
        this.clientArticleService = clientArticleService;
        this.positionService = positionService;
        this.departmentService = departmentService;
        this.locationService = locationService;
        this.userService = userService;
        this.employeesToCreateRepository = employeesToCreateRepository;
    }

    public CreateResponse create(String name) {
        Client client = new ClientCreator(this).create(MyString.create(name).get());
        return new CreateResponse(client, "Utworzono klienta");
    }

    public StandardResponse createForLoadFromFile(String clientName, int plantNumber, long userId) {
        Client client = new ClientCreator(this)
                .createWithPlantAndDepartment(clientName, plantNumber);
        userService.putClientToUser(plantNumber, userId);
        return StandardResponse.createForSucceed("Utworzono klienta: " + client.getName(), client);
    }

    public Plant createFromFileWithResponse(
            int plantNumber,
            long userId,
            PlantDataContainer dataContainer) {
        Client client = getByUser(userId);
        Plant plant = client.getPlants().stream()
                .filter(p -> p.getPlantNumber() == plantNumber).findFirst().get();
        Department mainDepartment = departmentService
                .createDefaultDepartmentsAndReturnMain(client, plantNumber);
//        List<Department> departments = departmentService.createWithDepartmentPositionAndLocation(dataContainer.getDepartments(), client, plantNumber);
//        List<Location> locations = locationService.createWithDepartmentPositionAndLocation(
//                dataContainer.getLocations(), client, plant);
//        lockerService.createFromZUSO(
//                dataContainer, plant, mainDepartment, locations);
//        clientArticleService
//                .addNewArticles(dataContainer.getArticles(), client);
//        List<Position> positions = positionService.createPositions(
//                dataContainer.getPositions(), client);
//        employeeService.createWithDepartmentPositionAndLocation(
//                dataContainer.getEmployees(), positions, locations, departments, mainDepartment);
        return plant;
    }

    public DataLoadedResponse loadDataBase(long clientId, LoadType loadType, XSSFWorkbook wb) {
        Client client = getById(clientId);
        DataBaseLoader dbLoader = new DataBaseLoader(wb, loadType, client);
        List<Locker> lockers = dbLoader.loadDataBase();
        lockersRepository.saveAll(lockers);
        DataLoadedResponse response = DataLoadedResponse.createLockersBoxesAndEmployeesLoadedSuccesfully(lockers);
        return response;
    }

    public Client getById(long clientId) {
        return clientRepository.getById(clientId);
    }

    public Employee getEmployeeBy(long employeeId) {
        return employeesRepository.getEmployeeById(employeeId);
    }

    public Set<Employee> getEmployeesToAssignBy(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        return measurementListService.getEmployeesToAssign(clientId);
    }

    public StandardResponse getEmployeesToCreate(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        List<EmployeeToCreate> employees = employeesToCreateRepository.getAllByClient(clientId);
        return StandardResponse.createForSucceed(employees);
    }


    public Set<Employee> updateEmployeesToRelease(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Client client = getById(clientId);
        MeasurementList measurementList = client.getMeasurementList();
        Set<Employee> employeesToRelease = updateEmployeesToRelease(measurementList.getEmployeesToAssign());
        measurementList.setEmployeesToAssign(employeesToRelease);
        client.setMeasurementList(measurementList);
        return clientRepository.save(client).getMeasurementList().getEmployeesToAssign();
    }

    private Set<Employee> updateEmployeesToRelease(Set<Employee> employeesToRelease) {
//        for(Employee e : employeesToRelease) {
//            e = employeeService.update(employee);
//            if(employeeService.isReleased())
//        }
        return null;
    }

    public List<Employee> getEmployeesToMeasure(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        return measurementListService.getEmployeesToMeasure(clientId);
    }

    public Set<Employee> getEmployeesToRelease(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        return measurementListService.getEmployeesToRelease(clientId);
    }

    public List<Client> getAll() {
        return clientRepository.getAll();
    }

    public Client getByUser(long userId) {
        long clientId = usersRepository.getById(userId).getActualClientId();
        return clientRepository.getById(clientId);
    }

    public ClientRepository getClientRepository() {
        return clientRepository;
    }

    public MeasurementListService getMeasurementListService() {
        return measurementListService;
    }

    public PlantService getPlantService() {
        return plantService;
    }

    public DepartmentService getDepartmentService() {
        return departmentService;
    }


}
