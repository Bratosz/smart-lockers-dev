package pl.bratosz.smartlockers.service.employees;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;

public class EmployeeWithActiveOrders {
    @JsonView(Views.Public.class)
    private long id;
    @JsonView(Views.Public.class)
    private String departmentName;
    @JsonView(Views.Public.class)
    private int plantNumber;
    @JsonView(Views.Public.class)
    private int lockerNumber;
    @JsonView(Views.Public.class)
    private int boxNumber;
    @JsonView(Views.Public.class)
    private String lastName;
    @JsonView(Views.Public.class)
    private String firstName;
    @JsonView(Views.Public.class)
    private long activeOrders;

    public EmployeeWithActiveOrders(
            long id, String departmentName, int plantNumber, int lockerNumber, int boxNumber, String lastName, String firstName, long activeOrders
    ) {
        this.id = id;
        this.departmentName = departmentName;
        this.plantNumber = plantNumber;
        this.lockerNumber = lockerNumber;
        this.boxNumber = boxNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.activeOrders = activeOrders;
    }

    public long getId() {
        return id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public int getPlantNumber() {
        return plantNumber;
    }

    public int getLockerNumber() {
        return lockerNumber;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public long getActiveOrders() {
        return activeOrders;
    }
}
