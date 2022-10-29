package pl.bratosz.smartlockers.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.date.CurrentDate;
import pl.bratosz.smartlockers.date.LocalDateConverter;
import pl.bratosz.smartlockers.file.FileStorage;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.files.TemplateTypeForPlantLoad;
import pl.bratosz.smartlockers.reports.ReportGenerator;
import pl.bratosz.smartlockers.repository.ClientArticlesRepository;
import pl.bratosz.smartlockers.repository.ClientRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.DownloadFileResponse;
import pl.bratosz.smartlockers.service.exels.ExcelUpdater;
import pl.bratosz.smartlockers.service.exels.template.EmployeesToMeasureWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final String listTemplatePath = "src/main/resources/static/xlsx-templates/employee-list-template.xlsx";
    private EmployeeService employeeService;
    private OrderService orderService;
    private MeasurementListService measurementListService;
    private UsersRepository usersRepository;
    private ClientRepository clientRepository;
    private ClientArticlesRepository clientArticlesRepository;


    public ReportService(EmployeeService employeeService,
                         OrderService orderService,
                         MeasurementListService measurementListService,
                         UsersRepository usersRepository,
                         ClientRepository clientRepository, ClientArticlesRepository clientArticlesRepository) {
        this.employeeService = employeeService;
        this.orderService = orderService;
        this.measurementListService = measurementListService;
        this.usersRepository = usersRepository;
        this.clientRepository = clientRepository;
        this.clientArticlesRepository = clientArticlesRepository;
    }

    public DownloadFileResponse generateAllOrdersReport(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Set<Employee> employees = employeeService.getEmployeesWithActiveOrdersThatAreNotReported(clientId);
        return generate(employees);
    }

    public DownloadFileResponse getTemplateForCreateClient(TemplateTypeForPlantLoad templateType, long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Client client = clientRepository.getById(clientId);
        List<ClientArticle> clientArticles = clientArticlesRepository.getAllBy(clientId);
        if(clientArticles.size() == 0) {
            return new DownloadFileResponse("Żeby pobrać szablon musisz najpierw przypisać artykuły do tego klienta.");
        } else {
            try {
                FileInputStream file = new FileInputStream(new File(templateType.getPath()));
                XSSFWorkbook wb = new XSSFWorkbook(file);
                ExcelUpdater updater = ExcelUpdater.create(wb);
                updater.updateColumn(1, 0, clientArticles, "Stanowiska");
                FileStorage fileStorage = new FileStorage();
                return fileStorage.storeAndGet(wb, client.getName() + " " + templateType.getName());
            } catch (IOException e) {
                return new DownloadFileResponse("Coś poszło nie tak przy próbie pobrania pliku. " +
                        "Skontaktuj się z dostawcą oprogramowania.");
            }
        }
    }

    public DownloadFileResponse getEmployeesWithClothesQuantities(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        System.out.println("1");
        Set<Employee> activeEmployees = employeeService.getAllActiveEmployees(clientId);
        System.out.println("2");
        ReportGenerator generator = new ReportGenerator(activeEmployees);
        System.out.println("3");
        XSSFWorkbook report = generator.generateForEmployeesWithClothQuantities();
        System.out.println("4");
        return storeReport(report, "Zestawienie odzieży", clientId);


    }

    public DownloadFileResponse getEmployeesToMeasureList(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Client client = clientRepository.getById(clientId);
        try {
            FileInputStream file = new FileInputStream(new File(listTemplatePath));
            XSSFWorkbook wb = new XSSFWorkbook(file);
            EmployeesToMeasureWriter writer = new EmployeesToMeasureWriter(wb, 0, 3, 4);
            List<Employee> employeesToMeasure = measurementListService.getEmployeesToMeasure(clientId);
            wb = writer.writeRows(employeesToMeasure);
            FileStorage fileStorage = new FileStorage();
            String fileName = "Pracownicy_do_pomiaru_" + client.getName();
            return fileStorage.storeAndGet(wb, fileName);
        } catch (IOException e) {
            return new DownloadFileResponse("Coś poszło nie tak przy ładowaniu pliku");
        }
    }

    public DownloadFileResponse generateForNotReportedEmployeesToRelease(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Set<Employee> employees = measurementListService.getEmployeesToAssign(clientId);
        employees = employees.stream()
                .filter(e -> !employeeService.isReported(e))
                .collect(Collectors.toSet());
        return generateForNewEmployees(employees, clientId);
    }

    public DownloadFileResponse generateForAllEmployeesToRelease(long userId) {
        long clientId = usersRepository.getActualClientId(userId);
        Set<Employee> employees = measurementListService.getEmployeesToAssign(clientId);
        return generateForNewEmployees(employees, clientId);
    }

    private DownloadFileResponse generate(Set<Employee> employees) {
        ReportGenerator generator = new ReportGenerator(employees);
        XSSFWorkbook workbook = generator.generate();
        FileStorage fileStorage = new FileStorage();
        LocalDate now = LocalDate.now();
        DownloadFileResponse test = fileStorage.storeAndGet(workbook, LocalDateConverter.getDate(now) + " zgłoszenia");
        return test;
    }

    private DownloadFileResponse generateForNewEmployees(Set<Employee> employees, long clientId) {
        ReportGenerator generator = new ReportGenerator(employees);
        XSSFWorkbook workbook = generator.generateForNewEmployees();
        return storeReport(workbook, "Pomierzeni", clientId);
    }

    private DownloadFileResponse storeReport(XSSFWorkbook report, String reportName, long clientId) {
        FileStorage fileStorage = new FileStorage();
        String fileName = reportName + " " + clientRepository.getById(clientId).getName() +
                " " + CurrentDate.createFor("dd-MM-yyyy").get();
        DownloadFileResponse response = fileStorage.storeAndGet(report, fileName);
        return response;
    }




}
