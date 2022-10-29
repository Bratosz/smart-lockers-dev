package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.converters.ConvertableEnum;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

@Entity
public class Box implements Comparable<Box>{
    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.Public.class)
    private int boxNumber;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    private BoxStatus boxStatus;

    @JsonView({Views.InternalForLockers.class, Views.InternalForBoxes.class})
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EmployeeGeneral employee;

    @OneToOne(fetch = FetchType.LAZY)
    private EmployeeDummy employeeDummy;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class, Views.InternalForClothes.class, Views.OrderBasicInfo.class})
    @ManyToOne
    private Locker locker;

    private boolean duplicated;

    public Box() {
    }

    public Box(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(int boxNumber) {
        this.boxNumber = boxNumber;
    }

    public BoxStatus getBoxStatus() {
        return boxStatus;
    }

    public void setBoxStatus(BoxStatus boxStatus) {
        this.boxStatus = boxStatus;
    }

    public EmployeeGeneral getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeGeneral employee) {
            this.employee = employee;
            setBoxStatus(BoxStatus.OCCUPY);
    }

    public EmployeeDummy getEmployeeDummy() {
        return employeeDummy;
    }

    public void setEmployeeDummy(EmployeeDummy employeeDummy) {
        this.employeeDummy = employeeDummy;
    }


    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    @Override
    public int compareTo(Box o) {
        return Comparator.comparing(Box::getLocker)
                .thenComparing(Box::getBoxNumber)
                .compare(this, o);
    }

//    public void setEmployee(Employee employee) {
//        if(employee.isOrderEmpty()) {
//            this.employee = employee;
//            setBoxStatus(BoxStatus.FREE);
//        } else {
//            setEmptyEmployee(this.employee);
//            this.employee = employee;
//            setBoxStatus(BoxStatus.OCCUPY);
//        }
//    }

    public enum BoxStatus implements ConvertableEnum {
        OCCUPY("Zajęta"),
        FREE("Wolna"),
        ALL("Wszystkie"),
        UNDEFINED("Niezdefiniowana"),
        DAMAGED("Uszkodzona");

        private String name;


        BoxStatus(String name) {
            this.name = name;
        }

        @JsonValue
        @Override
        public String getName() {
            return name;
        }
    }

    public boolean isDuplicated() {
        return duplicated;
    }

    public void setDuplicated(boolean duplicated) {
        this.duplicated = duplicated;
    }

    @Override
    public String toString() {
        return locker.toString() +
                "/" + boxNumber;
    }
}
