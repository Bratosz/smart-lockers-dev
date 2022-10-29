package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.utils.string.MyString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class EmployeeGeneral {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Public.class)
    protected long id;

    @JsonView(Views.Public.class)
    protected String personalNumber;

    @JsonView(Views.Public.class)
    protected String firstName;

    @JsonView(Views.Public.class)
    protected String lastName;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonView({Views.InternalForEmployees.class, Views.InternalForClothes.class, Views.EmployeeBasicInfo.class, Views.OrderBasicInfo.class})
    protected Box box;

    protected  boolean dummy;

    public EmployeeGeneral(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return MyString.create(lastName).get();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

   public void addToBox(Box box) {
        box.setEmployee(this);
        this.setBox(box);
   }

    public boolean isDummy() {
        return dummy;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    private void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeGeneral that = (EmployeeGeneral) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
