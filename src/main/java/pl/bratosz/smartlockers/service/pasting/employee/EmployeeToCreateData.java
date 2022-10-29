package pl.bratosz.smartlockers.service.pasting.employee;

public class EmployeeToCreateData {
    private long employeeId;
    private long departmentId;
    private long positionId;
    private long locationId;

    public EmployeeToCreateData() {
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public long getPositionId() {
        return positionId;
    }

    public long getLocationId() {
        return locationId;
    }
}
