package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.List;

public class ExcelUpdater {
    private XSSFWorkbook wb;

    public static ExcelUpdater create(XSSFWorkbook wb) {
        return new ExcelUpdater(wb);
    }

    public <T> XSSFWorkbook updateRow(int rowIndex, int colIndex, List<T> list, String sheetName) {
        XSSFSheet sheet = wb.getSheet(sheetName);
        XSSFRow row = sheet.getRow(rowIndex);
        for(int i = colIndex; i <= list.size(); i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(list.get(i - 1).toString());
        }
        return wb;
    }

    public <T> XSSFWorkbook updateColumn(int rowIndex, int colIndex, List<T> list, String sheetName) {
        XSSFSheet sheet = wb.getSheet(sheetName);
        for(int i = rowIndex; i <= list.size(); i++) {
            XSSFRow row = sheet.createRow(i);
            XSSFCell cell = row.createCell(colIndex);
            cell.setCellValue(list.get(i - 1).toString());
        }
        sheet.autoSizeColumn(colIndex);
        return wb;
    }

    private ExcelUpdater(XSSFWorkbook wb) {
        this.wb = wb;
    }
}
