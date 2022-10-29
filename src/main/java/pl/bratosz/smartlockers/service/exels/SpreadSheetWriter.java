package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import pl.bratosz.smartlockers.utils.string.MyString;
import pl.bratosz.smartlockers.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static pl.bratosz.smartlockers.reports.ReportGenerator.*;
import static pl.bratosz.smartlockers.service.exels.SpreadSheetWriter.Style.*;
import static pl.bratosz.smartlockers.service.exels.SpreadSheetWriter.Style.SMALL_BOLDED;
import static pl.bratosz.smartlockers.service.exels.SpreadSheetWriter.Style.VERY_SMALL;

public class SpreadSheetWriter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private XSSFRow row;
    private XSSFCell cell;
    private Map<ColumnDataType, Integer> columnIndexes;
    private Map<Style, XSSFCellStyle> styles;
    private int rowIndex;
    private int rowTemplateIndex;
    private int lastRowIndex;
    private int firstRowTableIndex;
    private int columnIndex;
    private int lastColumnIndex;

    public SpreadSheetWriter(String sheetName) {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
        rowIndex = 0;
        styles = new HashMap<>();
        createStyles();
    }

    public SpreadSheetWriter(
            XSSFWorkbook workbook,
            int sheetIndex,
            int rowTemplateIndex) {
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(sheetIndex);
        this.rowIndex = rowTemplateIndex;
        this.rowTemplateIndex = rowTemplateIndex;
        this.styles = new HashMap<>();
        createStyles();
    }

    public void createSheet(String sheetName) {
        sheet = workbook.createSheet(sheetName);
        rowIndex = 0;
        lastRowIndex = 0;
    }


    private void createStyles() {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = style.getFont();
        font.setFontHeightInPoints((short) 8);
        font.setBold(true);
        style.setFont(font);
        styles.put(SMALL_BOLDED, style);

        style = workbook.createCellStyle();
        font = style.getFont();
        font.setFontHeightInPoints((short) 6);
        font.setBold(false);
        style.setFont(font);
        styles.put(VERY_SMALL, style);

        style = workbook.createCellStyle();
        font = style.getFont();
        font.setFontHeightInPoints((short) 9);
        font.setBold(false);
        style.setFont(font);
        styles.put(MEDIUM, style);
    }

    public void addHeader(
            Map<ColumnDataType, Integer> headers,
            Style style) {
        this.columnIndexes = headers;
        createRow();
        row.setRowStyle(styles.get(style));
        headers.forEach((columnDataType, cellIndex) -> {
            createCell(cellIndex);
            cell.setCellValue(columnDataType.getName());
        });
    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void nextRow() {
        row = sheet.getRow(rowIndex + 1);
        if(row == null) {
            rowIndex++;
            createRow();
        } else {
            rowIndex ++;
            columnIndex = 0;
        }
    }

    public void copyRowAndAddBelow() {
        Row toCopy = row;
        createRow();
        for(Iterator cellIt = toCopy.cellIterator(); cellIt.hasNext();) {
            XSSFCell tmpCell = (XSSFCell) cellIt.next();
            XSSFCell newCell = row.createCell(tmpCell.getColumnIndex());
            newCell.setCellValue(tmpCell.getStringCellValue());
        }
    }


    public void set(
            ColumnDataType columnType,
            String value) {
        int columnIndex = columnIndexes.get(columnType);
        createCell(columnIndex, value);
    }

    public boolean present(ColumnDataType columnDataType) {
        if(columnIndexes.get(columnDataType) == null) {
            return false;
        } else {
            return true;
        }
    }

    public void set(
            int columnIndex,
            String value) {
        sheet.getRow(rowIndex).getCell(columnIndex).setCellValue(value);
    }

    public void set(
            int columnIndex,
            int value) {
        String sValue = String.valueOf(value);
        set(columnIndex, sValue);
    }

    public void set(
            ColumnDataType header,
            Style style,
            String value) {
        int columnIndex = columnIndexes.get(header);
        XSSFCellStyle cellStyle = styles.get(style);
        createCell(columnIndex, cellStyle, value);
    }

    public void set(
            int columnIndex,
            Style style,
            String value) {
        XSSFCellStyle cellStyle = styles.get(style);
        createCell(columnIndex, cellStyle, value);
    }

    public void set(
            ColumnDataType header,
            Style style,
            int value) {
        int columnIndex = columnIndexes.get(header);
        XSSFCellStyle cellStyle = styles.get(style);
        String stringValue = String.valueOf(value);
        createCell(columnIndex, cellStyle, stringValue);
    }

    public void set(ColumnDataType dataType, int value) {
        set(dataType, String.valueOf(value));
    }

    public void set(int columnIndex, long value) {
        set(columnIndex, String.valueOf(value));
    }

    public void copyRowTemplate(int numberOfRows) {
        for(int i = rowTemplateIndex + 1; i < (rowTemplateIndex + numberOfRows); i++) {
            sheet.copyRows(rowTemplateIndex, rowTemplateIndex, i, new CellCopyPolicy());
        }
    }

    public void formatSheet() {
        autoSizeColumns();
    }

    private void autoSizeRowHeight(String value) {
        if (value.contains("\n")) row.setHeightInPoints(28);
    }

    private void autoSizeColumns() {
        for (int i = 0; i <= lastColumnIndex; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void goToEmptyRowBelow() {
        rowIndex = createRow();
    }


    private void addHeaderCell(String content) {
        sheet.getRow(firstRowTableIndex - 1)
                .createCell(columnIndex)
                .setCellValue(content);
    }

    private void addTableHeaderRow() {
        firstRowTableIndex = createRow() + 1;
    }

    private void nextColumn() {
        columnIndex++;
    }

    private <T> void fillCellsInColumn(List<T> list) {
        rowIndex = firstRowTableIndex;
        for (T value : list) {
            addCellInColumn(columnIndex, value.toString());
        }
    }

    private <T> void addCellsInRow(List<T> list) {
        for (T data : list) {
            addCellToRow(data.toString());
        }
    }

    private void addCellToRow(String value) {
        autoSizeRowHeight(value);
        createCell(columnIndex++);
        cell.setCellValue(value);
    }

    private void addCellInColumn(int columnIndex, String value) {
        row = getRow(rowIndex++);
        autoSizeRowHeight(value);
        createCell(columnIndex);
        cell.setCellValue(value);
    }

    private XSSFRow getRow(int rowIndex) {
        XSSFRow row = sheet.getRow(rowIndex);
        if (row == null) {
            createMissingRow();
            return this.row;
        } else {
            return row;
        }
    }

    private void createCell(int columnIndex) {
        updateLastColumnIndex(columnIndex);
        cell = row.createCell(columnIndex);
    }

    private void createCell(int columnIndex, String value) {
        createCell(columnIndex);
        if(Utils.isNumeric(value)) {
            cell.setCellType(CellType.NUMERIC);
            value = MyString.create(value).get();
            cell.setCellValue(Long.parseLong(value));
        } else {
            cell.setCellValue(value);
        }
    }

    private void createCell(int columnIndex, XSSFCellStyle style, String value) {
        createCell(columnIndex);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    private void updateLastColumnIndex(int columnIndex) {
        if (columnIndex > lastColumnIndex) {
            lastColumnIndex = columnIndex;
        }
    }

    private void addEmptyRow() {
        createRow();
    }

    private void addDataRow(String label, String data) {
        createRow();
        addCellToRow(label);
        addCellToRow(data);
    }

    private void addDataRow(String value) {
        createRow();
        addCellToRow(value);
    }

    private int createRow() {
        columnIndex = 0;
        int thisRowIndex = lastRowIndex;
        row = sheet.createRow(lastRowIndex++);
        return thisRowIndex;
    }

    private int createMissingRow() {
        int thisRowIndex = lastRowIndex;
        row = sheet.createRow(lastRowIndex++);
        return thisRowIndex;
    }

    public enum Style {
        SMALL_BOLDED,
        VERY_SMALL,
        MEDIUM,
    }

}