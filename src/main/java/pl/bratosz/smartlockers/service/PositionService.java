package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.repository.*;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.exels.plant.template.data.TemplatePosition;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PositionService {
    private PositionsRepository positionsRepository;
    private DepartmentService departmentService;
    private ClientArticleService clientArticleService;
    private ClothTypesWithQuantitiesRepository clothTypesWithQuantitiesRepository;
    private UsersRepository usersRepository;
    private EmployeesRepository employeesRepository;
    private ClientRepository clientRepository;

    public PositionService(
            PositionsRepository positionsRepository,
            DepartmentService departmentService,
            ClientArticleService clientArticleService,
            ClothTypesWithQuantitiesRepository clothTypesWithQuantitiesRepository,
            UsersRepository usersRepository,
            EmployeesRepository employeesRepository, ClientRepository clientRepository) {
        this.positionsRepository = positionsRepository;
        this.departmentService = departmentService;
        this.clientArticleService = clientArticleService;
        this.clothTypesWithQuantitiesRepository = clothTypesWithQuantitiesRepository;
        this.usersRepository = usersRepository;
        this.employeesRepository = employeesRepository;
        this.clientRepository = clientRepository;
    }

    public StandardResponse create(
            String name,
            long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Client client = clientRepository.getById(clientId);
        Position position = create(name, client);
        return StandardResponse.createForSucceed(
                "Dodano stanowisko",
                position);
    }

    private Position createSurrogate(Client c) {
        String n = MyString.create("ZASTĘPCZE").get();
        Position p = new Position(n, c, true);
        return positionsRepository.save(p);
    }

    public Position create(
            String name,
            Client client) {
        name = MyString.create(name).get();
        Position position = new Position(name, client);
        return positionsRepository.save(position);
    }

    public Position create(TemplatePosition templatePosition, Client client) {
        Set<Department> departments = departmentService.get(templatePosition.getDepartments(), client.getId());
        Position position = create(templatePosition.getName(), client);
        return addDepartments(position, departments);
    }

    public List<Position> createPositions(List<TemplatePosition> templatePositions, Client client) {
        List<Position> positions = new ArrayList<>();
        templatePositions.forEach(zp -> positions.add(
                create(zp, client)));
        return positions;
    }

    public StandardResponse addDepartments(
            long departmentId,
            long positionId) {
        Department department = departmentService.getById(departmentId);
        Position position = positionsRepository.getById(positionId);
        position = addDepartment(position, department);
        return StandardResponse.createForSucceed(
                "Dodano oddział",
                position);
    }

    public Position addDepartment(Position position, Department department) {
        position.addDepartment(department);
        return positionsRepository.save(position);
    }

    public Position addDepartments(Position position, Set<Department> departments) {
        position.setDepartments(departments);
        return positionsRepository.save(position);
    }

    public Set<Position> get(long userId) {
        long clientId = usersRepository.getById(userId).getActualClientId();
        return positionsRepository.getByClientId(clientId);
    }

    public Position get(String positionName, Client c) {
        Position p = positionsRepository.getByClientIdAndName(c, positionName);
        if (p == null) {
            return getSurrogate(c);
        } else {
            return p;
        }
    }

    private Position getSurrogate(Client c) {
        Position p = positionsRepository.getByClientAndSurrogate(c, true);
        if (p == null) {
            return createSurrogate(c);
        } else {
            return p;
        }
    }

    public Set<Position> getAllByDepartment(long departmentId) {
        Department department = departmentService.getById(departmentId);
        return department.getPositions();
    }

    public StandardResponse addArticleWithQuantity(
            long clientArticleId,
            int quantity,
            long positionId) {
        Position position = positionsRepository.getById(positionId);
        ClientArticle article = clientArticleService.getById(clientArticleId);
        ArticleWithQuantity ctq = new ArticleWithQuantity(article, quantity);
        ctq.setPosition(position);
        ctq = clothTypesWithQuantitiesRepository.save(ctq);
        position.addClothTypeWithQuantity(ctq);
        position = positionsRepository.save(position);
        return StandardResponse.createForSucceed(
                "Dodano artykuł wraz z  ilością",
                position);
    }

    public StandardResponse addAnotherArticle(
            long clientArticleId,
            long clothWithQuantityId,
            long positionId) {
        Position position = positionsRepository.getById(positionId);
        ClientArticle article = clientArticleService.getById(clientArticleId);
        ArticleWithQuantity ctq = getClothWithQuantity(position, clothWithQuantityId);
        ctq.addArticle(article);
        position = clothTypesWithQuantitiesRepository.save(ctq).getPosition();
        return StandardResponse.createForSucceed(
                "Dodano dodatkowy rodzaj odzieży do wyboru",
                position);

    }

    private ArticleWithQuantity getClothWithQuantity(
            Position position,
            long clothWithQuantityId) {
        return position.getArticlesWithQuantities()
                .stream()
                .filter(ctq -> ctq.getId() == clothWithQuantityId)
                .findFirst()
                .get();
    }


    public StandardResponse delete(
            long positionId) {
        Position position = positionsRepository.getById(positionId);
        delete(position.getArticlesWithQuantities());
        removeAssignedPositionFromEmployees(position.getEmployees());
        positionsRepository.deleteById(positionId);
        return StandardResponse.createForSucceed("Usunięto stanowisko: " + position.getName(),
                position);
    }

    public Position getRotational(Plant plant) {
        String rotationalPositionName = "ROTACJA";
        return positionsRepository.getByClientIdAndName(plant.getClient(), rotationalPositionName);
    }

    private void delete(Set<ArticleWithQuantity> clothTypesWithQuantities) {
        for (ArticleWithQuantity ctq : clothTypesWithQuantities) {
            clothTypesWithQuantitiesRepository.deleteById(ctq.getId());
        }
    }

    private void removeAssignedPositionFromEmployees(Set<Employee> employees) {
        for (Employee emp : employees) {
            emp.setPosition(null);
            employeesRepository.save(emp);
        }
    }

    public Position getById(long positionId) {
        return positionsRepository.getById(positionId);
    }
}
