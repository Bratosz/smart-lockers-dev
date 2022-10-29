package pl.bratosz.smartlockers.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bratosz.smartlockers.calculator.CalculateClothesValue;
import pl.bratosz.smartlockers.date.CurrentDateForFiles;
import pl.bratosz.smartlockers.date.LocalDateConverter;
import pl.bratosz.smartlockers.exception.MyException;
import pl.bratosz.smartlockers.model.files.TemplateTypeForPlantLoad;
import pl.bratosz.smartlockers.model.files.TemplateTypeForLoadPlantResolver;
import pl.bratosz.smartlockers.repository.ClientArticlesRepository;
import pl.bratosz.smartlockers.response.StandardResponse;
import pl.bratosz.smartlockers.service.exels.ExcelWriter;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.payload.UploadFileResponse;
import pl.bratosz.smartlockers.service.*;
import pl.bratosz.smartlockers.service.exels.plant.template.data.PlantDataContainer;
import pl.bratosz.smartlockers.service.exels.plant.template.PlantDataFromFileExtractor;
import pl.bratosz.smartlockers.utils.string.MyString;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.service.exels.ExcelWriter.saveWorkbook;

@RestController
@RequestMapping("/file")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private FileStorageService fileStorageService;
    private EmployeeService employeeService;
    private BoxService boxesService;
    private LockerService lockerService;
    private DepartmentService departmentService;
    private PlantService plantService;
    private ClientService clientService;
    private LocationService locationService;
    private ClientArticlesRepository clientArticlesRepository;

    public FileController(FileStorageService fileStorageService, EmployeeService employeeService,
                          BoxService boxesService, LockerService lockerService, FileService fileService,
                          DepartmentService departmentService, PlantService plantService,
                          ClientService clientService, LocationService locationService, ClientArticlesRepository clientArticlesRepository) {
        this.fileStorageService = fileStorageService;
        this.employeeService = employeeService;
        this.boxesService = boxesService;
        this.lockerService = lockerService;
        this.departmentService = departmentService;
        this.plantService = plantService;
        this.clientService = clientService;
        this.locationService = locationService;
        this.clientArticlesRepository = clientArticlesRepository;
    }

//    public FileController(FileStorageService fileStorageService, EmployeeService employeeService,
//                          BoxService boxesService, LockerService lockerService) {
//        this.fileStorageService = fileStorageService;
//        this.employeeService = employeeService;
//        this.boxesService = boxesService;
//        this.lockerService = lockerService;
//    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = null;
        try {
            resource = fileStorageService.loadFileAsResource(fileName);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Try to determineComplete file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determineComplete file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(),
                file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files")
                                                                MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @JsonView(Views.Public.class)
    @PostMapping("/load-new-client/{userId}")
    public StandardResponse loadPlantFromExcelFile(
            @PathVariable long userId,
            @RequestParam("file") MultipartFile loadPlantFile) throws IOException {
        Client client = clientService.getByUser(userId);
        Set<Plant> plants = client.getPlants();
        if(plants.size() == 1) {
            Plant plant = plants.stream().findFirst().get();
            int plantNumber = plant.getPlantNumber();
            return loadPlantByPlantNumberFromExcelFile(plantNumber, userId, loadPlantFile);
        } else {
            return StandardResponse.createForFailure("Ten klient posiada kilka zakładów. Wybierz jeden z nich i spróbuj ponownie");
        }
    }


    @JsonView(Views.Public.class)
    @PostMapping("/load-new-client/{plantNumber}/{userId}")
    public StandardResponse loadPlantByPlantNumberFromExcelFile(
            @PathVariable("plantNumber") int plantNumber,
            @PathVariable("userId") long userId,
            @RequestParam("file") MultipartFile loadPlantFile) throws IOException, IllegalArgumentException {
        Plant plant = plantService.getByNumber(plantNumber);
        List<ClientArticle> clientArticles = clientArticlesRepository.getAllBy(plant.getClient().getId());
        try {
            TemplateTypeForPlantLoad templateType = TemplateTypeForLoadPlantResolver.resolve(loadPlantFile);
            XSSFWorkbook workbook = new XSSFWorkbook(loadPlantFile.getInputStream());
            PlantDataFromFileExtractor plantDataExtractor = PlantDataFromFileExtractor.create(templateType);
            PlantDataContainer plantDataContainer = plantDataExtractor.getInitialPlantData(workbook, clientArticles);
            plant = clientService.createFromFileWithResponse(plantNumber, userId, plantDataContainer);
        } catch (MyException e) {
            return StandardResponse.createForFailure(e.getMessage());
        }
//        return clientService.createFromFileWithResponse(clientName, plantNumber, sheets);
        return StandardResponse.createForSucceed("Utworzono klienta: " + plant.getPlantNumber() + " "
                + MyString.create(plant.getClient().getName()).get(), plant);
    }

//
//        List<Employee> employeeList = new LinkedList<>();
//        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
//            //creating instance of employee from row
//            XSSFRow row = worksheet.getRow(i);
//            int plantNumber = (int) row.getCell(2).getNumericCellValue();
//            String departmentName = row.getCell(3).getStringCellValue();
//            int lockerNumber = (int) row.getCell(4).getNumericCellValue();
//            int boxNumber = (int) row.getCell(5).getNumericCellValue();
//            String firstName = row.getCell(0).getStringCellValue();
//            String lastName = row.getCell(1).getStringCellValue();
//            //adding employee to box
//            Employee employee = employeeService.createEmployee(
//                    plantNumber, departmentName, lockerNumber,
//                    boxNumber, firstName, lastName);
//            employeeList.add(employee);
//        }
//        return employeeList;

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/add_employees/{plantNumber}")
    public List<Employee> addNewEmployeesFromExcelFile(
            @PathVariable int plantNumber,
            @RequestParam("file") MultipartFile newEmployeesFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(newEmployeesFile.getInputStream());
        List<Employee> employeeList = new LinkedList<>();

        Set<Department> departments = plantService.getDepartments(plantNumber);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String departmentFromSheetName = workbook.getSheetName(i).toUpperCase().trim();
            Department department = departmentService.getByNameAndPlantNumber(departmentFromSheetName, plantNumber);
            if (plantService.containsDepartment(departmentFromSheetName, plantNumber)) {
                XSSFSheet worksheet = workbook.getSheetAt(i);
                for (int j = 1; j < worksheet.getPhysicalNumberOfRows(); j++) {
                    XSSFRow row = worksheet.getRow(j);
                    if (row.getCell(1) == null || row.getCell(1).getStringCellValue().trim().length() == 0) {
                        continue;
                    }
                    Integer plantNumberCell = Integer.parseInt(row.getCell(5).getStringCellValue());
                    Set<Integer> plantNumbers = plantService.getAllPlantNumbersOfClient(plantNumber);
                    if (plantNumbers.contains(plantNumberCell)) {
                        continue;
                    }

                    Employee employee = new Employee();
                    employee.setFirstName(row.getCell(2).getStringCellValue().trim().toUpperCase());
                    employee.setLastName(row.getCell(1).getStringCellValue().trim().toUpperCase());
                    employee.setDepartment(department);

                    String locationName = row.getCell(5).getStringCellValue();
                    Location location = locationService.getByNameAndPlantNumber(locationName, plantNumberCell);

                    //creating emploee and assignWithdrawnCloth it to the next free box
                    Employee createdEmployee = employeeService.createEmployeeAndAssignToBox(
                            plantNumberCell, department, location, employee);
                    Box box = createdEmployee.getBox();
                    row.getCell(3).setCellValue(box.getLocker().getLockerNumber());
                    row.getCell(4).setCellValue(box.getBoxNumber());
                    row.getCell(5).setCellValue(box.getLocker().getPlant().getPlantNumber());
                    row.getCell(6).setCellValue(employee.getDepartment().getName());
                    employeeList.add(createdEmployee);
                }
            }
        }
        CurrentDateForFiles date = new CurrentDateForFiles();
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/KLS/raports/" +
                date.getDate() + "/" + date.getDate()
                + "_pomiary.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
        return employeeList;
    }

    @JsonView(Views.InternalForEmployees.class)
    @PostMapping("/dismiss-by-id/{userId}")
    public StandardResponse dismissEmployeesFromFileByID(
            @PathVariable long userId,
            @RequestParam("file") MultipartFile employeesToDelete) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(employeesToDelete.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            long employeeId = (long) sheet.getRow(i).getCell(0).getNumericCellValue();
            employeeService.dismissBy(true, employeeId, userId);
        }
        return StandardResponse.createForSucceed();
    }

    @PostMapping("/reformat")
    public void reformat(@RequestParam("file") MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheetToLoad = workbook.getSheetAt(0);
        Row row;
        List<EmployeeRow> employees = new LinkedList<>();
        int counter;
        XSSFSheet sheet = workbook.createSheet();
        for (int i = 1; i < sheetToLoad.getPhysicalNumberOfRows(); i++) {
            long[] barCodes = new long[5];
            String firstName;
            String lastName;
            Date date;
            counter = 0;
            row = sheetToLoad.getRow(i);
            if (row.getCell(0) == null) {
                continue;
            }
            lastName = row.getCell(0).getStringCellValue().toUpperCase().trim();
            firstName = row.getCell(1).getStringCellValue().toUpperCase().trim();
            date = row.getCell(7).getDateCellValue();
            final String stringDate = LocalDateConverter.getDate(date);
            int j = 2;
            while (j < 7) {
                if (row.getCell(j) == null || row.getCell(j).getStringCellValue().length() < 2) {
                    j++;
                    continue;
                }
                barCodes[counter] = Long.parseLong(row.getCell(j).getStringCellValue().substring(1));
                j++;
                counter++;
            }
            for (int k = 0; k < counter; k++) {
                employees.add(new EmployeeRow(firstName, lastName, barCodes[k], stringDate));
            }
        }
        for (int i = 0; i < employees.size(); i++) {
            XSSFRow createdRow = sheet.createRow(i);
            EmployeeRow employeeRow = employees.get(i);
            createdRow.createCell(0).setCellValue(employeeRow.lastName);
            createdRow.createCell(1).setCellValue(employeeRow.firstName);
            createdRow.createCell(2).setCellValue(employeeRow.barCode);
            createdRow.createCell(3).setCellValue(employeeRow.releaseDate);
        }
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/files_to_testing/Lear/raports/rotacja.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    class EmployeeRow {
        String firstName;
        String lastName;
        long barCode;
        String releaseDate;

        public EmployeeRow(String firstName, String lastName, long barCode, String date) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.barCode = barCode;
            this.releaseDate = date;
        }
    }


    @PostMapping("/load_lockers/{sheetToLoad}")
    public void loadLockersFromExcelFile(@RequestParam("file") MultipartFile lockersToLoad,
                                         @PathVariable int sheetToLoad) throws IOException {
        sheetToLoad = sheetToLoad - 1;
        XSSFWorkbook workbook = new XSSFWorkbook(lockersToLoad.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(sheetToLoad);

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            int plantNumber = (int) row.getCell(3).getNumericCellValue();

            Locker locker = new Locker();
            locker.setLockerNumber((int) row.getCell(1).getNumericCellValue());
            locker.setCapacity((int) row.getCell(2).getNumericCellValue());
            locker.setPlant(plantService.getByNumber(plantNumber));
            locker.setDepartment(departmentService.getByNameAndPlantNumber(row.getCell(4).getStringCellValue(), plantNumber));
            locker.setLocation(locationService.getByNameAndPlantNumber(
                    row.getCell(5).getStringCellValue(), locker.getPlant().getPlantNumber()));

            lockerService.create(locker);
        }
    }

    @JsonView(Views.InternalForLockers.class)
    @GetMapping("/getAllLockersToExcel/{plantNumber}")
    public List<Locker> getAllLockersToExcel(@PathVariable int plantNumber) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Pracownicy");
        List<Locker> all = lockerService.getLockersByPlantNumber(plantNumber);
        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("Imię");
        row.createCell(1).setCellValue("Nazwisko");
        row.createCell(2).setCellValue("Szafa");
        row.createCell(3).setCellValue("Box");
        int rowCounter = 1;
        for (int i = 0; i < all.size(); i++) {
            List<Box> boxes = all.get(i).getBoxes();
            for (int j = 0; j < boxes.size(); j++) {
                if (boxes.get(j).getBoxStatus() == Box.BoxStatus.FREE) {
                    continue;
                }
                row = sheet.createRow(rowCounter++);
                row.createCell(0).setCellValue(boxes.get(j).getEmployee().getFirstName());
                row.createCell(1).setCellValue(boxes.get(j).getEmployee().getLastName());
                row.createCell(2).setCellValue(all.get(i).getLockerNumber());
                row.createCell(3).setCellValue(boxes.get(j).getBoxNumber());
            }
        }
        FileOutputStream fileOut = new FileOutputStream("C:/Users/HP/Desktop/KLS/raports/" + sheet.getSheetName() + ".xlsx");
        wb.write(fileOut);
        fileOut.close();
        wb.close();
        return all;

    }

    @GetMapping("/calculate_clothes_value/{articleColumnNo}/{releaseDateColumnNo}/{resultColumnNo}")
    public Float calculateClothesValueFromExcelFile(@PathVariable Integer articleColumnNo,
                                                    @PathVariable Integer releaseDateColumnNo,
                                                    @PathVariable Integer resultColumnNo,
                                                    @RequestParam("file") MultipartFile clothesToCount) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(clothesToCount.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        Float totalAmount = 0.0f;

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            String articleNumber = String.valueOf((int) row.getCell(articleColumnNo - 1).getNumericCellValue());
            Date releaseDate = row.getCell(releaseDateColumnNo - 1).getDateCellValue();

            Integer clothValue = CalculateClothesValue.calculateValueForCloth(articleNumber, releaseDate);
            row.createCell(resultColumnNo - 1).setCellValue(clothValue);
            totalAmount += clothValue;
        }
        sheet.createRow(sheet.getPhysicalNumberOfRows()).createCell(resultColumnNo).setCellValue(totalAmount);
        saveWorkbook(workbook);
        return totalAmount;
    }

    @JsonView(Views.InternalForEmployees.class)
    @GetMapping("/find-employees")
    public List<Employee> findEmployeesFromExcelFileAndGenerateExcelReport(
            @RequestParam("file") MultipartFile employeesToFind) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(employeesToFind.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        int doubledEmployees = 0;
        List<Employee> employeesToFile = new LinkedList<>();
        List<SimpleEmployee> omittedEmployees = new LinkedList<>();
        String previousFirstName = "";
        String previousLastName = "";

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            XSSFRow row = worksheet.getRow(i);
            if (row.getCell(1) == null) continue;
            String lastName = row.getCell(1).getStringCellValue();
            String firstName = row.getCell(2).getStringCellValue();

            //skip row if person doubles
            if ((previousFirstName == firstName) && (previousLastName == lastName)) {
                doubledEmployees++;
                continue;
            } else {
                previousFirstName = firstName;
                previousLastName = lastName;
            }

            //getClothesAndEmployeesToUpdate all employees with particular name
            List<Employee> employeesFromDB = employeeService.getByFirstNameAndLastName(firstName, lastName);
            //addArticle employees to final list
            employeesFromDB.stream().forEach(employee -> employeesToFile.add(employee));
            //if there is no employee then addArticle it to list with omitted employees
            if (employeesFromDB.size() == 0) {
                omittedEmployees.add(new SimpleEmployee(firstName, lastName));
            }
        }

        List<Employee> sortedEmployees = employeeService.sortByPlantBoxAndLocker(employeesToFile);

        List<String> columnHeaders = new LinkedList<>();
        Row row = worksheet.getRow(0);
        for (Cell cell : row) {
            String cellHeader = cell.getStringCellValue();
            columnHeaders.add(cellHeader);
        }

        String sheetName = worksheet.getSheetName();
        ExcelWriter excelWriter = new ExcelWriter(columnHeaders, sortedEmployees, sheetName);
        XSSFWorkbook excelReportWithEmployees = excelWriter.createExcelRaportWithEmployees();

        XSSFSheet sheet = excelReportWithEmployees.createSheet("Raport");
        sheet.createRow(0).createCell(0).setCellValue("Podwójni:");
        sheet.getRow(0).createCell(1).setCellValue(doubledEmployees);
        sheet.createRow(1).createCell(0).setCellValue("Pominięci:");
        for (int i = 2; i <= omittedEmployees.size() + 1; i++) {
            Row myRow = sheet.createRow(i);
            myRow.createCell(0).setCellValue(omittedEmployees.get(i - 2).getLastName());
            myRow.createCell(1).setCellValue(omittedEmployees.get(i - 2).getFirstName());
        }

        saveWorkbook(excelReportWithEmployees);

        return sortedEmployees;
    }

    private String checkContentType(HttpServletRequest request, Resource resource) {

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    private XSSFSheet getSheetAtFromFile(int index, MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        return workbook.getSheetAt(index);
    }


}