package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.repository.ClientRepository;
import pl.bratosz.smartlockers.repository.PlantsRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.service.update.ScrapingService;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlantService {

    private PlantsRepository plantsRepository;
    private ScrapingService scrapingService;
    private ClientRepository clientRepository;
    private UsersRepository usersRepository;
    private DepartmentService departmentService;
    private LocationService locationService;

    public PlantService(PlantsRepository plantsRepository, ScrapingService scrapingService, ClientRepository clientRepository, UsersRepository usersRepository, DepartmentService departmentService, LocationService locationService) {
        this.plantsRepository = plantsRepository;
        this.scrapingService = scrapingService;
        this.clientRepository = clientRepository;
        this.usersRepository = usersRepository;
        this.departmentService = departmentService;
        this.locationService = locationService;
    }

    public Plant getByNumber(int plantNumber) {
        return plantsRepository.getByPlantNumber(plantNumber);
    }

    public long getPlantId(int plantNumber) {
        return plantsRepository.getPlantIdByPlantNumber(plantNumber);
    }

    public Set<Department> getDepartments(int plantNumber) {
        Plant plant = getByNumber(plantNumber);
        return plant.getDepartments();
    }

    public boolean containsDepartment(String departmentName, int plantNumber) {
        Set<Department> departments = getDepartments(plantNumber);
        String correctDepartmentName = departmentName.toUpperCase().trim();
        boolean found = false;
        for(Department d : departments) {
            if (d.getName().toUpperCase().trim().equals(correctDepartmentName)) {
                found = true;
            }
        }
        return found;
    }

    public Set<Integer> getAllPlantNumbersOfClient(int plantNumber) {
        Client client = plantsRepository.getByPlantNumber(plantNumber).getClient();
        Set<Integer> plantNumbers = client.getPlants().stream()
                .map(plant -> plant.getPlantNumber())
                .collect(Collectors.toSet());
        return plantNumbers;
    }

    public Plant create(int plantNumber, Client client) {
        Plant plant = new Plant(plantNumber);
        plant.setClient(client);
        plant = plantsRepository.save(plant);
        return plant;
    }

    public Plant create(
            Plant plant,
            long clientId) {
        String name = MyString.create(plant.getName()).get();
        String login = MyString.create(plant.getLogin()).get();
        Client client = clientRepository.getById(clientId);
        plant = new Plant(plant.getPlantNumber(), name, login, plant.getPassword());
        plant.setClient(client);
        plant = plantsRepository.save(plant);
        departmentService
                .getSurrogateAndCreateIfNotFound(client, plant.getPlantNumber());
        locationService.createAndAssignToPlant(
                client,  plant.getPlantNumber() + " ZASTĘPCZA", true, plant);
        return plant;
    }

    public Plant create(long clientId, int plantNumber, String name, String login, String password) {
        Client client = clientRepository.getById(clientId);
        Plant plant = new Plant(plantNumber, name, login, password);
        plant.setClient(client);
        plant = plantsRepository.save(plant);
        return plant;
    }

    public CreateResponse createWithResponse(
            Plant plant,
            long clientId) {
        if(plantsRepository.getByPlantNumber(plant.getPlantNumber()) != null) {
            return new CreateResponse("Zakład o podanym numerze już istnieje!");
        }
        plant = create(plant, clientId);
        return new CreateResponse(plant, "Dodano zakład");
    }

    public List<Plant> getAll(long userId) {
        long clientId = usersRepository.getById(userId).getActualClientId();
        return plantsRepository.getAll(clientId);
    }

    public Plant getById(long id) {
        if(id == 0) {
            return null;
        }
        return plantsRepository.getById(id);
    }

    public void setLastUpdate(Plant plant) {
        plant.setLastUpdate(LocalDate.now());
        plantsRepository.save(plant);
    }
}
