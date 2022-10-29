package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Row;
import pl.bratosz.smartlockers.service.exels.columns.ColumnType;

import java.util.Map;

public class RowForCloth extends LoadedRow {
    private long barCode;

    public RowForCloth(Row row, Map<ColumnType, Integer> indexes) {
        super(row, indexes);
        setBarCode(ColumnType.BAR_CODE);
    }

    private void setBarCode(ColumnType columnType) {
        this.barCode = getNumericCellValueByColumnType(columnType);
    }

    public long getBarCode() {
        return barCode;
    }
}
