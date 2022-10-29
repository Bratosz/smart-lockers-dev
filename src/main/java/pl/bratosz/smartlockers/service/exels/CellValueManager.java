package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import pl.bratosz.smartlockers.exception.CellValueIsInvalidException;

public class CellValueManager {
    public static String getNormalizedStringValue(Cell cell) {
        if (isCellValueValid(cell)) {
            if(isCellNumeric(cell)){
                return String.valueOf(cell.getNumericCellValue());
            }
            String content = cell.getStringCellValue();
            return content.trim().toUpperCase();
        } else {
            throw new CellValueIsInvalidException("Cell is empty");
        }
    }

    public static boolean isCellValueValid(Cell cell) {
        if(isCellNull(cell)){
            return false;
        } else if (isCellNumeric(cell)) {
            return true;
        } else if (isCellString(cell)) {
            if (isCellStringEmpty(cell)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCellStringEmpty(Cell cell) {
        return cell.getStringCellValue().length() == 0;
    }


    public static boolean isCellNull(Cell cell) {
        if (cell == null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCellNumeric(Cell cell) {
        if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCellString(Cell cell) {
        if (cell.getCellTypeEnum().equals(CellType.STRING)) {
            return true;
        } else {
            return false;
        }
    }
}
