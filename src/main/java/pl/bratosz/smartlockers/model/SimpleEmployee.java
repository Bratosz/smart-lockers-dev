package pl.bratosz.smartlockers.model;

import pl.bratosz.smartlockers.service.update.SimpleCloth;

import javax.persistence.Embeddable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Embeddable
public class SimpleEmployee implements Comparable<SimpleEmployee>{
    private String firstName;
    private String lastName;
    private int lockerNumber;
    private int boxNumber;
    private String departmentAlias;
    private String comment;
    private Set<SimpleCloth> clothes;

    public SimpleEmployee() {
        firstName = "";
        lastName = "";
        departmentAlias = "";
        comment = "";
    }

    public SimpleEmployee(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public SimpleEmployee(String firstName,
                          String lastName,
                          int locker,
                          int box) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lockerNumber = locker;
        this.boxNumber = box;
    }

    public SimpleEmployee(String firstName,
                          String lastName,
                          int locker,
                          int box,
                          String departmentAlias) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lockerNumber = locker;
        this.boxNumber = box;
        this.departmentAlias = departmentAlias;
        this.clothes = new HashSet<>();
    }

    public SimpleEmployee(String firstName,
                          String lastName,
                          String comment,
                          String departmentAlias,
                          int lockerNumber,
                          int boxNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.comment = comment;
        this.departmentAlias = departmentAlias;
        this.lockerNumber = lockerNumber;
        this.boxNumber = boxNumber;
    }

    public SimpleEmployee(SimpleCloth simpleCloth, Employee e) {
        addCloth(simpleCloth);
        this.firstName = e.getFirstName();
        this.lastName = e.getLastName();
        this.lockerNumber = e.getBox().getLocker().getLockerNumber();
        this.boxNumber = e.getBox().getBoxNumber();
        this.departmentAlias = e.getDepartment().getName();
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

    public String getDepartmentAlias() {
        return departmentAlias;
    }

    public void setDepartmentAlias(String departmentAlias) {
        this.departmentAlias = departmentAlias;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<SimpleCloth> getClothes() {
        return clothes;
    }

    public void addCloth(SimpleCloth cloth) {
        if(clothes == null) {
            clothes = new HashSet<>();
        }
        cloth.setSimpleEmployee(this);
        clothes.add(cloth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleEmployee that = (SimpleEmployee) o;
        return Objects.equals(getLastName(), that.getLastName()) &&
                Objects.equals(getFirstName(), that.getFirstName()) &&
                getBoxNumber() == that.getBoxNumber() &&
                getLockerNumber() == that.getLockerNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getLockerNumber(), getBoxNumber());
    }

    @Override
    public String toString() {
        return lockerNumber + "/" + boxNumber + " " + lastName + " " + firstName;
    }

    @Override
    public int compareTo(SimpleEmployee o) {
        return Comparator.comparing(SimpleEmployee::getLockerNumber)
                .thenComparing(SimpleEmployee::getBoxNumber)
                .thenComparing(SimpleEmployee::getLastName)
                .compare(this, o);
    }
}
