package pl.bratosz.smartlockers.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.users.ManagementList;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.model.users.UserOurStaff;
import pl.bratosz.smartlockers.repository.ClientRepository;
import pl.bratosz.smartlockers.repository.UsersOurStaffRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.StandardResponse;

import javax.persistence.NonUniqueResultException;

@Service
public class UserService {
    private UsersRepository usersRepository;
    private UsersOurStaffRepository usersOurStaffRepository;
    private EmployeeService employeeService;
    private ManagementListService managementListService;
    private ClientRepository clientRepository;

    public UserService(UsersRepository usersRepository,
                       UsersOurStaffRepository usersOurStaffRepository,
                       @Lazy EmployeeService employeeService,
                       ManagementListService managementListService, ClientRepository clientRepository) {
        this.usersRepository = usersRepository;
        this.usersOurStaffRepository = usersOurStaffRepository;
        this.employeeService = employeeService;
        this.managementListService = managementListService;
        this.clientRepository = clientRepository;
    }

    public StandardResponse putClientToUser(int plantNumber, long userId) {
        Client client;
        try{
            client = clientRepository.getByPlantNumber(plantNumber);
        } catch (NonUniqueResultException e) {
            return new StandardResponse("Coś poszło nie tak. Skontaktuj się z dostawcą oprogramowania.");
        }
        if(client == null) return new StandardResponse("Nie znaleziono klienta z tym numerem zakładu.");
        UserOurStaff user = usersOurStaffRepository.getById(userId);
        user.setActualClientId(client.getId());
        usersOurStaffRepository.save(user);
        return StandardResponse.createForSucceed("Wczytano klienta: " + client.getName(), client);
    }

    public UserOurStaff create(String firstName, String lastName) {
        UserOurStaff user = new UserOurStaff();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setInitialStageForOrders(
                OrderStatus.OrderStage.PENDING_FOR_ASSIGNMENT);
        user.setManagementList(
                managementListService.create());
        return usersRepository.save(user);
    }

    public User getUserById(long id) {
        return usersRepository.getById(id);
    }

    public UserOurStaff getOurUserById(long id) {
        return usersOurStaffRepository.getById(id);
    }

    public User getDefaultUser() {
        User defaultUser = usersRepository.getUserByFirstName("default");
        if (defaultUser == null) {
            return create("default", "user1");
        } else {
            return defaultUser;
        }
    }

    public StandardResponse addEmployeeToManagementList(
            long employeeId,
            long userId) {
        Employee employee = employeeService.getById(employeeId);
        UserOurStaff user = usersOurStaffRepository.getById(userId);
        if(addToManagementList(employee, user)) {
            return StandardResponse.createForSucceed("Dodano do listy", employee);
        } else {
            return StandardResponse.createForFailure(
                    "Lista zawiera pracowników innego klienta. \n" +
                            "Żeby go dodać lista musi być pusta");
        }

    }

    private boolean addToManagementList(
            Employee employeeToManage,
            UserOurStaff user) {
        ManagementList managementList = user.getManagementList();
        long clientEmployeeId = employeeToManage.getDepartment().getClient().getId();
        if (managementList.getEmployees().isEmpty()) {
            managementList.addEmployee(employeeToManage);
            managementList.setActualClient(clientEmployeeId);
            user.setManagementList(managementList);
            usersOurStaffRepository.save(user);
            return true;
        } else if (clientEmployeeId == managementList.getActualClient()) {
            managementList.addEmployee(employeeToManage);
            user.setManagementList(managementList);
            usersOurStaffRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public ManagementList getManagementList(long userId) {
        ManagementList managementList = usersOurStaffRepository
                .getById(userId)
                .getManagementList();
        return managementList.calculateRedemptionPrices();
    }
}
