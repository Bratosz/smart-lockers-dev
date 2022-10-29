package pl.bratosz.smartlockers.scraping;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.date.LocalDateConverter;
import pl.bratosz.smartlockers.exception.EmptyElementException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.clothes.*;

import pl.bratosz.smartlockers.service.ClientArticleService;
import pl.bratosz.smartlockers.service.update.EmployeesAndClothes;
import pl.bratosz.smartlockers.service.update.SimpleCloth;
import pl.bratosz.smartlockers.utils.string.MyString;
import pl.bratosz.smartlockers.utils.Utils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static pl.bratosz.smartlockers.scraping.TableHeader.*;
import static pl.bratosz.smartlockers.scraping.TableSelector.BODY;
import static pl.bratosz.smartlockers.scraping.TableSelector.HEADER;

@Service
public class Scrapper {
    private ClientArticleService clientArticleService;
    private OnlineConnection connection;
    private String login;
    private String password;
    private Elements elements;
    private Elements headerRow;
    private Elements mainBody;
    private Elements secondaryBody;
    private TableSelector loadedColumnIndexesTableType;
    private Map<TableHeader, Integer> columnIndexes;

    public Scrapper(ClientArticleService clientArticleService) {
        this.clientArticleService = clientArticleService;
        login = "";
        password = "";
        columnIndexes = new HashMap<>();
    }

    public void createConnection(Plant plant) {
        String login = plant.getLogin();
        String password = plant.getPassword();
        if (itIsSamePlant(login, password) && connection != null) {
            try {
                connection.checkConnection();

            } catch (IOException e) {
                updateActualLoginData(login, password);
                connection = new OnlineConnection(login, password);
            }
        } else {
            updateActualLoginData(login, password);
            connection = new OnlineConnection(login, password);
        }
    }

    public Element getRow() {
        return mainBody.get(0);
    }

    private void updateActualLoginData(String login, String password) {
        this.login = login;
        this.password = password;
    }

    private boolean itIsSamePlant(String login, String password) {
        if (this.login.equals(login) && this.password.equals(password)) return true;
        return false;
    }

    public void find(int lockerNumber, int boxNumber) {
        searchByLockerNoAndBoxNo(lockerNumber, boxNumber);
    }

    public void goTo(Box box) {
        int lockerNo = box.getLocker().getLockerNumber();
        int boxNo = box.getBoxNumber();
        searchByLockerNoAndBoxNo(lockerNo, boxNo);
    }

    public void goTo(String lastName, Box box) {
        int lockerNo = box.getLocker().getLockerNumber();
        int boxNo = box.getBoxNumber();
        searchBy(lastName, lockerNo, boxNo);
    }


    public void find(Locker locker) {
        findLocker(locker.getLockerNumber());
    }


    public void findLocker(int lockerNumber) {
        searchByLockerNumber(lockerNumber);
    }

    private void searchByLockerNoAndBoxNo(Integer lockerNo, Integer boxNo) {
        putLockerNoAndBoxNoToForm(lockerNo.toString(), boxNo.toString());
        connection.standardPost();
    }

    private void searchBy(String lastName, Integer lockerNo, Integer boxNo) {
        putToForm(lastName, lockerNo.toString(), boxNo.toString());
        connection.standardPost();
    }


    private void searchByLockerNumber(Integer lockerNumber) {
        putLockerNumberToForm(lockerNumber.toString());
        connection.standardPost();
    }


    public String getDepartmentName() {
        return MyString.create(connection.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > " +
                        "tr:nth-child(2) > td:nth-child(1)").text()).get();
    }

    public String getEmployeeLastName() {
        return MyString.create(connection.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > " +
                        "tr:nth-child(2) > td:nth-child(3)").text()).get();
    }

    public String getEmployeeFirstName(int rowIndex) {
        Element boxRow = mainBody.get(rowIndex);
        return MyString.create(boxRow.select("td").get(1).text()).get();
    }

    public String getEmployeeLastName(int rowIndex) {
        Element boxRow = mainBody.get(rowIndex);
        return MyString.create(boxRow.select("td").get(2).text()).get();
    }

    public String getDepartmentName(int rowIndex) {
        Element boxRow = mainBody.get(rowIndex);
        return MyString.create(boxRow.select("td").get(0).text()).get();
    }

    public String getEmployeeFirstName() {
        return MyString.create(connection.getActualPage().select(
                "#ctl00_MainContent_GridView102 > tbody > " +
                        "tr:nth-child(2) > td:nth-child(2)").text()).get();
    }


    public List<Cloth> getClothes(
            Elements clothesRows,
            Client client) {
        List<Cloth> clothes = new LinkedList<>();
        for (int i = 0; i < clothesRows.size(); i++) {
            Elements td = clothesRows.get(i).select("td");
            LocalDate release = getRelease(td);
            Cloth cloth = new Cloth(
                    getBarcode(td),
                    getAssignment(td),
                    getLastWashingDate(td),
                    release,
                    getOrdinalNo(td),
                    getArticleByArticleNumber(td, client),
                    getSize(td),
                    getLengthModification(td),
                    getLifeCycleStatus(release));
            clothes.add(cloth);
        }
        return clothes;
    }

    private LifeCycleStatus getLifeCycleStatus(LocalDate release) {
        if (release.compareTo(LocalDateConverter.convert(new Date(0))) == 0) {
            return LifeCycleStatus.BEFORE_RELEASE;
        } else {
            return LifeCycleStatus.IN_ROTATION;
        }
    }

    public int getBoxNumberByTableRow(int rowIndex) {

        return getBoxNumber(rowIndex);
    }

    public Elements getMainTableBody() {
        return mainBody;
    }

    public List<Integer> getBoxNumbers(Elements boxesRows) {
        List<Integer> boxNumbers = new LinkedList<>();
        for (int i = 1; i < boxesRows.size(); i++) {
            boxNumbers.add(
                    getBoxNumber(i));
        }
        return boxNumbers;
    }

    public void clickViewButton() {
        int buttonIndex = 0;
        clickViewButton(buttonIndex);
    }

    public void clickViewButtonByRowIndex(int rowIndex) {
        int buttonIndex = rowIndex - 1;
        clickViewButton(buttonIndex);
    }

    public void clickViewButton(int buttonIndex) {
        updateFormWithClick(buttonIndex);
        connection.standardPost();
    }

    public void clickViewButton(Element row) {
        int buttonIndex = Integer.valueOf(Utils
                .substringBetween(row.toString(), "Select$", "')"));
        clickViewButton(buttonIndex);
    }

    public SimpleEmployee getEmployee(Element boxRow) {
        int lockerNumber = getLockerNumber(boxRow);
        int boxNumber = getBoxNumber(boxRow);
        String firstName = getFirstName(boxRow);
        String lastName = getLastName(boxRow);
        String departmentName = getDepartmentName(boxRow);
        return new SimpleEmployee(
                firstName, lastName, lockerNumber, boxNumber, departmentName);
    }

    private void updateFormWithClick(int buttonIndex) {
        connection.formParameters.put("__EVENTTARGET", "ctl00$MainContent$GridView102");
        connection.formParameters.put("__EVENTARGUMENT", "Select$" + buttonIndex);
    }


    private void putLockerNoAndBoxNoToForm(String lockerNo, String boxNo) {
        connection.formParameters.clear();
        connection.formParameters.put("ctl00$MainContent$szafa_akt", lockerNo);
        connection.formParameters.put("ctl00$MainContent$box_akt", boxNo);
        connection.formParameters.put("__EVENTTARGET", "ctl00$MainContent$szafa_akt");
    }

    private void putToForm(String lastName, String lockerNo, String boxNo) {
        connection.formParameters.clear();
        connection.formParameters.put("ctl00$MainContent$nazwisko_akt", lastName);
        connection.formParameters.put("ctl00$MainContent$szafa_akt", lockerNo);
        connection.formParameters.put("ctl00$MainContent$box_akt", boxNo);
        connection.formParameters.put("__EVENTTARGET", "ctl00$MainContent$szafa_akt");
    }


    private void putLockerNumberToForm(String lockerNumber) {
        connection.setFormParameters(new LinkedHashMap<>());
        connection.formParameters.put("__EVENTTARGET", "ctl00$MainContent$szafa_akt");
        connection.formParameters.put("ctl00$MainContent$szafa_akt", lockerNumber);
    }

    private int getOrdinalNo(Elements td) {
        return Integer.parseInt(td.get(columnIndexes.get(ORDINAL_NUMBER)).text());
    }

    private ClientArticle getArticleByArticleNumber(Elements td, Client client) {
        int articleNumber = Integer.parseInt(td.get(columnIndexes.get(ARTICLE_NUMBER)).text());
        String articleName = getArticleName(td);
        return clientArticleService.get(articleNumber, client, articleName);
    }

    private String getArticleName(Elements td) {
        return td.get(columnIndexes.get(ARTICLE_NAME)).text();
    }

    private ClothSize getSize(Elements td) {
        String stringSize = getStringSize(td);
        return StringsForClothExtractor.getSizeFromString(stringSize);
    }

    private LengthModification getLengthModification(Elements td) {
        String stringSize = getStringSize(td);
        return StringsForClothExtractor.getModificationFromString(stringSize);
    }

    private String getStringSize(Elements td) {
        return MyString.create(
                td.get(columnIndexes.get(SIZE)).text()).get();
    }

    private LocalDate getAssignment(Elements td) {
        String date = td.get(columnIndexes.get(ASSIGNMENT)).text();
        return LocalDateConverter.getDateFrom(date);
    }

    private long getBarcode(Elements td) {
        return Long.parseLong(td
                .get(columnIndexes.get(BARCODE)).text());
    }

    private LocalDate getRelease(Elements td) {
        String date = td.get(columnIndexes.get(RELEASE)).text();
        return LocalDateConverter.getDateFrom(date);
    }

    private LocalDate getLastWashingDate(Elements td) {
        String date = td.get(columnIndexes.get(LAST_WASHING)).text();
        return LocalDateConverter.getDateFrom(date);
    }

    private Integer getBoxNumber(int rowIndex) {
        String boxNumber = mainBody.get(rowIndex)
                .select("td").get(columnIndexes.get(BOX_NUMBER)).text();
        return Integer.valueOf(boxNumber);
    }

    public int getLockerNumber(Element row) {
        return Integer.valueOf(row.select("td")
                .get(columnIndexes.get(LOCKER_NUMBER)).text());
    }

    public String getComment(Element row) {
        return MyString.create(row
                .select("td")
                .get(columnIndexes.get(COMMENT)).text())
                .get();
    }

    public LocalDate getAssignment(Element row) {
        String date = row.select("td")
                .get(columnIndexes.get(ASSIGNMENT)).text();
        return LocalDate.parse(date);
    }

    public LocalDate getLastWashing(Element row) {
        String date = row.select("td")
                .get(columnIndexes.get(LAST_WASHING)).text();
        return LocalDate.parse(date);
    }

    public Date getLastWashingDate(Element row) {
        String date = row.select("td")
                .get(columnIndexes.get(LAST_WASHING)).text();
        return LocalDateConverter.getDate(date);
    }

    public long getBarcode(Element row) {
        return Long.valueOf(row.select("td")
                .get(columnIndexes.get(BARCODE)).text());
    }

    public int getBoxNumber(Element row) {
        return Integer.valueOf(row.select("td")
                .get(columnIndexes.get(BOX_NUMBER)).text());
    }

    public void goToBoxesView() {
        connection.goToBoxesView();
    }

    public void goToClothesView() {
        connection.goToClothesView();
    }

    private void loadColumnIndexes(TableSelector selector) {
        loadedColumnIndexesTableType = selector;
        Integer index = 0;
        for (Element header : headerRow) {
            Integer finalIndex = index;
            String s = MyString.create(header.text()).get();
            List<TableHeader> tableHeaders = Arrays.asList(TableHeader.values());
            tableHeaders
                    .stream()
                    .filter(h -> h.getNames().contains(s))
                    .findFirst()
                    .ifPresent(h ->
                            columnIndexes.put(h, finalIndex));
            index++;
        }
    }

    public String getFirstName(Element row) {
        return MyString.create(row.select("td")
                .get(columnIndexes.get(FIRST_NAME)).text()).get();
    }

    public String getLastName(Element row) {
        return MyString.create(row.select("td")
                .get(columnIndexes.get(LAST_NAME)).text()).get();
    }



    public String getDepartmentName(Element row) {
        return MyString.create(row
                .select("td")
                .get(columnIndexes.get(DEPARTMENT)).text()).get();
    }

    public String getLastName(Elements boxTable) {
        return MyString.create(
                boxTable.select("td")
                        .get(columnIndexes
                        .get(LAST_NAME)).text())
                .get();
    }

    public String getFirstName(Elements boxTable) {
        return MyString.create(
                boxTable.select("td")
                        .get(columnIndexes
                                .get(FIRST_NAME)).text())
                .get();
    }

    private Elements getHeaderRow(TableSelector selector) {
        return connection.actualPage.select(selector.get() + HEADER.get());
    }

    private Elements getBody(TableSelector selector) {
        return connection.actualPage.select(selector.get() + BODY.get());
    }

    public Elements getAllRowsFromTableAndAssignAsMainTable(TableSelector tableSelector) {
        headerRow = getHeaderRow(tableSelector);
        if(!loadedColumnIndexesAreActual(tableSelector)) {
            loadColumnIndexes(tableSelector);
        }
        return mainBody = getBody(tableSelector);
    }

    public EmployeesAndClothes convertRows(List<Element> clothesRows) {
        Stack<SimpleEmployee> employees = new Stack<>();
        Stack<SimpleCloth> clothes = new Stack<>();
        SimpleEmployee employee;
        SimpleCloth cloth;
        for(Element c : clothesRows) {
            employee = new SimpleEmployee(
                    getFirstName(c),
                    getLastName(c),
                    getLockerNumber(c),
                    getBoxNumber(c),
                    getDepartmentName(c));
            cloth = new SimpleCloth(
                    getBarcode(c),
                    getAssignment(c),
                    getLastWashing(c));
            clothes.push(cloth);
            if(employees.empty())
                employees.push(employee);
            if(employees.peek().equals(employee)) {
                employee = employees.peek();
                employee.addCloth(cloth);
            } else {
                employee.addCloth(cloth);
                employees.push(employee);
            }
        }
        return new EmployeesAndClothes(employees, clothes);
    }

    public Elements getAllRowsAndSelectAsSecondaryTable(TableSelector selector) throws EmptyElementException {
        secondaryBody = getBody(selector);
        if(secondaryBody.size() == 0) {
            throw new EmptyElementException("There is no table");
        }
        if (!loadedColumnIndexesAreActual(selector)) {
            headerRow = getHeaderRow(selector);
            loadColumnIndexes(selector);
        }
        return secondaryBody = getBody(selector);
    }

    private boolean loadedColumnIndexesAreActual(TableSelector selector) {
        if(loadedColumnIndexesTableType != null) {
            return loadedColumnIndexesTableType.equals(selector);
        } else {
            return false;
        }
    }
}
