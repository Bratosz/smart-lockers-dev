package pl.bratosz.smartlockers.model.clothes;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.model.orders.ClothOrder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Cloth implements Comparable<Cloth> {
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    protected long id;

    @JsonView(Views.Public.class)
    @Enumerated(EnumType.STRING)
    protected ClothSize size;

    @JsonView(Views.Public.class)
    protected Date created;

    @JsonView(Views.Public.class)
    protected int ordinalNumber;

    @JsonView({Views.EmployeeCompleteInfo.class, Views.InternalForBoxes.class, Views.InternalForClothOrders.class})
    @ManyToOne(fetch = FetchType.EAGER)
    protected ClientArticle clientArticle;

    @JsonView(Views.InternalForClothes.class)
    @OneToOne(mappedBy = "clothToExchange", cascade = CascadeType.PERSIST)
    private ClothOrder exchangeOrder;

    @JsonView(Views.InternalForClothes.class)
    @OneToOne(mappedBy = "clothToRelease", cascade = CascadeType.PERSIST)
    private ClothOrder releaseOrder;

    @JsonView(Views.InternalForClothes.class)
    @ManyToOne(fetch = FetchType.LAZY)
    protected Employee employee;

    @JsonView({Views.InternalForEmployees.class, Views.InternalForBoxes.class})
    @OneToMany(mappedBy = "cloth",fetch = FetchType.LAZY)
    protected List<ClothStatus> statusHistory;

    @JsonView(Views.Public.class)
    protected LifeCycleStatus lifeCycleStatus;

    @JsonView(Views.Public.class)
    long barcode;

    @JsonView(Views.Public.class)
    protected LocalDate assignment;

    @JsonView(Views.Public.class)
    protected LocalDate lastWashing;

    @JsonView(Views.Public.class)
    protected LocalDate releaseDate;

    @JsonView(Views.Public.class)
    protected boolean active;

    @JsonView(Views.Public.class)
    protected Double actualRedemptionPrice;

    @JsonView(Views.Public.class)
    protected LengthModification lengthModification;

    @JsonView(Views.InternalForClothes.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Employee rotationTemporaryOwner;

    @JsonView(Views.Public.class)
    private LocalDate releasedToEmployeeAsRotation;

    @JsonView(Views.Public.class)
    private boolean releasedToEmployee;

    @JsonView(Views.Public.class)
    boolean rotational;

    public Cloth() {
    }

    public Cloth(
            long id,
            long barcode,
            LocalDate lastWashing,
            Employee employee) {
        this.id = id;
        this.barcode = barcode;
        this.lastWashing = lastWashing;
        this.employee = employee;
    }

    public Cloth(
            int ordinalNumber,
            ClientArticle clientArticle,
            ClothSize size,
            Employee employee,
            Date created
    ) {
        this.ordinalNumber = ordinalNumber;
        this.clientArticle = clientArticle;
        this.size = size;
        this.employee = employee;
        this.created = created;
        active = false;

    }

    public Cloth(
            ClientArticle clientArticle,
            ClothSize size,
            Employee employee,
            Date created
    ) {
        this.size = size;
        this.created = created;
        this.clientArticle = clientArticle;
        this.employee = employee;
        active = false;

    }

    public Cloth(
            long barcode,
            LocalDate assignment,
            LocalDate lastWashing,
            LocalDate releaseDate,
            int ordinalNumber,
            ClientArticle clientArticle,
            ClothSize size
    ) {
        this.barcode = barcode;
        this.assignment = assignment;
        setLastWashing(lastWashing);
        this.releaseDate = releaseDate;
        this.ordinalNumber = ordinalNumber;
        this.clientArticle = clientArticle;
        this.size = size;
        active = true;
    }

    public Cloth(long barcode,
                 LocalDate assignment,
                 LocalDate lastWashing,
                 LocalDate releaseDate,
                 int ordinalNumber,
                 ClientArticle clientArticle,
                 ClothSize size,
                 LengthModification lengthModification,
                 LifeCycleStatus lifeCycleStatus) {
        this.barcode = barcode;
        this.assignment = assignment;
        setLastWashing(lastWashing);
        this.releaseDate = releaseDate;
        this.ordinalNumber = ordinalNumber;
        this.clientArticle = clientArticle;
        this.size = size;
        this.lengthModification = lengthModification;
        active = true;
        this.lifeCycleStatus = lifeCycleStatus;
    }

    public LocalDate getAssignment() {
        return assignment;
    }

    public void setAssignment(LocalDate assignment) {
        this.assignment = assignment;
    }

    public LocalDate getLastWashing() {
        return lastWashing;
    }

    public void setLastWashing(LocalDate lastWashing) {
        this.lastWashing = lastWashing;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public LocalDate getReleasedToEmployeeAsRotation() {
        return releasedToEmployeeAsRotation;
    }

    public void setReleasedToEmployeeAsRotation(LocalDate releasedToEmployeeAsRotation) {
        this.releasedToEmployeeAsRotation = releasedToEmployeeAsRotation;
    }

    public boolean isReleasedToEmployee() {
        return releasedToEmployee;
    }

    public void setReleasedToEmployee(boolean releasedToEmployee) {
        this.releasedToEmployee = releasedToEmployee;
    }

    public boolean isRotational() {
        return rotational;
    }

    public void setRotational(boolean rotational) {
        this.rotational = rotational;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ClothSize getSize() {
        return size;
    }

    public void setSize(ClothSize size) {
        this.size = size;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getOrdinalNumber() {
        return ordinalNumber;
    }

    public void setOrdinalNumber(int ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }

    public ClientArticle getClientArticle() {
        return clientArticle;
    }

    public void setClientArticle(ClientArticle clientArticle) {
        this.clientArticle = clientArticle;
    }

    public ClothOrder getExchangeOrder() {
        return exchangeOrder;
    }

    public void setExchangeOrder(ClothOrder exchangeOrder) {
        this.exchangeOrder = exchangeOrder;
    }

    public void addExchangeOrder(ClothOrder exchangeOrder) {
        exchangeOrder.setClothToRelease(this);
        this.exchangeOrder = exchangeOrder;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<ClothStatus> getStatusHistory() {
        if(statusHistory == null) {
            return new LinkedList<>();
        } else {
            return statusHistory;
        }
    }

    public ClothStatus getClothStatus() {
        if(statusHistory == null) {
            return new ClothStatus();
        } else {
            return statusHistory.get(statusHistory.size() - 1);
        }
    }

    public void setStatusHistory(List<ClothStatus> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public void setStatus(ClothStatus status) {
        status.setCloth(this);
        if (statusHistory == null) statusHistory = new LinkedList<>();
        statusHistory.add(status);
    }

    public Double getActualRedemptionPrice() {
        return actualRedemptionPrice;
    }

    public void setActualRedemptionPrice(Double actualRedemptionPrice) {
        this.actualRedemptionPrice = actualRedemptionPrice;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }



    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ClothOrder getReleaseOrder() {
        return releaseOrder;
    }

    public void setReleaseOrder(ClothOrder releaseOrder) {
        this.releaseOrder = releaseOrder;
    }

    public LengthModification getLengthModification() {
        return lengthModification;
    }

    public void setLengthModification(LengthModification lengthModification) {
        this.lengthModification = lengthModification;
    }

    public Employee getRotationTemporaryOwner() {
        return rotationTemporaryOwner;
    }

    public void setRotationTemporaryOwner(Employee rotationTemporaryOwner) {
        this.rotationTemporaryOwner = rotationTemporaryOwner;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cloth cloth = (Cloth) o;
        return getBarcode() == cloth.getBarcode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return getClientArticle().getArticle().getName() + " " + getSize() + " " + "lp. " +
                getOrdinalNumber();
    }

    public long getClientId() {
        return getEmployee().getDepartment().getClient().getId();
    }

    public LifeCycleStatus getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    public void setLifeCycleStatus(LifeCycleStatus lifeCycleStatus) {
        this.lifeCycleStatus = lifeCycleStatus;
    }


    @Override
    public int compareTo(Cloth o) {
        return Comparator.comparing(Cloth::getClientArticle)
                .thenComparing(Cloth::getSize)
                .thenComparing(Cloth::getOrdinalNumber)
                .compare(this, o);
    }
}
