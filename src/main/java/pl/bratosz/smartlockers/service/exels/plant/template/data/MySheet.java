package pl.bratosz.smartlockers.service.exels.plant.template.data;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class MySheet {
    private XSSFSheet sheet;
    private int lastRowIndex;
    private int lastColIndex;

    public MySheet(XSSFSheet sheet, int lastRowIndex, int lastColIndex) {
        this.sheet = sheet;
        this.lastRowIndex = lastRowIndex;
        this.lastColIndex = lastColIndex;
    }

    public XSSFSheet getSheet() {
        return sheet;
    }

    public int getLastRowIndex() {
        return lastRowIndex;
    }

    public int getLastColIndex() {
        return lastColIndex;
    }
}
