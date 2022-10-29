package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import pl.bratosz.smartlockers.service.exels.columns.ColumnType;

import java.util.*;

public class RowLoader {
    private Map<ColumnType, Integer> indexes;
    private LoadType loadType;

    private RowLoader() {
        indexes = new HashMap<>();
    }

    public static RowLoader create(LoadType loadType) {
        RowLoader rowLoader = new RowLoader();
        rowLoader.setLoadType(loadType);
        return rowLoader;
    }

    public <T extends LoadedRow> List<T> loadRows(Sheet sheet) {
        ColumnAssigner ca = new ColumnAssigner(sheet.getRow(0));
        indexes = ca.assignColumnsToTheirTypeWithIndexes();
        List<T> loadedRows = loadAllRows(sheet);
        return loadedRows;
    }

    private <T extends LoadedRow> List<T> loadAllRows(Sheet sheet) {
        Row row;
        List<T> loadedRows = new LinkedList<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (isRowCorrect(row)) {
                T loadedRow = loadValues(row);
                loadedRows.add(loadedRow);
            } else {
                continue;
            }
        }
        return loadedRows;
    }

    private <T extends LoadedRow> T loadValues(Row row) {
        switch (loadType) {
            case CLOTHES_ROTATION_UPLOAD:
                return (T) new RowForRotationUpdate(row, indexes);
            case RELEASED_ROTATIONAL_CLOTHING_UPDATE:
                return (T) new RowForReleasedRotationalClothes(row, indexes);
            case BASIC_DATA_BASE_UPLOAD:
                return (T) new RowForBasicDataBaseUpload(row, indexes);
            default:
                return null;
        }
    }

    private boolean isRowCorrect(Row row) {
        return CellValueManager.isCellValueValid(row.getCell(0));
    }



    public void setLoadType(LoadType loadType) {
        this.loadType = loadType;
    }
}
