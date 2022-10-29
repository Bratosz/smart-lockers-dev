package pl.bratosz.smartlockers.service.exels;

import pl.bratosz.smartlockers.service.exels.format.A4;
import pl.bratosz.smartlockers.service.exels.format.Format;

public class LabelsSheetParameters {
    private int fontSize;
    private String fontName;
    private String sheetName;
    private Format sheetFormat;
    private int labelsInRow;
    private int labelsInColumn;

    public LabelsSheetParameters(String sheetName) {
        fontSize = 16;
        fontName = "Times New Roman";
        this.sheetName = sheetName;
        sheetFormat = new A4();
        labelsInRow = 3;
        labelsInColumn = 8;
    }

    public LabelsSheetParameters(int fontSize, String fontName, Format format) {
        this.fontSize = fontSize;
        this.fontName = fontName;
        this.sheetFormat = format;
        labelsInRow = 3;
        labelsInColumn = 8;
    }

    public LabelsSheetParameters(int fontSize, String fontName, Format sheetFormat, int labelsInRow, int labelsInColumn) {
        this.fontSize = fontSize;
        this.fontName = fontName;
        this.sheetFormat = sheetFormat;
        this.labelsInRow = labelsInRow;
        this.labelsInColumn = labelsInColumn;
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getFontName() {
        return fontName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public Format getSheetFormat() {
        return sheetFormat;
    }

    public int getLabelsInRow() {
        return labelsInRow;
    }

    public int getLabelsInColumn() {
        return labelsInColumn;
    }
}
