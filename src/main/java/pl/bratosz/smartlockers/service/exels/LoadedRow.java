package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import pl.bratosz.smartlockers.service.exels.columns.ColumnType;

import java.util.Map;

import static pl.bratosz.smartlockers.service.exels.columns.ColumnType.*;

public class LoadedRow {
    private String firstName;
    private String lastName;
    private Row row;
    private Map<ColumnType, Integer> indexes;

    public LoadedRow(Row row, Map<ColumnType, Integer> indexes){
        this.row = row;
        this.indexes = indexes;
        setFirstName(FIRST_NAME);
        setLastName(LAST_NAME);
    }


    private  void setFirstName(ColumnType firstName) {
        this.firstName = getStringCellValueByColumnType(firstName);
    }

    private void setLastName(ColumnType lastName) {
        this.lastName = getStringCellValueByColumnType(lastName);
    }



     String getStringCellValueByColumnType(ColumnType columnType) {
        int columnIndex = indexes.get(columnType);
        Cell cell = row.getCell(columnIndex);
        if(CellValueManager.isCellValueValid(cell)){
            String cellValue = CellValueManager.getNormalizedStringValue(cell);
            return cellValue;
        }
        return "";
    }

    int getNumericCellValueByColumnType(ColumnType columnType) {
        String s = getStringCellValueByColumnType(columnType);
        return (int) Double.parseDouble(s);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
