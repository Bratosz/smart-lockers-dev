package pl.bratosz.smartlockers.service.pasting;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.BoxNotAvailableException;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Position;
import pl.bratosz.smartlockers.repository.ClientRepository;
import pl.bratosz.smartlockers.repository.EmployeesToCreateRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.DepartmentService;
import pl.bratosz.smartlockers.service.EmployeeService;
import pl.bratosz.smartlockers.service.LocationService;
import pl.bratosz.smartlockers.service.PositionService;
import pl.bratosz.smartlockers.service.pasting.employee.EmployeeToCreate;
import pl.bratosz.smartlockers.service.pasting.employee.EmployeeToCreateData;
import pl.bratosz.smartlockers.service.pasting.employee.PastedEmployeeEDPL;
import pl.bratosz.smartlockers.utils.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CreatingEmployeeService {
    private UsersRepository usersRepository;
    private ClientRepository clientRepository;
    private DepartmentService departmentService;
    private LocationService locationService;
    private PositionService positionService;
    private EmployeesToCreateRepository employeesToCreateRepository;
    private EmployeeService employeeService;

    public CreatingEmployeeService(UsersRepository usersRepository, ClientRepository clientRepository, DepartmentService departmentService, LocationService locationService, PositionService positionService, EmployeesToCreateRepository employeesToCreateRepository, EmployeeService employeeService) {
        this.usersRepository = usersRepository;
        this.clientRepository = clientRepository;
        this.departmentService = departmentService;
        this.locationService = locationService;
        this.positionService = positionService;
        this.employeesToCreateRepository = employeesToCreateRepository;
        this.employeeService = employeeService;
    }

    public StandardResponse add(long userId, List<PastedEmployeeEDPL> employees) {
        long clientId = usersRepository.getActualClientId(userId);
        Client client = clientRepository.getById(clientId);
        employees.forEach(e -> createEmployeeToCreate(e, client));
        return StandardResponse.createForSucceed();
    }

    private void createEmployeeToCreate(PastedEmployeeEDPL e, Client c) {
        Department d = departmentService.getByAliasAndClientId(e.getDepartment(), c.getId());
        Location l = locationService.getByNameAndClientId(e.getLocation(), c.getId());
        Position p = positionService.get(e.getPosition(), c);
        EmployeeToCreate employeeToCreate = EmployeeToCreate.createWithDepartmentPositionAndLocation(e, d, l, p, c);
        employeesToCreateRepository.save(employeeToCreate);
    }

    public StandardResponse setDepartmentPositionAndLocation(
            long departmentId, long positionId, long locationId, Set<Long> employeesIds) {
        Department d = departmentService.getById(departmentId);
        Position p = positionService.getById(positionId);
        Location l = locationService.getById(locationId);
        Set<EmployeeToCreate> emps =
                employeesIds.stream()
                .map(id -> employeesToCreateRepository.getOne(id))
                .collect(Collectors.toSet());
        if(Utils.haveSameClient(emps, d, p, l)) {
            emps.stream()
                    .forEach(e -> update(e, d, p, l));
            return StandardResponse.createForSucceed("Zaktualizowano pracowników");
        } else {
            return StandardResponse.createForFailure("Multiple clients exception - skontaktuj " +
                    "się z dostawcą oprogramowania");
        }
    }

    private EmployeeToCreate update(EmployeeToCreate e, Department d, Position p, Location l) {
        e.setDepartment(d);
        e.setPosition(p);
        e.setLocation(l);
        return employeesToCreateRepository.save(e);
    }

    public StandardResponse createTheEmployees(List<EmployeeToCreateData> employeesData) {
        List<EmployeeToCreate> employeesToCreate = update(employeesData);
        employeesToCreate.forEach(e -> create(e));
        return StandardResponse.createForSucceed();
    }

    private void create(EmployeeToCreate e) {
        try {
            employeeService.createEmployee(e);
            deleteEmployeeToCreate(e);
        } catch (BoxNotAvailableException ex) {
            ex.printStackTrace();
        }
    }

    public StandardResponse deleteEmployeesToCreate(List<Long> employeesIds, long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        employeesIds.forEach(e -> employeesToCreateRepository.deleteById(e));
        return StandardResponse.createForSucceed(employeesToCreateRepository.getAllByClient(clientId));
    }

    private void deleteEmployeeToCreate(EmployeeToCreate e) {
        employeesToCreateRepository.deleteById(e.getId());
    }

    private List<EmployeeToCreate> update(List<EmployeeToCreateData> employeesData) {
        EmployeeToCreate e;
        Department d;
        Position p;
        Location l;
        List<EmployeeToCreate> employees = new LinkedList<>();
        for(EmployeeToCreateData etc : employeesData) {
            e = employeesToCreateRepository.getById(etc.getEmployeeId());
            d = departmentService.getById(etc.getDepartmentId());
            p = positionService.getById(etc.getPositionId());
            l = locationService.getById(etc.getLocationId());
            if(Utils.haveSameClient(e, d, p, l)) {
                e = update(e, d, p, l);
                employees.add(e);
            } else {
                throw new RuntimeException("Client have to be the same");
            }
        }
        return employees;
    }

    public void swapNames(long employeeId) {
        EmployeeToCreate e = employeesToCreateRepository.getById(employeeId);
        String firstName = e.getFirstName();
        String lastName = e.getLastName();
        e.setFirstName(lastName);
        e.setLastName(firstName);
        employeesToCreateRepository.save(e);
    }
}

