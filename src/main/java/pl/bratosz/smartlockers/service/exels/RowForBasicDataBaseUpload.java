package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Row;
import pl.bratosz.smartlockers.model.Box;
import pl.bratosz.smartlockers.service.exels.columns.ColumnType;

import java.util.Map;

import static pl.bratosz.smartlockers.service.exels.columns.ColumnType.*;

public class RowForBasicDataBaseUpload extends LoadedRow {
    private int lockerNumber;
    private int boxNumber;
    private Box.BoxStatus boxStatus;
    private int plantNumber;
    private String departmentName;
    private String locationName;
    private int capacity;

    public RowForBasicDataBaseUpload(
            Row row, Map<ColumnType, Integer> indexes) {
        super(row, indexes);
        setLockerNumber(LOCKER_NUMBER);
        setBoxNumber(BOX_NUMBER);
        setBoxStatus(BOX_STATUS);
        setPlantNumber(PLANT_NUMBER);
        setDepartmentName(DEPARTMENT);
        setLocationName(ColumnType.LOCATION);
        setCapacity(CAPACITY);
    }

    private void setLockerNumber(ColumnType columnType) {
        this.lockerNumber = getNumericCellValueByColumnType(columnType);
    }

    private void setBoxNumber(ColumnType columnType) {
            this.boxNumber = getNumericCellValueByColumnType(columnType);

    }

    private void setBoxStatus(ColumnType columnType) {
        String status = getStringCellValueByColumnType(columnType);
        switch(status) {
            case "OCCUPY":
            case "ZAJĘTA":
                this.boxStatus = Box.BoxStatus.OCCUPY;
                break;
            case "FREE":
            case "WOLNA":
                this.boxStatus = Box.BoxStatus.FREE;
            default:
                this.boxStatus = Box.BoxStatus.UNDEFINED;
        }
    }

    private void setPlantNumber(ColumnType columnType) {
        this.plantNumber = getNumericCellValueByColumnType(columnType);
    }

    private void setDepartmentName(ColumnType columnType) {
        this.departmentName = getStringCellValueByColumnType(columnType);
    }

    private void setLocationName(ColumnType columnType) {
        this.locationName = getStringCellValueByColumnType(columnType);
    }

    private void setCapacity(ColumnType columnType) {
        this.capacity = getNumericCellValueByColumnType(columnType);
    }

    public int getLockerNumber() {
        return lockerNumber;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public Box.BoxStatus getBoxStatus() {
        return boxStatus;
    }

    public int getPlantNumber() {
        return plantNumber;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getLocationName() {
        return locationName;
    }

    public int getCapacity() { return capacity; }
}
