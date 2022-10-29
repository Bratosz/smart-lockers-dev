package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.response.DataLoadedResponse;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.*;
import pl.bratosz.smartlockers.service.exels.LoadType;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/client")
public class ClientController {
    private ClientService clientService;
    private PlantService plantService;
    private DepartmentService departmentService;
    private LocationController locationController;
    private UserService userService;


    public ClientController(ClientService clientService,
                            PlantService plantService,
                            DepartmentService departmentService, LocationController locationController,
                            UserService userService) {
        this.clientService = clientService;
        this.plantService = plantService;
        this.departmentService = departmentService;
        this.locationController = locationController;
        this.userService = userService;
    }

    @JsonView(Views.ClientBasicInfo.class)
    @GetMapping("/get-by-user/{userId}")
    public Client getByUser(@PathVariable long userId) {
        return clientService.getByUser(userId);
    }


    @JsonView(Views.Public.class)
    @PostMapping("/create/{name}")
    public CreateResponse create(@PathVariable String name) {
        return clientService.create(name);
    }

    @JsonView(Views.Public.class)
    @PostMapping("/create-for-load-from-file/{clientName}/{plantNumber}/{userId}")
    public StandardResponse createForLoadFromFile(
            @PathVariable String clientName,
            @PathVariable int plantNumber,
            @PathVariable long userId) {
        return clientService.createForLoadFromFile(clientName, plantNumber, userId);
    }

    @JsonView(Views.Public.class)
    @GetMapping("/get-all")
    public List<Client> getAll() {
        return clientService.getAll();
    }

    @JsonView(Views.ClientBasicInfo.class)
    @GetMapping("/get-by-id/{clientId}")
    public Client getById(@PathVariable long clientId) {
        return clientService.getById(clientId);
    }

    @JsonView(Views.Public.class)
    @GetMapping("/get-employees-to-create/{userId}")
    public StandardResponse getEmployeesToCreate(@PathVariable long userId) {
        return clientService.getEmployeesToCreate(userId);
    }

    @GetMapping("/get-employees-to-assign/{userId}")
    @JsonView(Views.EmployeeBasicInfo.class)
    public Set<Employee> getEmployeesToAssign(@PathVariable long userId) {
        return clientService.getEmployeesToAssignBy(userId);
    }

    @GetMapping("/get-employees-to-measure/{userId}")
    @JsonView(Views.EmployeeBasicInfo.class)
    public List<Employee> getEmployeesToMeasure(@PathVariable long userId) {
        return clientService.getEmployeesToMeasure(userId);
    }

    @GetMapping("/get-employees-to-release/{userId}")
    @JsonView(Views.EmployeeBasicInfo.class)
    public Set<Employee> getEmployeesToRelease(@PathVariable long userId) {
        return clientService.getEmployeesToRelease(userId);
    }


    @PostMapping("/update-employees-to-release/{userId}")
    @JsonView(Views.Public.class)
    public Set<Employee> updateMeasurementList(@PathVariable long userId) {
        return clientService.updateEmployeesToRelease(userId);
    }

    @PostMapping("/generate-394")
    public Client generateLB() {
        Client client = (Client) clientService.create("LEAR").getEntity();
        long clientId = client.getId();
        Plant plant1 = plantService.create(clientId, 394,
                "394 LEAR BIERUŃ", "", "");
        Department department1 = departmentService.create(
                "GŁÓWNY", clientId, 394, false);
        Department department7 = departmentService.create(
                "ZASTĘPCZY", clientId, 394, true);

        long id1 = locationController.create(
                clientId, "Główna").getId();
        long id4 = locationController.create(
                clientId, "ZASTĘPCZA", true).getId();
        locationController.assignToPlant(id1, 394);
        locationController.assignToPlant(id4, 394);

        userService.create("Admin", "Admin");

        return client;
    }

    @PostMapping("/generate-ht")
    public Client generateHT() {
        Client client = (Client) clientService.create("LEAR").getEntity();
        long clientId = client.getId();
        Plant plant1 = plantService.create(clientId, 176,
                "176 SKHITECH", "", "");
        Department department1 = departmentService.create(
                "PROJECT", clientId, 176, false);
        Department department2 = departmentService.create(
                "CCS", clientId, 176, false);
        Department department3 = departmentService.create(
                "LIBS", clientId, 176, false);
        Department department4 = departmentService.create(
                "MATERIALS", clientId, 176, false);
        Department department5 = departmentService.create(
                "QUALITY", clientId, 176, false);
        Department department6 = departmentService.create(
                "MTSc", clientId, 176, false);
        Department department7 = departmentService.create(
                "ZASTĘPCZY", clientId, 384, true);

        long id1 = locationController.create(
                clientId, "Główna").getId();
        long id2 = locationController.create(
                clientId, "Męska").getId();
        long id3 = locationController.create(
                clientId, "Damska").getId();
        long id4 = locationController.create(
                clientId, "ZASTĘPCZA", true).getId();
        locationController.assignToPlant(id1, 176);
        locationController.assignToPlant(id2, 176);
        locationController.assignToPlant(id3, 176);
        locationController.assignToPlant(id4, 176);

        userService.create("Admin", "Admin");

        return client;
    }

    @PostMapping("/generate")
    @JsonView(Views.Public.class)
    public Client generate() {
        Client client = (Client) clientService.create("LEAR").getEntity();
        long clientId = client.getId();
        Plant plant1 = plantService.create(clientId, 384,
                "384 LEAR Zakład Główny", "", "");
        Plant plant2 = plantService.create(clientId, 385,
                "385 LEAR Zakład Główny", "", "");
        Plant plant3 = plantService.create(clientId, 386,
                "386 LEAR Mantrans", "", "");
        Department department1 = departmentService.create(
                "STRUCTURES", clientId, 384, false);
        Department department2 = departmentService.create(
                "JIT", clientId, 385, false);
        Department department3 = departmentService.create(
                "MANTRANS", clientId, 386, false);
        Department department4 = departmentService.create(
                "JIT LOGISTYKA", clientId, 384, false);
        Department department5 = departmentService.create(
                "ZASTĘPCZY", clientId, 384, true);
        long id1 = locationController.create(
                clientId, "Stara szatnia - piwnica").getId();
        long id2 = locationController.create(
                clientId, "Stara szatnia - parter").getId();
        long id3 = locationController.create(
                clientId, "Nowa szatnia - parter").getId();
        long id4 = locationController.create(
                clientId, "Nowa szatnia - piętro (antresola)").getId();
        long id5 = locationController.create(
                clientId, "Nowa szatnia - piętro (korytarz)").getId();
        long id6 = locationController.create(
                clientId, "Nowa hala - produkcja").getId();
        long id7 = locationController.create(
                clientId, "SEGRO").getId();
        long id8 = locationController.create(
                clientId, "ZASTĘPCZA", true).getId();

        locationController.assignToPlant(id1, 384);
        locationController.assignToPlant(id2, 384);
        locationController.assignToPlant(id3, 384);
        locationController.assignToPlant(id4, 384);
        locationController.assignToPlant(id8, 384);

        locationController.assignToPlant(id5, 385);
        locationController.assignToPlant(id6, 385);

        locationController.assignToPlant(id7, 386);

//        lockerController.createFromZUSO(1,1, 10, 1,1,1);a


        userService.create("Admin", "Admin");

        return client;
    }

    @PostMapping("/load_basic_db/{clientId}/{loadType}")
    public DataLoadedResponse loadBasicDataBase(
            @PathVariable long clientId,
            @PathVariable LoadType loadType,
            @RequestParam("file")MultipartFile basicDataBaseToLoad) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(basicDataBaseToLoad.getInputStream());
            DataLoadedResponse response = clientService.loadDataBase(clientId, loadType, wb);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return DataLoadedResponse.createFailLoadDataFromFile(e.getMessage());
        }
    }
}
