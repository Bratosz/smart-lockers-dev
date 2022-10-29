package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import javax.persistence.*;

@Entity
public class SimpleBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonView(Views.Public.class)
    private int plantNumber;
    @JsonView(Views.Public.class)
    private int lockerNumber;
    @JsonView(Views.Public.class)
    private int boxNumber;

    @ManyToOne
    private Employee employee;

    public SimpleBox(){}

    public SimpleBox(int plantNumber, int lockerNumber, int boxNumber, Employee employee) {
        this.plantNumber = plantNumber;
        this.lockerNumber = lockerNumber;
        this.boxNumber = boxNumber;
        this.employee = employee;
    }

    public SimpleBox(Box box) {
        this.plantNumber = box.getLocker().getPlant().getPlantNumber();
        this.lockerNumber = box.getLocker().getLockerNumber();
        this.boxNumber = box.getBoxNumber();
    }

    public int getPlantNumber() {
        return plantNumber;
    }

    public void setPlantNumber(int plantNumber) {
        this.plantNumber = plantNumber;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
