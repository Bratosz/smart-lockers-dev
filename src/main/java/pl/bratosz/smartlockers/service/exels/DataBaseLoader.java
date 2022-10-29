package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.bratosz.smartlockers.exception.EmptyElementException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.service.managers.creators.EmployeeCreator;
import pl.bratosz.smartlockers.service.managers.creators.LockerCreator;

import java.util.*;

public class DataBaseLoader {
    private XSSFSheet sheet;
    private LoadType loadType;
    private List<Locker> lockers;
    private List<Employee> employees;
    private Client client;
    private List<RowForBasicDataBaseUpload> loadedRows;


    public DataBaseLoader(XSSFWorkbook wb, LoadType loadType, Client client) {
        sheet = wb.getSheetAt(0);
        this.client = client;
        this.loadType = loadType;
        lockers = new LinkedList<>();


    }

    public List<Locker> loadDataBase() {
        switch (loadType) {
            case BASIC_DATA_BASE_UPLOAD:
                return loadBasicDataBase();
            case DETAILED_DATA_BASE_UPLOAD:
                return loadDetailedDataBase(client);
            default:
                return new LinkedList<>();
        }
    }

     private List<Locker> loadBasicDataBase() {
        RowLoader rowLoader = RowLoader.create(loadType);
        loadedRows = rowLoader.loadRows(sheet);
        return createLockersWithBoxesAndEmployees();

    }

    private List<Locker> createLockersWithBoxesAndEmployees() {
        createLockersAndBoxes();
        createEmployeesAndAddToBoxes();
        return lockers;
    }

    private void createEmployeesAndAddToBoxes() {
        EmployeeCreator employeeCreator = new EmployeeCreator(client);
        for(RowForBasicDataBaseUpload row : loadedRows) {
            Locker locker = getLockerByRow(row);
            Box box = getBoxByRow(row);
            box.setLocker(locker);
            Box.BoxStatus boxStatus = row.getBoxStatus();
            if(boxStatus.equals(Box.BoxStatus.OCCUPY)) {
                Employee employee = employeeCreator.createFromRow(row);
                box.setEmployee(employee);
            }
        }
    }

    private Locker getLockerByRow(RowForBasicDataBaseUpload row) {
        int lockerNumber = row.getLockerNumber();
        int plantNumber = row.getPlantNumber();
        return lockers.stream().filter(l -> l.getPlant().getPlantNumber() == plantNumber)
                .filter(l -> l.getLockerNumber() == lockerNumber)
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    private Box getBoxByRow(RowForBasicDataBaseUpload row) {
        int lockerNumber = row.getLockerNumber();
        int boxNumber = row.getBoxNumber();
        int plantNumber = row.getPlantNumber();
        Locker locker = lockers.stream().filter(l -> l.getLockerNumber() == lockerNumber)
                .filter(l -> l.getPlant().getPlantNumber() == plantNumber)
                .findFirst().orElseThrow(NoSuchElementException::new);
        return locker.getBoxes()
        .stream()
        .filter(box -> box.getBoxNumber() == boxNumber).findFirst().orElseThrow(NoSuchElementException::new);
    }

    private void createLockersAndBoxes() {
        LockerCreator lockerCreator = new LockerCreator(client);
        int actualLockerNumber = -1;
        for(RowForBasicDataBaseUpload row : loadedRows) {
            if(actualLockerNumber != row.getLockerNumber()) {
                Locker locker = null;
                try {
                    locker = lockerCreator.createFromRowWithBoxes(row);
                    actualLockerNumber = locker.getLockerNumber();
                    lockers.add(locker);
                } catch (EmptyElementException e) {
                    continue;
                }

            }
        }
    }


    private List<Locker> loadDetailedDataBase(Client client) {
        return new LinkedList<>();
    }
}
