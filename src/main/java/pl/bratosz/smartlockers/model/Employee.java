package pl.bratosz.smartlockers.model;


import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.orders.MainOrder;
import pl.bratosz.smartlockers.model.users.UserEmployee;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Employee extends EmployeeGeneral implements Comparable<Employee>{

    @JsonView({Views.EmployeeCompleteInfo.class})
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cloth> clothes;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.EmployeeBasicInfo.class})
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<MainOrder> mainOrders;

    @JsonView(Views.EmployeeCompleteInfo.class)
    @OneToMany(mappedBy = "rotationTemporaryOwner", cascade = CascadeType.ALL)
    private Set<Cloth> rotationalClothes;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.EmployeeBasicInfo.class, Views.OrderBasicInfo.class})
    @ManyToOne(cascade = CascadeType.ALL)
    private Department department;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private UserEmployee userEmployee;

    @JsonView(Views.Public.class)
    private boolean active;

    @JsonView(Views.EmployeeCompleteInfo.class)
    private String note;

    @JsonView(Views.EmployeeCompleteInfo.class)
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<SimpleBox> pastBoxes;

    @JsonView(Views.InternalForEmployeesForOurStaff.class)
    private Double redemptionPrice;

    private String comment;

    private boolean isEmpty;

    @ManyToOne
    @JsonView({Views.EmployeeCompleteInfo.class, Views.EmployeeBasicInfo.class})
    private Position position;

    @JsonView(Views.Public.class)
    private LocalDate created;

    public Employee() {
    }

    public Employee(
            String firstName,
            String lastName,
            Department department,
            Position position,
            boolean active) {
        setFirstName(firstName);
        setLastName(lastName);
        setDepartment(department);
        setPosition(position);
        setActive(active);
        setCreated(LocalDate.now());
    }

    public static Employee createEmpty() {
        Employee e = new Employee();
        e.isEmpty = true;
        return e;
    }

    public static Employee create(
            String firstName,
            String lastName,
            String comment,
            Department department,
            Box box) {
        Employee e = createWithoutBox(
                firstName, lastName, comment, department);
        e.addToBox(box);
        return e;
    }

    public static Employee createWithoutBox(
            String firstName,
            String lastName,
            String comment,
            Department department) {
        Employee e = new Employee();
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setComment(comment);
        e.setDepartment(department);
        e.setActive(true);
        return e;
    }


    public Employee(String firstName, String lastName, Department department, boolean active) {
        setFirstName(firstName);
        setLastName(lastName);
        setDepartment(department);
        setActive(active);
    }

    public Set<Cloth> getRotationalClothes() {
        return rotationalClothes;
    }

    public void setRotationalClothes(Set<Cloth> rotationalClothes) {
        this.rotationalClothes = rotationalClothes;
    }

    public List<Cloth> getClothes() {
        if(clothes == null) return new LinkedList<>();
        else return clothes;
    }

    public void setClothes(List<Cloth> clothes) {
        this.clothes = clothes;
    }

    public void addClothes(List<Cloth> clothes) {
        for(Cloth c : clothes) {
            c.setEmployee(this);
        }
        this.setClothes(clothes);
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public UserEmployee getUserEmployee() {
        return userEmployee;
    }

    public void setUserEmployee(UserEmployee userEmployee) {
        this.userEmployee = userEmployee;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<SimpleBox> getPastBoxes() {
        return pastBoxes;
    }

    public void setPastBoxes(List<SimpleBox> pastBoxes) {
        this.pastBoxes = pastBoxes;
    }

    public void setAsPastBox(SimpleBox box) {
        box.setEmployee(this);
        if(pastBoxes == null) {
            pastBoxes = new LinkedList<>();
        }
        pastBoxes.add(box);
    }

    public SimpleBox getLastBox() {
        int last = pastBoxes.size() - 1;
        return pastBoxes.get(last);
    }



    public Double getRedemptionPrice() {
        return redemptionPrice;
    }

    public void setRedemptionPrice(Double redemptionPrice) {
        this.redemptionPrice = redemptionPrice;
    }

    public List<MainOrder> getMainOrders() {
        if(mainOrders == null) {
            return new LinkedList<>();
        }
        return mainOrders;
    }

    public void setMainOrders(List<MainOrder> mainOrders) {
        this.mainOrders = mainOrders;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    @Override
    public int compareTo(Employee o) {
        return Comparator.comparing(Employee::getBox)
                .thenComparing(Employee::getId)
                .compare(this, o);
    }

    @Override
    public String toString() {
        if(box == null) {
            return lastName + " " + firstName;
        } else {
            return box.toString() +
                    " " + lastName + " " + firstName;
        }
    }


}
