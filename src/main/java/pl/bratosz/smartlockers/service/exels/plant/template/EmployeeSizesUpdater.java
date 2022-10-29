package pl.bratosz.smartlockers.service.exels.plant.template;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import pl.bratosz.smartlockers.exception.MyException;
import pl.bratosz.smartlockers.model.clothes.ClothSize;
import pl.bratosz.smartlockers.service.exels.plant.template.data.*;
import pl.bratosz.smartlockers.utils.MyXLSX;

import java.util.Map;

import static pl.bratosz.smartlockers.utils.MyXLSX.*;

public class EmployeeSizesUpdater {
    private PlantDataContainer dataContainer;
    private Map<String, TemplateEmployee> employees;
    private static final SheetTypeForPlantLoad sheetType = SheetTypeForPlantLoad.EMPLOYEES_AND_SIZES;


    public EmployeeSizesUpdater(PlantDataContainer dataContainer) {
        this.dataContainer = dataContainer;
        this.employees = dataContainer.getEmployees();
    }

    public void update(MySheet mySheet) throws MyException {
        XSSFSheet sheet = mySheet.getSheet();
        XSSFRow row;
        TemplateEmployee employee;
        for (int rowIndex = 1; rowIndex <= mySheet.getLastRowIndex(); rowIndex++) {
            row = sheet.getRow(rowIndex);
            employee = getEmployee(row, rowIndex);
            TemplateArticle article = getArticle(row, rowIndex, employee);
            TemplateClothSize size = getSize(row, rowIndex);
            employee.addArticleWithSize(
                    article,
                    size);
        }
    }

    private TemplateClothSize getSize(XSSFRow row, int rowIndex) throws MyException {
        String size = getCellValue(row.getCell(4));
        TemplateClothSize templateClothSize = new TemplateClothSize(size);
        if(templateClothSize.getClothSize().equals(ClothSize.SIZE_UNKNOWN)) {
            throw new MyException("W arkuszu: " + sheetType.getName() +
                    " w wierszu numer: " + rowIndex + " podano nieprawidłowy rozmiar");
        } else {
            return templateClothSize;
        }

    }

    private TemplateArticle getArticle(XSSFRow row, int rowIndex, TemplateEmployee employee) throws MyException {
        int articleNumber = getIntCellValue(row.getCell(3));
        TemplateArticle templateArticle = dataContainer.getArticles().get(articleNumber);
        if (templateArticle == null) throw new MyException(
                "Brak arykułu o numerze: " + articleNumber +
                        ". " + sheetType.getName() + " wiersz nr " + rowIndex);
        if(articleBelongsToEmployeesPosition(templateArticle, employee.getPosition())) {
            return templateArticle;
        } else {
            throw new MyException("Stanowisko: " + employee.getPosition().getName() +
                    " pracownika " + employee.getPersonalNumber() +
                    " " + employee.getLastName() + " " + employee.getFirstName() +
                    " nie ma przypisanego tego artykułu: " + templateArticle.getArticleName());
        }
    }

    private boolean articleBelongsToEmployeesPosition(
            TemplateArticle templateArticle,
            TemplatePosition position) {
        return position.getArticlesWithQuantities()
                .containsKey(templateArticle);
    }

    private TemplateEmployee getEmployee(XSSFRow row, int rowIndex) throws MyException {
        String personalNumber = getCellValue(row.getCell(0));
        TemplateEmployee employee = employees.get(personalNumber);
        if (employee == null) {
            throw new MyException("Na liście pracowników w arkuszu PRACOWNICY brak" +
                    " pracownika o nr personalnym " + personalNumber +
                    ". Sprawdź arkusz " + sheetType.getName() +
                    ", wiersz nr " + rowIndex);
        } else {
            return employee;
        }
    }
}
