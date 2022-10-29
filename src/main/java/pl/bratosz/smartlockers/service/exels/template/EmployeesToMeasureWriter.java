package pl.bratosz.smartlockers.service.exels.template;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.bratosz.smartlockers.model.ArticleWithQuantity;
import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.service.exels.ColumnAssigner;
import pl.bratosz.smartlockers.service.exels.SpreadSheetWriter;

import java.util.List;
import java.util.Set;

import static pl.bratosz.smartlockers.service.exels.columns.ColumnType.*;

public class EmployeesToMeasureWriter {
    private XSSFWorkbook wb;
    private XSSFSheet sheet;
    private XSSFRow headerRow;
    private XSSFRow templateRow;
    private ColumnAssigner columns;
    private SpreadSheetWriter writer;

    public EmployeesToMeasureWriter(XSSFWorkbook workBookTemplate, int sheetIndex, int headerRowIndex, int templateRowIndex) {
        wb = workBookTemplate;
        sheet = wb.getSheetAt(sheetIndex);
        headerRow  = sheet.getRow(headerRowIndex);
        templateRow = sheet.getRow(templateRowIndex);
        columns = new ColumnAssigner(headerRow);
        writer = new SpreadSheetWriter(workBookTemplate, sheetIndex, templateRowIndex);
    }

    public XSSFWorkbook writeRows(List<Employee> employees) {
        createRows(employees);
        writeEmployeesWithClothes(employees);
        return writer.getWorkbook();
    }

    private void writeEmployeesWithClothes(List<Employee> employees) {
        int ordinalNumber = 1;
        for(Employee e : employees) {
            writeEmployee(e);
            writeArticles(
                    e.getId(),
                    ordinalNumber++,
                    e.getPosition().getArticlesWithQuantities());
        }
    }

    private void writeArticles(
            long employeeId,
            int ordinalNumber,
            Set<ArticleWithQuantity> articlesWithQuantities) {
        articlesWithQuantities.forEach(a -> {
            ArticleNumberWithName articles = getArticles(a.getAvailableArticles());
            writer.set(
                    columns.getColumnIndex(ID),
                    employeeId);
            writer.set(
                    columns.getColumnIndex(ORDINAL_NUMBER),
                    ordinalNumber);
            writer.set(
                    columns.getColumnIndex(ARTICLE_NUMBER),
                    (articles.getNumber()));
            writer.set(
                    columns.getColumnIndex(ARTICLE_NAME),
                    articles.getName());
            writer.set(
                    columns.getColumnIndex(ARTICLE_QUANTITY),
                    a.getQuantity());
            writer.nextRow();
        });
    }

    private ArticleNumberWithName getArticles(Set<ClientArticle> availableArticles) {
            String number = "";
            String name = "";
            int i = 0;
            for(ClientArticle a : availableArticles) {
                i++;
                number += a.getArticle().getNumber();
                name += a.getArticle().getName();
                if(i < availableArticles.size()) {
                    number += "/";
                    name += "/";
                }
            }
        return new ArticleNumberWithName(number, name);
    }


    private void writeEmployee(Employee e) {
        writer.set(
                columns.getColumnIndex(LAST_AND_FIRST_NAME),
                e.getLastName() + " " + e.getFirstName());
        writer.set(
                columns.getColumnIndex(LOCKER_NUMBER),
                e.getBox().getLocker().getLockerNumber());
        writer.set(
                columns.getColumnIndex(BOX_NUMBER),
                e.getBox().getBoxNumber());
        writer.set(
                columns.getColumnIndex(DEPARTMENT),
                e.getDepartment().getName());
    }

    private void createRows(List<Employee> employees) {
        int numberOfRows = 0;
        for(Employee e : employees) {
            numberOfRows += e.getPosition().getArticlesWithQuantities().size();
        }
        writer.copyRowTemplate(numberOfRows);
    }

}
