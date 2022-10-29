package pl.bratosz.smartlockers.service.update;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.exception.MultipleBoxException;
import pl.bratosz.smartlockers.exception.EmptyElementException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.users.User;
import pl.bratosz.smartlockers.repository.ClothesRepository;
import pl.bratosz.smartlockers.scraping.Scrapper;

import java.util.*;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.scraping.TableSelector.*;

@Service
public class ScrapingService {
    private ClothesRepository clothesRepository;
    private Scrapper scrapper;
    private User user;




    public ScrapingService(ClothesRepository clothesRepository, Scrapper scrapper
    ) {
        this.clothesRepository = clothesRepository;
        this.scrapper = scrapper;
    }

    public void loadBox(Element row) {
        scrapper.clickViewButton(row);
    }

    public void loadBox(Box box) {
        scrapper.find(
                box.getLocker().getLockerNumber(),
                box.getBoxNumber());
        scrapper.clickViewButton();
    }

    public void loadBox() {
        scrapper.clickViewButton();
    }

    public List<Cloth> getClothes(
            Employee employee) throws EmptyElementException {
        Client client = employee
                .getBox().getLocker().getPlant().getClient();
        return getClothes(client);
    }

    public List<Cloth> getClothes(Box box) throws EmptyElementException {
        Client client = box.getLocker().getPlant().getClient();;
        return getClothes(client);
    }

    private List<Cloth> getClothes(Client client) throws EmptyElementException {
        Elements clothesTable = scrapper
                .getAllRowsAndSelectAsSecondaryTable(TABLE_OF_EMPLOYEE_CLOTHES);
        return scrapper.getClothes(clothesTable, client);
    }

    public List<Cloth> getClothesAsRotational(
            Client client) throws EmptyElementException {
        Elements clothesTable = scrapper
                .getAllRowsAndSelectAsSecondaryTable(TABLE_OF_EMPLOYEE_CLOTHES);
        List<Cloth> clothes = scrapper.getClothes(clothesTable, client);
        clothes.stream()
                .forEach(c -> c.setRotational(true));
        return clothes;
    }

    public void connectToBoxesView(Employee employee) {
        Plant plant = employee.getBox().getLocker().getPlant();
        connectToBoxesView(plant);
    }

    public void connectToBoxesView(Box box) {
        Plant plant = box.getLocker().getPlant();
        connectToBoxesView(plant);
    }

    /**
     * Check status of box by number in klsonline24.pl
     * service
     *
     * @return 1 if employee exist,
     * 0 if box is empty,
     * -1 if there is another
     * employee at that box
     */
    public int checkBoxStatusBy(Employee employee) throws MultipleBoxException {
        Elements boxTable;
        try {
            boxTable = getBoxTableBody();
        } catch (EmptyElementException e) {
            return 0;
        }
        if(employeesAreEqual(employee, boxTable)){
            return 1;
        } else {
            return -1;
        }
    }

    private boolean employeesAreEqual(Employee employee, Elements boxTable) {
        String lastName = scrapper.getLastName(boxTable);
        String firstName = scrapper.getFirstName(boxTable);
        if ((employee.getLastName().equals(lastName)
                && employee.getFirstName().equals(firstName))
                ||
                (employee.getLastName().equals(firstName)
                        && employee.getFirstName().equals(lastName))) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Check status of box by number in klsonline24.pl
     * service
     *
     * @return 1 if employee exist,
     * 0 if box is empty,
     * -1 if there is another
     * employee at that box
     */
    public int checkBoxStatusBy(Box box) throws MultipleBoxException {
        Elements boxTable;
        try {
            boxTable = getBoxTableBody();
        } catch (EmptyElementException e) {
            return 0;
        }
        if(box.getBoxStatus().equals(Box.BoxStatus.FREE)) {
            return 1;
        } else if(box.getBoxStatus().equals(Box.BoxStatus.OCCUPY)){
            if(employeesAreEqual((Employee) box.getEmployee(), boxTable)) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    public void goToEmployeeByLastNameAndBoxNoAndLockerNo(Employee employee) {
        scrapper.goTo(employee.getLastName(),
                employee.getBox());
    }


    public void goTo(Employee employee) {
        scrapper.goTo(employee.getBox());
    }

    public void goTo(Box box) {
        scrapper.goTo(box);
    }

    public SimpleEmployee goToAndGetEmployeeBy(Box box) throws MultipleBoxException {
        Elements boxTable = null;
        try {
            scrapper.goTo(box);
            boxTable = getBoxTableBody();
        } catch (EmptyElementException e) {
            return new SimpleEmployee("", "");
        }
        return createSimpleEmployee(boxTable);
    }

    private SimpleEmployee createSimpleEmployee(Elements boxTableBody) throws MultipleBoxException {
        if (boxTableBody.select("tr").size() > 1) {
            throw new MultipleBoxException();
        } else {
            return getSimpleEmployee(boxTableBody
                    .select("tr")
                    .stream()
                    .findFirst()
                    .get());
        }
    }

    private Elements getBoxTableBody() throws EmptyElementException, MultipleBoxException {
        Elements boxTableBody = scrapper.getAllRowsFromTableAndAssignAsMainTable(TABLE_OF_BOXES);
        if (boxTableBody.size() == 1) {
            return boxTableBody;
        } else if (boxTableBody.size() > 1) {
            List<SimpleEmployee> simpleEmployees = boxTableBody.stream()
                    .map(r -> getSimpleEmployee(r))
                    .collect(Collectors.toList());
            throw new MultipleBoxException(simpleEmployees);
        } else {
            throw new EmptyElementException("Box is empty");
        }
    }

    private boolean boxIsDuplicated(int boxNumber, List<Integer> duplicates) {
        if (duplicates.contains(boxNumber)) {
            return true;
        } else {
            return false;
        }
    }

    public void connectToBoxesView(Plant plant) {
        scrapper.createConnection(plant);
        scrapper.goToBoxesView();
    }

    private void connectToAllClothesView(Plant plant) {
        scrapper.createConnection(plant);
        scrapper.goToClothesView();
    }

    public ClothesAndEmployeesToUpdate getClothesAndEmployeesToUpdate(Plant plant) {
        connectToAllClothesView(plant);
        List<Element> clothesRows = scrapper.getAllRowsFromTableAndAssignAsMainTable(ALL_CLOTHES_TABLE);
        EmployeesAndClothes actualEmployeesAndClothes = scrapper.convertRows(clothesRows);
        List<Cloth> previousClothes = clothesRepository.getActiveSimpleClothesBy(plant);
        ClothesAndEmployeesToUpdateExtractor clothesExtractor =
                new ClothesAndEmployeesToUpdateExtractor(
                        actualEmployeesAndClothes,
                        previousClothes,
                        plant.getLastUpdate());
        return clothesExtractor.getClothesAndEmployeesToUpdate();
    }

    public void goToLocker(int lockerNumber) {
        scrapper.findLocker(lockerNumber);
    }

    public List<Element> getBoxesRows() {
        return scrapper.getAllRowsFromTableAndAssignAsMainTable(TABLE_OF_BOXES);
    }

    public List<Element> getBoxesRowsFromLockersRange(
            int from,
            int to) {
        List<Element> boxesRows = getSortedBoxesRows();
        return getBoxesFromLockersRange(boxesRows, from, to);
    }

    public List<Element> getSortedBoxesRows() {
        scrapper.getAllRowsFromTableAndAssignAsMainTable(TABLE_OF_BOXES);
        return sortBoxesRowsByLockerNumber(
                scrapper.getMainTableBody());
    }

//    private List<SimpleEmployee> updateClothes(Elements clothesRows, Plant plant) {
//        Client client = plant.getClient();
//        List<Cloth> updatedClothes = new LinkedList<>();
//        List<SimpleEmployee> skippedEmployees = new LinkedList<>();
//        Set<SimpleEmployee> doubledBoxes = new HashSet<>();
//        SimpleEmployee actualEmployee;
//        SimpleEmployee previousEmployee = getSimpleEmployee(clothesRows.first());
//        for (Element row : clothesRows) {
//            actualEmployee = getSimpleEmployee(row);
//            if (!(previousEmployee.equals(actualEmployee))
//                    || (row.equals(clothesRows.last()))) {
//                if (row.equals(clothesRows.last())) {
//                    updatedClothes.add(loadCloth(row, client));
//                }
//                try {
//                    Employee employee = employeeService.getBy(previousEmployee, plant);
//                    clothService.updateClothes(updatedClothes, employee, user);
//                } catch (SkippedEmployeeException e) {
//                    skippedEmployees.add(e.goToAndGetEmployeeBy());
//                } catch (MultipleBoxException e) {
//                    doubledBoxes.add(e.goToAndGetEmployeeBy());
//                } finally {
//                    updatedClothes.clear();
//                }
//            } else {
//                updatedClothes.add(loadCloth(row, client));
//            }
//        }
//        return skippedEmployees;
//    }

    public SimpleEmployee getSimpleEmployee() {
        return getSimpleEmployee(
                scrapper.getRow());
    }

    public SimpleEmployee getSimpleEmployee(Element row) {
        String firstName = scrapper.getFirstName(row);
        String lastName = scrapper.getLastName(row);
        String comment = scrapper.getComment(row);
        String departmentAlias = getDepartmentAlias(row);
        int lockerNumber = scrapper.getLockerNumber(row);
        int boxNumber = scrapper.getBoxNumber(row);
        return new SimpleEmployee(
                firstName,
                lastName,
                comment,
                departmentAlias,
                lockerNumber,
                boxNumber);
    }

    public int getLockerNumber(Element row) {
        return scrapper.getLockerNumber(row);
    }

    public int getFirstLockerNumber(List<Element> boxesRows) {
        return scrapper.getLockerNumber(boxesRows.get(0));
    }

    public int getBoxNumber(Element row) {
        return scrapper.getBoxNumber(row);
    }

    public int getLockerCapacity(List<Box> boxes) {
        int highestBoxNumber = boxes
                .stream()
                .mapToInt(b -> b.getBoxNumber())
                .max().orElse(10);
        if (highestBoxNumber <= 10) {
            return 10;
        } else if (highestBoxNumber <= 20) {
            return 20;
        } else if (highestBoxNumber <= 30) {
            return 30;
        } else if (highestBoxNumber <= 40) {
            return 40;
        } else {
            return 1000;
        }
    }

    private List<Element> sortBoxesRowsByLockerNumber(Elements boxesRows) {
        return boxesRows
                .stream()
                .sorted(Comparator.comparing(
                        r -> Integer.parseInt(
                                r.select("td").get(4).text())))
                .collect(Collectors.toList());
    }

    private List<Element> getBoxesFromLockersRange(
            List<Element> boxesRows,
            int from,
            int to) {
        return boxesRows
                .stream()
                .filter(r -> getLockerNumber(r) >= from
                        && getLockerNumber(r) <= to)
                .collect(Collectors.toList());
    }

        private String getDepartmentAlias(Element r) {
        return r.select("td").get(0).text();
    }


}
