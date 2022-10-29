package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.bratosz.smartlockers.service.exels.format.Format;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.model.Employee;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelWriter {
    private List<String> columns;
    private List<Employee> employees;
    private String sheetName;
    private CellStyle cellStyle;
    private Font font;
    private XSSFWorkbook workbook;
    private Sheet sheet;
    private Format format;
    private int labelsInRow;
    private int labelsInColumn;

    public ExcelWriter(LabelsSheetParameters parameters) {
        createFont(parameters);
        format = parameters.getSheetFormat();
        labelsInRow = parameters.getLabelsInRow();
        labelsInColumn = parameters.getLabelsInColumn();
        sheetName = parameters.getSheetName();
        sheet = workbook.createSheet(sheetName);
        setPrintParameters();
    }

    private void setPrintParameters() {
        sheet.setMargin(Sheet.RightMargin, 0.0d);
        sheet.setMargin(Sheet.LeftMargin, 0.0d);
        sheet.setMargin(Sheet.BottomMargin, 0.0d);
        sheet.setMargin(Sheet.TopMargin, 0.0d);
        sheet.setFitToPage(true);
    }

    public ExcelWriter(List<String> columns, List<Employee> sortedEmployees, String sheetName) {
        this.columns = columns;
        this.employees = sortedEmployees;
        this.sheetName = sheetName;
    }

    public XSSFWorkbook createLabels(List<String> labels) {
        createLabelsStyle();
        setColumnsAndRowsSize();
        createRequiredNumberOfRows(labels.size());
        writeLabelsInCells(labels);
        return workbook;
    }

    private void createLabelsStyle() {
        cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);
    }

    private void setColumnsAndRowsSize() {
        int pageWidth = format.getWidth();
        int pageHeight = format.getHeight();
        setColumnsWidth(pageWidth);
        setRowsHeight(pageHeight);
    }

    private void createRequiredNumberOfRows(int size) {
        int numberOfRows = calculateNumberOfRows(size);
        for (int i = 0; i < numberOfRows; i++) {
            sheet.createRow(i);
        }
    }

    private void writeLabelsInCells(List<String> labels) {
        int labelPointer = 0;
        for (int i = 0; i < labels.size(); i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < labelsInRow; j++) {
                if (labelPointer >= labels.size()) {
                    return;
                }
                String label = labels.get(labelPointer++);
                Cell cell = row.createCell(j);
                cell.setCellValue(label);
                cell.setCellStyle(cellStyle);
            }
        }

    }

    private int calculateNumberOfRows(int numberOfLabels) {
        return (int) Math.ceil((float) numberOfLabels / labelsInRow);
    }

    private void createFont(LabelsSheetParameters parameters) {
        workbook = new XSSFWorkbook();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) parameters.getFontSize());
        font.setFontName(parameters.getFontName());
    }

    private void setColumnsWidth(int pageWidth) {
        int columnWidthInMM = calculateSingleColumnWidth(pageWidth);
        int columnWidthInPoints = (int) convertMillimetersToPointsForWidth(columnWidthInMM);

        for (int i = 0; i < labelsInRow; i++) {
            sheet.setColumnWidth(i, columnWidthInPoints);
        }
    }

    private void setRowsHeight(int height) {
        float rowHeight = calculateSingleRowHeight(height);
        float rowHeightInPoints = convertMillimetersToPointsForHeight(rowHeight);
        sheet.setDefaultRowHeightInPoints(rowHeightInPoints);
    }

    private int calculateSingleColumnWidth(int pageWidth) {
        return (pageWidth / labelsInRow);
    }

    private float calculateSingleRowHeight(float pageHeight) {
        return pageHeight / labelsInColumn;
    }

    private float convertMillimetersToPointsForWidth(float width) {
        return width / 0.00765613f;
    }

    private float convertMillimetersToPointsForHeight(float height) {
        return height / 0.35266666f;
    }


    public XSSFWorkbook createExcelRaportWithEmployees() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        //Create a font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 13);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        //Create a cell style with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i));
            cell.setCellStyle(headerCellStyle);
        }

        int counter = 1;
        for (Employee employee : employees) {
            Row row = sheet.createRow(counter++);

//            headerRow.createCell(0).setCellValue(employee.getBoxes().stream().findFirst().getClothesAndEmployeesToUpdate().goToAndGetEmployeeBy().getArticlesWithQuantityId());
            row.createCell(0).setCellValue(employee.getId());
            row.createCell(1).setCellValue(employee.getLastName());
            row.createCell(2).setCellValue(employee.getFirstName());
            row.createCell(3).setCellValue(employee.getDepartment().getName());

            Box box = employee.getBox();
            int lockerNumber = box.getLocker().getLockerNumber();
            int boxNumber = box.getBoxNumber();
            row.createCell(4).setCellValue(lockerNumber);
            row.createCell(5).setCellValue(boxNumber);
            row.createCell(6).setCellValue(box.getLocker().getPlant().getPlantNumber());
        }

        //Resize all columns to fit the content
        for (int i = 0; i < columns.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        return (XSSFWorkbook) workbook;
    }

    public static void saveWorkbook(XSSFWorkbook workbook) throws IOException {
        FileOutputStream fileOut = new FileOutputStream("C:/Users/maria/Desktop/KLS/Reports/" + workbook.getSheetName(0)
                + ".xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }


}
