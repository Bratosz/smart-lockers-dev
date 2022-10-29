package pl.bratosz.smartlockers.service.exels.plant.template.data;

public class TemplateLockers {
    int firstLockerNumber;
    int lastLockerNumber;
    int capacity;
    String location;

    public TemplateLockers(String firstLockerNumber, String lastLockerNumber, String capacity, String location) {
        this.firstLockerNumber = Float.valueOf(firstLockerNumber).intValue();
        this.lastLockerNumber = Float.valueOf(lastLockerNumber).intValue();
        this.capacity = Float.valueOf(capacity).intValue();
        this.location = location;
    }

    public int getFirstLockerNumber() {
        return firstLockerNumber;
    }

    public int getLastLockerNumber() {
        return lastLockerNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getLocation() {
        return location;
    }
}
