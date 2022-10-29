package pl.bratosz.smartlockers.service.exels;


public class LabelEmployee {
    private String firstName;
    private String lastName;
    private int lockerNumber;
    private int boxNumber;

    public LabelEmployee(String firstName, String lastName, int lockerNumber, int boxNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lockerNumber = lockerNumber;
        this.boxNumber = boxNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getLockerNumber() {
        return lockerNumber;
    }

    public void setLockerNumber(int lockerNumber) {
        this.lockerNumber = lockerNumber;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }
}
