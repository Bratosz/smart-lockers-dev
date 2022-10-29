package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.ClientRepository;
import pl.bratosz.smartlockers.repository.DepartmentsRepository;
import pl.bratosz.smartlockers.repository.PlantsRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.service.managers.DepartmentManager;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.*;

@Service
public class DepartmentService {

    private DepartmentsRepository departmentsRepository;
    private DepartmentAliasService departmentAliasService;
    private ClientRepository clientRepository;
    private UsersRepository usersRepository;
    private PlantsRepository plantsRepository;

    public DepartmentService(DepartmentsRepository departmentsRepository,

                             DepartmentAliasService departmentAliasService,
                             ClientRepository clientRepository, UsersRepository usersRepository, PlantsRepository plantsRepository) {
        this.departmentsRepository = departmentsRepository;
        this.departmentAliasService = departmentAliasService;
        this.clientRepository = clientRepository;
        this.usersRepository = usersRepository;
        this.plantsRepository = plantsRepository;
    }

    public Department getByNameAndPlantNumber(String name, int plantNumber) {
        name = MyString.create(name).get();
        return departmentsRepository.getByNameAndMainPlantNumber(
                name, plantNumber);
    }

    public Department getByNameAndClientId(String name, long clientId) {
        name = MyString.create(name).get();
        return departmentsRepository.getByNameAndClientId(
                name, clientId);
    }

    public Department getByAliasAndClientId(String alias, long clientId) {
        alias = MyString.create(alias).get();
        Department department = departmentsRepository.getByAliasAndClientId(
                alias, clientId);
        if(department == null) {
            return getSurrogateBy(clientId);
        } else {
            return department;
        }
    }



    public CreateResponse createWithResponse(String departmentName, long plantId, long clientId) {
        int mainPlantNumber = plantsRepository.getById(plantId).getPlantNumber();
        Department department = create(departmentName, clientId, mainPlantNumber, false);
        return new CreateResponse(department, "Utworzono oddział");
    }

    public Department create(String departmentName, long clientId, long plantId, boolean surrogate) {
        Client client = clientRepository.getById(clientId);
        Plant plant = plantsRepository.getById(plantId);
        DepartmentAlias alias = departmentAliasService.create(departmentName);
        Department department = new Department(
                departmentName, client, plant, plant.getPlantNumber(), alias, surrogate);
        return departmentsRepository.save(department);
    }

    public Department create(String departmentName,
                             long clientId,
                             int mainPlantNumber,
                             boolean surrogate) {
        long plantId = plantsRepository.getByPlantNumber(mainPlantNumber).getId();
        return create(departmentName, clientId, plantId, surrogate);
    }

    public Department addPlant(long departmentId, int plantNumber) {
        Plant plant = plantsRepository.getByPlantNumber(plantNumber);
        Department department = departmentsRepository.getOne(departmentId);
        department.addPlant(plant);
        return departmentsRepository.save(department);
    }

    public List<Department> getAll(long userId) {
        long clientId = usersRepository.getById(userId).getActualClientId();
        List<Department> all = departmentsRepository.getAll(clientId);
        return all;
    }

    public Department getById(long id) {
        if (id == 0) {
            return null;
        }
        return departmentsRepository.getOne(id);
    }

    public Department getBy(String departmentAlias, Box box) {
        Client client = box.getLocker().getPlant().getClient();
        Set<Department> departments = client.getDepartments();
        for (Department d : departments) {
            if (d.getAliases().stream().anyMatch(
                    alias -> alias.equals(departmentAlias))) {
                return d;
            }
        }
        Optional<Department> departmentDefault = departments.stream().filter(
                d -> d.isSurrogate()).findFirst();
        if (departmentDefault.isPresent()) {
            return departmentDefault.get();
        } else {
            Department department = DepartmentManager.createDefaultDepartment(client);
            return departmentsRepository.save(department);
        }
    }

    public Department addAlias(long departmentId, String alias) {
        Department dep = departmentsRepository.getById(departmentId);
        DepartmentAlias departmentAlias = departmentAliasService.create(alias);
        dep.addAlias(departmentAlias);
        return departmentsRepository.save(dep);
    }

    public Department getSurrogateAndCreateIfNotFound(Client client, int mainPlantNumber) {
        Department surrogate = departmentsRepository.getBySurrogateAndClient(true, client);
        if(surrogate == null) {
            surrogate = create("ZASTĘPCZY", client.getId(), mainPlantNumber, true);
        }
        return surrogate;
    }


    private Department createSurrogate(long clientId) {
        Client c = clientRepository.getById(clientId);
        return create("ZASTĘPCZY", clientId, c.getPlants().stream().findFirst().get().getPlantNumber(), true);

    }


    public Department getSurrogateBy(long clientId) {
        Department d = departmentsRepository.getBySurrogateAndClientId(true, clientId);
        if(d == null) {
            return createSurrogate(clientId);
        } else {
            return d;
        }
    }

    public Location getLocation(Department department, long locationId) {
        return department.getLocations()
                .stream()
                .filter(location -> location.getId() == locationId)
                .findFirst()
                .get();
    }

    public Position getPosition(Department department, long positionId) {
        return department.getPositions()
                .stream()
                .filter(p -> p.getId() == positionId)
                .findFirst()
                .get();
    }

    public Department createDefaultDepartmentsAndReturnMain(Client client, int plantNumber) {
        getSurrogateAndCreateIfNotFound(client, plantNumber);
        return create("GŁÓWNY", client.getId(), plantNumber, false);
    }

    public List<Department> create(List<String> departmentsNames, Client client, int plantNumber) {
        List<Department> departments = departmentsRepository.getAll(client.getId());
        departmentsNames.forEach(name -> {
                    if(departmentNotExist(departments, name)){
                        departments.add(
                                create(name, client.getId(), plantNumber, false));
                    }
                });
        return departments;
    }

    private boolean departmentNotExist(List<Department> departments, String name) {
        return departments.stream()
                .noneMatch(d -> d.getName().equals(name));
    }

    public Department get(String departmentName, long clientId) {
        return departmentsRepository.getByNameAndClientId(departmentName, clientId);
    }

    public Set<Department> get(Set<String> departmentsNames, long clientId) {
        Set<Department> departments = new HashSet<>();
        departmentsNames.forEach(d ->
                departments.add(departmentsRepository.getByNameAndClientId(d, clientId)));
        return departments;
    }
}
