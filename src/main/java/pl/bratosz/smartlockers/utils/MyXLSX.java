package pl.bratosz.smartlockers.utils;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import pl.bratosz.smartlockers.utils.string.MyString;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

public class MyXLSX {

    public static String getCellValue(XSSFCell cell) {
        CellType cellType = cell.getCellTypeEnum();
        if (cellType.equals(NUMERIC))
            return MyString.create(
                    String.valueOf(cell.getNumericCellValue())).get();
        else
            return MyString.create(
                    cell.getStringCellValue()).get();
    }

    public static int getIntCellValue(XSSFCell cell) {
        CellType cellType = cell.getCellTypeEnum();
        if(cellType.equals(NUMERIC)) {
            return (int) cell.getNumericCellValue();
        } else {
            String s = MyString.create(cell.getStringCellValue()).get();
            return Float.valueOf(s).intValue();
        }
    }

}
