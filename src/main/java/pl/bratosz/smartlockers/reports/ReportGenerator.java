package pl.bratosz.smartlockers.reports;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.bratosz.smartlockers.date.DateComparator;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.*;
import pl.bratosz.smartlockers.model.orders.*;
import pl.bratosz.smartlockers.service.exels.SpreadSheetWriter;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.*;
import static pl.bratosz.smartlockers.model.orders.OrderStatus.OrderStage.*;
import static pl.bratosz.smartlockers.reports.ReportGenerator.ColumnDataType.*;
import static pl.bratosz.smartlockers.service.exels.SpreadSheetWriter.*;
import static pl.bratosz.smartlockers.service.exels.SpreadSheetWriter.Style.SMALL_BOLDED;
import static pl.bratosz.smartlockers.service.exels.SpreadSheetWriter.Style.VERY_SMALL;

public class ReportGenerator {
    private SpreadSheetWriter writer;
    private TreeSet<Employee> employees;
    private List<MainOrder> mainOrders;
    private TreeMap<ClothToOrder, ArticleAmounts> articlesAmounts;
    private HashSet<MainOrder> reportedOrders;

    public ReportGenerator(Set<Employee> employees) {
        this.employees = new TreeSet<>(employees);
        this.mainOrders = new LinkedList<>();
        this.writer = new SpreadSheetWriter("Raport");
        articlesAmounts = new TreeMap<>();
        reportedOrders = new HashSet<>();
    }

    public XSSFWorkbook generate() {
        createGeneralReportSheet();
        createGeneralReportSheetSortedByOrderTypes();
        createDetailedReportSheetAndCountClothes();
        createSummarySheet();
        return writer.getWorkbook();
    }

    public XSSFWorkbook generateForNewEmployees() {
        createGeneralReportForEmployeesToRelease();
        createGeneralReportSheetForPython();
        createDetailedReportSheetAndCountClothes();
        createSummarySheet();
        createSheetWithBoxesNumbers();
        return writer.getWorkbook();
    }


    public XSSFWorkbook generateForEmployeesWithClothQuantities() {
        createGeneralReportForEmployeesWithClothQuantities();
        return writer.getWorkbook();
    }

    private void createGeneralReportSheetForPython() {
        writer.createSheet("Raport do wczytania");
        createGeneralReportHeaderWithNewEmployeesToLoad(SMALL_BOLDED);
        writeGeneralOrdersToRowsWithNewEmployeesToLoad();
    }

    private void writeGeneralOrdersToRowsWithNewEmployeesToLoad() {
        writer.nextRow();
        int i = 1;
        for (Employee e : employees) {
            TreeSet<MainOrder> sortedOrders = new TreeSet<>(getActiveSortedMainOrders(e));
            for (MainOrder o : sortedOrders) {
                if (o.isActive() && !o.isReported()) {
                    writeNewEmployeeToLoad(e, i);
                    reportedOrders.add(o);
                    writeMainOrderToLoad(o);
                    writer.nextRow();
                }
            }
            i++;
        }
    }

    private void createGeneralReportForEmployeesToRelease() {
        createGeneralReportHeaderWithNewEmployeesToLoad(SMALL_BOLDED);
        writeGeneralOrdersToRowsForEmployeesToRelease();
        writer.formatSheet();
    }

    private void createGeneralReportForEmployeesWithClothQuantities() {
        createGeneralReportHeaderForEmployeesWithClothQuantities(SMALL_BOLDED);
        writeEmployeesWithClothQuantities();
        writer.formatSheet();
    }

    private void createGeneralReportSheet() {
        createGeneralReportHeader(SMALL_BOLDED);
        writeGeneralOrdersToRowsWithSkippingEmployeeIfRepeats();
        writer.formatSheet();
    }

    private void createGeneralReportSheetSortedByOrderTypes() {
        writer.createSheet("Zamówienia");
        createGeneralReportHeader(SMALL_BOLDED);
        getActiveSortedMainOrders();
        mainOrders.forEach(o -> writeMainOrderWithEmployee(o));
        writer.formatSheet();
    }

    private List<MainOrder> getActiveSortedMainOrders() {
        for (Employee e : employees) {
            getActiveSortedMainOrders(e).forEach(o -> mainOrders.add(o));
        }
        Collections.sort(mainOrders, Comparator.comparing(MainOrder::getOrderType)
                .thenComparing(MainOrder::getEmployee)
                .thenComparing(MainOrder::getOrderType));
        return mainOrders;
    }

    private void writeGeneralOrdersToRowsWithSkippingEmployeeIfRepeats() {
        writer.nextRow();
        for (Employee e : employees) {
            System.out.println(e.getLastName());
            writeEmployeeWithNameAndLastNameInOneCell(e);
            TreeSet<MainOrder> sortedOrders = new TreeSet<>(getActiveSortedMainOrders(e));
            for (MainOrder o : sortedOrders) {
                if (o.isActive() && !o.isReported()) {
                    reportedOrders.add(o);
                    writeMainOrder(o);
                    writer.nextRow();
                }
            }
        }
    }


    private void createDetailedReportSheetAndCountClothes() {
        writer.createSheet("Raport szczegółowy");
        createDetailedReportHeader(SMALL_BOLDED);
        writeDetailedOrdersToRows();
        writer.formatSheet();
    }

    private void createSheetWithBoxesNumbers() {
        writer.createSheet("Numery szafek");
        createEmployeeDataHeader(SMALL_BOLDED);
        for (Employee e : employees) {
            writer.nextRow();
            writeEmployeeWithNameAndLastNameInOneCell(e);
        }
    }

    private void writeGeneralOrdersToRowsForEmployeesToRelease() {
        writer.nextRow();
        int i = 0;
        for (Employee e : employees) {
            i++;
            writeEmployee(e);
            TreeSet<MainOrder> sortedOrders = new TreeSet<>(getActiveSortedMainOrders(e));
            for (MainOrder o : sortedOrders) {
                if (o.isActive() && !o.isReported()) {
                    reportedOrders.add(o);
                    writeMainOrderForEmployeeToRelease(o, i);
                    writer.nextRow();
                }
            }
        }
    }

    private void writeEmployeesWithClothQuantities() {
        writer.nextRow();
        int i = 0;
        for (Employee e : employees) {
            i++;
            Collections.sort(e.getClothes());
            Map<Article, Integer> clothesQuantities = countClothes(e.getClothes());
            for (Map.Entry<Article, Integer> entry : clothesQuantities.entrySet()) {
                Article article = entry.getKey();
                Integer quantity = entry.getValue();
                writer.set(ORDINAL_NUMBER, i);
                writeEmployee(e);
                writeArticleWithQuantity(article, quantity);
                writer.nextRow();
            }
        }
    }

    private void writeArticleWithQuantity(Article article, Integer quantity) {
        writer.set(ACTUAL_ARTICLE_NUMBER, article.getNumber());
        writer.set(ACTUAL_ARTICLE_NAME, article.getName());
        writer.set(QUANTITY, quantity);
    }

    private Map<Article, Integer> countClothes(List<Cloth> clothes) {
        Map<Article, Integer> clothesQuantities = new LinkedHashMap<>();
        for (Cloth c : clothes) {
            Article article = c.getClientArticle().getArticle();
            if (clothesQuantities.containsKey(article)) {
                Integer integer = clothesQuantities.get(article);
                integer += 1;
                clothesQuantities.put(article, integer);
            } else {
                clothesQuantities.put(article, 1);
            }
        }
        return clothesQuantities;
    }

    private void writeGeneralOrdersToRows() {
        writer.nextRow();
        for (Employee e : employees) {
            writeEmployeeWithNameAndLastNameInOneCell(e);
            TreeSet<MainOrder> sortedOrders = new TreeSet<>(getActiveSortedMainOrders(e));
            for (MainOrder o : sortedOrders) {
                if (o.isActive() && !o.isReported()) {
                    reportedOrders.add(o);
                    writeMainOrder(o);
                    writer.nextRow();
                }
            }
        }
    }

    private void writeDetailedOrdersToRows() {
        for (Employee e : employees) {
            TreeSet<MainOrder> sortedOrders = new TreeSet<>(getActiveSortedMainOrders(e));
            for (MainOrder o : sortedOrders) {
                if (o.isActive() && !o.isReported()) {
                    for (ClothOrder clothOrder : o.getClothOrders()) {
                        if (clothOrder.isActive()) {
                            writer.nextRow();
                            writeEmployeeWithNameAndLastNameInOneCell(e);
                            writeOrderParameters(clothOrder);
                            writeClothParameters(clothOrder);
                            countCloth(clothOrder);
                        }
                    }
                }
            }
        }
    }

    private List<MainOrder> getActiveSortedMainOrders(Employee e) {
        return e.getMainOrders()
                .stream()
                .filter(MainOrder::isActive)
                .collect(Collectors.toList());
    }

    private void createSummarySheet() {
        writer.createSheet("Podsumowanie zamówienia");
        createSummaryHeader(SMALL_BOLDED);
        AtomicInteger previousArticleNumber = new AtomicInteger(0);
        articlesAmounts.forEach((article, amounts) -> {
            writer.nextRow();
            int actualArticleNumber = article.getArticle().getNumber();
            if (previousArticleNumber.get() == 0) {
                previousArticleNumber.set(actualArticleNumber);
            }
            if (itIsNextArticle(previousArticleNumber.get(), actualArticleNumber)) {
                writer.nextRow();
                previousArticleNumber.set(actualArticleNumber);
            }
            writeClothToOrderWithAmount(article, amounts);
        });
        writer.formatSheet();
    }

    private boolean itIsNextArticle(int previousArticleNumber, int actualArticleNumber) {
        if (previousArticleNumber != actualArticleNumber) {
            return true;
        } else {
            return false;
        }
    }

    private void countCloth(ClothOrder clothOrder) {
        ClothToOrder cloth = new ClothToOrder(clothOrder.getClothToRelease());
        if (articlesAmounts.containsKey(cloth)) {
            ArticleAmounts articleAmount = articlesAmounts.get(cloth);
            articleAmount.countClothBy(clothOrder);
            articlesAmounts.replace(
                    cloth, articleAmount);
        } else {
            articlesAmounts.put(cloth, new ArticleAmounts(clothOrder));
        }
    }

    private void writeMainOrderForEmployeeToRelease(MainOrder order, int ordinalNumber) {
        ClientArticle desiredArticle = order.getDesiredClientArticle();
        Article article = desiredArticle.getArticle();
        writer.set(ORDINAL_NUMBER, ordinalNumber);
        writer.set(DESIRED_ARTICLE_NUMBER, article.getNumber());
        writer.set(DESIRED_SIZE, order.getDesiredSize().getName());
        writer.set(LENGTH_MODIFICATION, getLengthModification(order));
        writer.set(DESIRED_CLOTH, VERY_SMALL, article.getName());
        writer.set(BADGE_NUMBER, desiredArticle.getBadge().getNumber());
        writer.set(QUANTITY, order.getClothOrders().size());
    }

    private void writeMainOrderToLoad(MainOrder o) {
        ClientArticle clientArticle = o.getDesiredClientArticle();
        writer.set(DESIRED_ARTICLE_NUMBER, clientArticle.getArticle().getNumber());
        writer.set(DESIRED_CLOTH, SMALL_BOLDED, clientArticle.getArticle().getName());
        writer.set(DESIRED_SIZE, o.getDesiredSize().getName());
        writer.set(LENGTH_MODIFICATION, getLengthModification(o));
        writer.set(BADGE_NUMBER, clientArticle.getBadge().getNumber());
        writer.set(QUANTITY, o.getClothOrders().size());
    }

    private void writeMainOrderWithEmployee(MainOrder order) {
        writer.nextRow();
        writeEmployee(order.getEmployee());
        writeMainOrder(order);
    }

    private void writeMainOrder(MainOrder order) {
        writer.set(REFERS_TO_CLOTHES, getClothOrdinalNumbersToExchange(order));
        writer.set(ORDER_TYPE, order.getOrderType().getName());
        writer.set(DESIRED_SIZE, getSizeWithLengthModification(order));
        writer.set(DESIRED_CLOTH, order.getDesiredClientArticle()
                .getArticle().getName());
        writer.set(DESIRED_ARTICLE_NUMBER, order.getDesiredClientArticle()
                .getArticle().getNumber());
        writer.set(CLOTHES_TO_EXCHANGE, getClothesToRelease(order));
        writer.set(CLOTHES_DEPRECIATED, getOrdinalNumbersOfDepreciatedClothes(order));
        writer.set(ColumnDataType.EMPTY, "    -    ");
        if (order.getOrderType().equals(OrderType.CHANGE_ARTICLE)) {
            writer.set(ACTUAL_ARTICLE_NAME, order.getPreviousClientArticle()
                    .getArticle().getName());
            writer.set(ACTUAL_ARTICLE_NUMBER, order.getPreviousClientArticle()
                    .getArticle().getNumber());
        }
    }

    private String getSizeWithLengthModification(MainOrder order) {
        String result = order.getDesiredSize().getName();
        String lengthModification = getLengthModification(order);
        return result + " " + lengthModification;
    }

    private String getLengthModification(MainOrder order) {
        if (order.getLengthModification().getLength() != 0) {
            return order.getLengthModification().toString();
        } else {
            return "";
        }
    }

    private String getClothOrdinalNumbersToExchange(MainOrder order) {
        List<Integer> ordinalNumbers = new LinkedList<>();
        Set<ClothOrder> clothOrders = order.getClothOrders();
        for (ClothOrder c : clothOrders) {
            if (c.isActive()) {
                int ordinalNumber = c.getClothToExchange().getOrdinalNumber();
                ((LinkedList<Integer>) ordinalNumbers).push(ordinalNumber);
            }
        }
        return convertOrdinalNumbersToString(ordinalNumbers);

    }

    private String convertOrdinalNumbersToString(List<Integer> ordinalNumbers) {
        ordinalNumbers = ordinalNumbers.stream().sorted().collect(Collectors.toList());
        String result = "";
        for (Integer i : ordinalNumbers) {
            result = result + i + ", ";
        }
        return result;
    }

    private String getClothesToRelease(MainOrder mainOrder) {
        List<Integer> ordinalNumbers = new LinkedList<>();
        Set<ClothOrder> clothOrders = mainOrder.getClothOrders();
        for (ClothOrder order : clothOrders) {
            OrderStage orderStage = order.getOrderStatus().getOrderStage();
            if (isClothOrderActiveAndNotReported(order, orderStage)) {
                if(clothIsReturned(order.getClothToExchange())) {
                    int ordinalNumber = order.getClothToExchange().getOrdinalNumber();
                    ((LinkedList<Integer>) ordinalNumbers).push(ordinalNumber);
                }
            }
        }
        return convertOrdinalNumbersToString(ordinalNumbers);
    }

    private boolean clothIsReturned(Cloth cloth) {
        return cloth.getLifeCycleStatus().equals(LifeCycleStatus.ACCEPTED);
    }

    private String getOrdinalNumbersOfDepreciatedClothes(MainOrder mainOrder) {
        List<Integer> ordinalNumbers = new LinkedList<>();
        Set<ClothOrder> clothOrders = mainOrder.getClothOrders();
        for (ClothOrder order : clothOrders) {
            OrderStage orderStage = order.getOrderStatus().getOrderStage();
            if(isClothOrderActiveAndNotReported(order, orderStage)) {
                if(clothIsDeprecated(order.getClothToExchange())) {
                    int ordinalNumber = order.getClothToExchange().getOrdinalNumber();
                    ordinalNumbers.add(ordinalNumber);
                }
            }
        }
        return convertOrdinalNumbersToString(ordinalNumbers);
    }

    private boolean isClothOrderActiveAndNotReported(ClothOrder order, OrderStage orderStage) {
        return (!order.isReported() && order.isActive())
                && (orderStage.equals(READY_FOR_REALIZATION) ||
                        orderStage.equals(READY_BUT_PENDING_FOR_ASSIGNMENT) ||
                        orderStage.equals(PENDING_FOR_ASSIGNMENT));
    }


    private int writeClothToOrderWithAmount(ClothToOrder cloth, ArticleAmounts amounts) {
        writer.set(DESIRED_CLOTH, SMALL_BOLDED, cloth.article.getName());
        writer.set(DESIRED_ARTICLE_NUMBER, cloth.article.getNumber());
        writer.set(DESIRED_SIZE, cloth.size.getName());
        if (cloth.modification.equals(LengthModification.NONE)) {
            writer.set(LENGTH_MODIFICATION, "0");
        } else {
            writer.set(LENGTH_MODIFICATION, cloth.modification.toString());
        }
        writer.set(AMOUNT_CLOTHES_TO_RELEASE, amounts.getArticlesToRelease());
        writer.set(AMOUNT_CLOTHES_DEPRECIATED, amounts.getArticlesDepreciated());
        writer.set(AMOUNT_CLOTHES_TO_RETURN, amounts.getArticlesToReturn());
        writer.set(AMOUNT_SUM_OF_ALL_CLOTHES, amounts.getArticlesSum());
        return cloth.article.getNumber();
    }

    private void writeClothParameters(ClothOrder c) {
        writer.set(ORDINAL_ARTICLE_NUMBER, c.getClothToExchange().getOrdinalNumber());
        if (c.getClothToExchange().getLifeCycleStatus().equals(LifeCycleStatus.ACCEPTED)) {
            writer.set(IS_RETURNED, "Tak");
        } else {
            writer.set(IS_RETURNED, "Nie");
        }
        if (clothIsDeprecated(c.getClothToExchange())) {
            writer.set(IS_DEPRECATED, "Tak");
        } else {
            writer.set(IS_DEPRECATED, "Nie");
        }
        writer.set(BARCODE, String.valueOf(c.getClothToExchange().getBarcode()));
    }

    private void writeEmployee(Employee e) {
        writer.set(DEPARTMENT, SMALL_BOLDED, e.getDepartment().getName());
        writer.set(PLANT, e.getBox().getLocker().getPlant().getPlantNumber());
        writer.set(LOCKER, e.getBox().getLocker().getLockerNumber());
        writer.set(BOX, e.getBox().getBoxNumber());
        if (writer.present(FIRST_NAME) && writer.present(LAST_NAME)) {
            writer.set(FIRST_NAME, SMALL_BOLDED, e.getFirstName());
            writer.set(LAST_NAME, SMALL_BOLDED, e.getLastName());
        } else {
            writer.set(EMPLOYEE, SMALL_BOLDED, e.getLastName() + " " + e.getFirstName());
        }
    }

    private void writeEmployeeWithNameAndLastNameInOneCell(Employee e) {
        writer.set(DEPARTMENT, SMALL_BOLDED, e.getDepartment().getName());
        writer.set(PLANT, e.getBox().getLocker().getPlant().getPlantNumber());
        writer.set(LOCKER, e.getBox().getLocker().getLockerNumber());
        writer.set(BOX, e.getBox().getBoxNumber());
        writer.set(EMPLOYEE, SMALL_BOLDED, e.getLastName() + " " + e.getFirstName());
    }

    private void writeNewEmployeeToLoad(Employee e, int ordinalNumber) {
        writer.set(ORDINAL_NUMBER, ordinalNumber);
        writer.set(DEPARTMENT, SMALL_BOLDED, e.getDepartment().getName());
        writer.set(PLANT, e.getBox().getLocker().getPlant().getPlantNumber());
        writer.set(LOCKER, e.getBox().getLocker().getLockerNumber());
        writer.set(BOX, e.getBox().getBoxNumber());
        writer.set(FIRST_NAME, SMALL_BOLDED, e.getFirstName());
        writer.set(LAST_NAME, SMALL_BOLDED, e.getLastName());
    }


    private void writeOrderParameters(ClothOrder c) {
        writer.set(ACTUAL_ARTICLE_NAME, SMALL_BOLDED, c.getClothToExchange().getClientArticle()
                .getArticle().getName());
        writer.set(ACTUAL_ARTICLE_NUMBER, c.getClothToExchange().getClientArticle()
                .getArticle().getNumber());
        writer.set(ACTUAL_SIZE, c.getClothToExchange().getSize().getName());
        writer.set(ORDER_TYPE, c.getOrderType().getName());
        writer.set(ARROWS, ">>>");
        writer.set(DESIRED_CLOTH, SMALL_BOLDED, c.getClientArticle().getArticle().getName());
        writer.set(DESIRED_ARTICLE_NUMBER, c.getClientArticle().getArticle().getNumber());
        writer.set(DESIRED_SIZE, c.getSize().getName());
    }

    private boolean clothIsDeprecated(Cloth cloth) {
        LocalDate releaseDate = cloth.getReleaseDate();
        if (releaseDate == null) {
            return false;
        } else {
            int depreciationPeriod = cloth.getClientArticle().getDepreciationPeriod();
            return DateComparator
                    .isDateOlderThanPeriodOfMonths(releaseDate, depreciationPeriod);
        }
    }

    private void createSummaryHeader(Style style) {
        Map<ColumnDataType, Integer> columnIndexes = new HashMap<>();
        columnIndexes.put(DESIRED_CLOTH, 0);
        columnIndexes.put(DESIRED_ARTICLE_NUMBER, 1);
        columnIndexes.put(DESIRED_SIZE, 2);
        columnIndexes.put(LENGTH_MODIFICATION, 3);
        columnIndexes.put(AMOUNT_CLOTHES_TO_RELEASE, 4);
        columnIndexes.put(AMOUNT_CLOTHES_DEPRECIATED, 5);
        columnIndexes.put(AMOUNT_CLOTHES_TO_RETURN, 6);
        columnIndexes.put(AMOUNT_SUM_OF_ALL_CLOTHES, 7);
        writer.addHeader(columnIndexes, style);
    }

    private void createEmployeeDataHeader(Style style) {
        Map<ColumnDataType, Integer> columnIndexes = new HashMap<>();
        columnIndexes.put(DEPARTMENT, 0);
        columnIndexes.put(PLANT, 1);
        columnIndexes.put(LOCKER, 2);
        columnIndexes.put(BOX, 3);
        columnIndexes.put(EMPLOYEE, 4);
        writer.addHeader(columnIndexes, style);
    }

    private void createGeneralReportHeaderWithNewEmployeesToLoad(Style style) {
        Map<ColumnDataType, Integer> colIndexes = new HashMap<>();
        colIndexes.put(ORDINAL_NUMBER, 0);
        colIndexes.put(PLANT, 1);
        colIndexes.put(DEPARTMENT, 2);
        colIndexes.put(FIRST_NAME, 3);
        colIndexes.put(LAST_NAME, 4);
        colIndexes.put(LOCKER, 5);
        colIndexes.put(BOX, 6);
        colIndexes.put(DESIRED_ARTICLE_NUMBER, 7);
        colIndexes.put(DESIRED_CLOTH, 8);
        colIndexes.put(DESIRED_SIZE, 9);
        colIndexes.put(LENGTH_MODIFICATION, 10);
        colIndexes.put(BADGE_NUMBER, 11);
        colIndexes.put(QUANTITY, 12);
        writer.addHeader(colIndexes, style);
    }

    private void createGeneralReportHeaderForEmployeesWithClothQuantities(Style style) {
        Map<ColumnDataType, Integer> colIndexes = new HashMap<>();
        colIndexes.put(ORDINAL_NUMBER, 0);
        colIndexes.put(PLANT, 1);
        colIndexes.put(DEPARTMENT, 2);
        colIndexes.put(FIRST_NAME, 3);
        colIndexes.put(LAST_NAME, 4);
        colIndexes.put(LOCKER, 5);
        colIndexes.put(BOX, 6);
        colIndexes.put(ACTUAL_ARTICLE_NUMBER, 7);
        colIndexes.put(ACTUAL_ARTICLE_NAME, 8);
        colIndexes.put(QUANTITY, 9);
        writer.addHeader(colIndexes, style);
    }

    private void createGeneralReportHeader(Style style) {
        Map<ColumnDataType, Integer> columnIndexes = new HashMap<>();
        columnIndexes.put(ORDINAL_NUMBER, 0);
        columnIndexes.put(DEPARTMENT, 1);
        columnIndexes.put(PLANT, 2);
        columnIndexes.put(LOCKER, 3);
        columnIndexes.put(BOX, 4);
        columnIndexes.put(EMPLOYEE, 5);
        columnIndexes.put(REFERS_TO_CLOTHES, 6);
        columnIndexes.put(ORDER_TYPE, 7);
        columnIndexes.put(DESIRED_SIZE, 8);
        columnIndexes.put(DESIRED_CLOTH, 9);
        columnIndexes.put(DESIRED_ARTICLE_NUMBER, 10);
        columnIndexes.put(CLOTHES_TO_EXCHANGE, 11);
        columnIndexes.put(CLOTHES_DEPRECIATED, 12);
        columnIndexes.put(ColumnDataType.EMPTY, 13);
        columnIndexes.put(ACTUAL_ARTICLE_NAME, 14);
        columnIndexes.put(ACTUAL_ARTICLE_NUMBER, 15);
        writer.addHeader(columnIndexes, style);
    }

    private void createDetailedReportHeader(Style style) {
        Map<ColumnDataType, Integer> headersWithIndexes = new HashMap<>();
        headersWithIndexes.put(DEPARTMENT, 0);
        headersWithIndexes.put(PLANT, 1);
        headersWithIndexes.put(LOCKER, 2);
        headersWithIndexes.put(BOX, 3);
        headersWithIndexes.put(EMPLOYEE, 4);
        headersWithIndexes.put(ACTUAL_ARTICLE_NAME, 5);
        headersWithIndexes.put(ACTUAL_ARTICLE_NUMBER, 6);
        headersWithIndexes.put(ACTUAL_SIZE, 7);
        headersWithIndexes.put(ORDINAL_ARTICLE_NUMBER, 8);
        headersWithIndexes.put(ORDER_TYPE, 9);
        headersWithIndexes.put(ARROWS, 10);
        headersWithIndexes.put(DESIRED_CLOTH, 11);
        headersWithIndexes.put(DESIRED_ARTICLE_NUMBER, 12);
        headersWithIndexes.put(DESIRED_SIZE, 13);
        headersWithIndexes.put(IS_RETURNED, 14);
        headersWithIndexes.put(IS_DEPRECATED, 15);
        headersWithIndexes.put(BARCODE, 16);
        writer.addHeader(headersWithIndexes, style);
    }

    public enum ColumnDataType {
        DEPARTMENT("Oddział"),
        PLANT("Zakład"),
        LOCKER("Szafa"),
        BOX("Box"),
        EMPLOYEE("Pracownik"),
        ACTUAL_ARTICLE_NAME("Obecne ubranie"),
        ACTUAL_ARTICLE_NUMBER("Nr art."),
        ACTUAL_SIZE("Rozmiar"),
        ORDINAL_NUMBER("Lp."),
        REFERS_TO_CLOTHES("Dotyczy sztuk"),
        ORDER_TYPE("Typ zlecenia"),
        DESIRED_CLOTH("Ubranie do wydania"),
        DESIRED_ARTICLE_NUMBER("Nr artykułu"),
        DESIRED_SIZE("Rozmiar"),
        IS_RETURNED("Czy oddana"),
        IS_DEPRECATED("Czy zamortyzowana"),
        BARCODE("Kod kreskowy"),
        ARROWS(""),
        LENGTH_MODIFICATION("Modyfikacja długości"),
        AMOUNT_CLOTHES_TO_RELEASE("Do wydania"),
        AMOUNT_CLOTHES_DEPRECIATED("Zamortyzowane"),
        AMOUNT_CLOTHES_TO_RETURN("Do zwrotu"),
        AMOUNT_SUM_OF_ALL_CLOTHES("Razem"),
        CLOTHES_TO_EXCHANGE("Zwrócone sztuki"),
        CLOTHES_DEPRECIATED("Zamortyzowane sztuki"),
        EMPTY(""),
        QUANTITY("Ilość"),
        BADGE_NUMBER("Nr emblematu"),
        FIRST_NAME("Imię"),
        LAST_NAME("Nazwisko"),
        ORDINAL_ARTICLE_NUMBER("Egz.");

        private String name;

        ColumnDataType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private class ArticleAmounts {
        private int articlesToRelease;
        private int articlesDepreciated;
        private int articlesToReturn;
        private int articlesSum;

        public ArticleAmounts(ClothOrder clothOrder) {
            resolveClothDestinationAndIncrementAppropriateCounter(clothOrder);
        }

        public void countClothBy(ClothOrder clothOrder) {
            resolveClothDestinationAndIncrementAppropriateCounter(clothOrder);
        }

        private void resolveClothDestinationAndIncrementAppropriateCounter(ClothOrder clothOrder) {
            if (clothIsToRelease(clothOrder)) {
                incrementToRelease();
            } else if (clothIsDeprecated(clothOrder.getClothToExchange())) {
                incrementDepreciated();
            } else {
                incrementToReturn();
            }
        }

        private boolean clothIsToRelease(ClothOrder clothOrder) {
            LifeCycleStatus lifeCycleStatus = clothOrder.getClothToExchange().getLifeCycleStatus();
            ExchangeStrategy exchangeStrategy = clothOrder.getExchangeStrategy();
            if (exchangeStrategy == ExchangeStrategy.PIECE_FOR_PIECE &&
                    lifeCycleStatus != LifeCycleStatus.ACCEPTED) {
                return false;
            } else {
                return true;
            }
        }

        private void incrementToRelease() {
            articlesToRelease++;
            articlesSum++;
        }

        private void incrementDepreciated() {
            articlesDepreciated++;
            articlesSum++;
        }

        private void incrementToReturn() {
            articlesToReturn++;
            articlesSum++;
        }

        public int getArticlesToRelease() {
            return articlesToRelease;
        }

        public int getArticlesDepreciated() {
            return articlesDepreciated;
        }

        public int getArticlesToReturn() {
            return articlesToReturn;
        }

        public int getArticlesSum() {
            return articlesSum;
        }


    }

    private class ClothToOrder implements Comparable<ClothToOrder> {
        private Article article;
        private ClothSize size;
        private LengthModification modification;

        public ClothToOrder(Cloth c) {
            article = c.getClientArticle().getArticle();
            size = c.getSize();
            modification = c.getLengthModification();
        }


        public Article getArticle() {
            return article;
        }

        public void setArticle(Article article) {
            this.article = article;
        }

        public ClothSize getSize() {
            return size;
        }

        public void setSize(ClothSize size) {
            this.size = size;
        }

        public LengthModification getModification() {
            return modification;
        }

        public void setModification(LengthModification modification) {
            this.modification = modification;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClothToOrder)) return false;
            ClothToOrder that = (ClothToOrder) o;
            return Objects.equals(article, that.article) &&
                    size == that.size &&
                    modification == that.modification;
        }

        @Override
        public int hashCode() {
            return Objects.hash(article, size, modification);
        }

        @Override
        public int compareTo(ClothToOrder o) {
            return Comparator.comparing(ClothToOrder::getArticle)
                    .thenComparing(ClothToOrder::getSize)
                    .thenComparing(ClothToOrder::getModification)
                    .compare(this, o);
        }
    }

    public Set<MainOrder> getReportedOrders() {
        return reportedOrders;
    }
}
