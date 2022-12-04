package pl.bratosz.smartlockers.service.managers.creators;

import pl.bratosz.smartlockers.exception.EmptyElementException;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.service.exels.RowForBasicDataBaseUpload;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class LockerCreator {
    private Client client;
    private Location location;
    private Department department;

    public LockerCreator(Client client) {
        this.client = client;
    }

    public LockerCreator() {
    }

    public static Locker create(int lockerNumber,
                                int lockerCapacity,
                                Plant plant,
                                Department department,
                                Location location) {
        Locker locker = new Locker(lockerNumber, lockerCapacity);
        List<Box> boxes = BoxCreator.createBoxes(locker);
        locker.setBoxes(boxes);
        locker.setPlant(plant);
        locker.setDepartment(department);
        locker.setLocation(location);
        return locker;
    }

    public static Locker createWithCustomBoxNumbers(
            int lockerNumber,
            List<Integer> boxNumbers,
            Plant plant,
            Department department,
            Location location) {
        Locker locker = new Locker(lockerNumber, boxNumbers.size());
        List<Box> boxes = BoxCreator.createBoxesWithCustomNumbers(locker, boxNumbers);
        locker.setBoxes(boxes);
        locker.setPlant(plant);
        locker.setDepartment(department);
        locker.setLocation(location);
        return locker;
    }

    public static Locker create(
            int lockerNumber,
            List<Box> boxes,
            int capacity,
            Plant plant,
            Department department,
            Location location) {
        Locker l = new Locker();
        l.setLockerNumber(lockerNumber);
        l.addBoxes(boxes.stream()
                .sorted(Comparator.comparing(box -> box.getBoxNumber()))
                .collect(Collectors.toList()));
        l.setCapacity(capacity);
        l.setPlant(plant);
        l.setDepartment(department);
        l.setLocation(location);
        return l;
    }

    public Locker createFromRowWithBoxes(RowForBasicDataBaseUpload row) throws EmptyElementException {
        int lockerNumber = row.getLockerNumber();
        int capacity = row.getCapacity();
        try {
            Plant plant = client.getPlantByNumber(row.getPlantNumber());
            Location location = client.getLocationByName(row.getLocationName());
            Department department = client.getDepartmentByName(row.getDepartmentName());

            Locker locker = new Locker();
            locker.setCapacity(capacity);
            BoxCreator boxCreator = new BoxCreator();
            List<Box> boxes = boxCreator.createBoxes(locker);

            locker.setLockerNumber(lockerNumber);
            locker.setPlant(plant);
            locker.setDepartment(department);
            locker.setLocation(location);
            locker.setBoxes(boxes);

            return locker;
        } catch (NoSuchElementException e) {
            throw new EmptyElementException(
                    "Locker #" +
                            lockerNumber + " has not been created.\n" +
                            row.getLocationName() + "\n" + row.getLastName());
        }

    }


}
