package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.*;


@Entity
public class Locker implements Comparable<Locker>{
    @JsonView(Views.Public.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.Public.class)
    private int lockerNumber;

    @JsonView(Views.Public.class)
    private int capacity;

    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @JsonView(Views.InternalForLockers.class)
    @OneToMany(mappedBy = "locker", cascade = CascadeType.ALL)
    private List<Box> boxes;

    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private Plant plant;

    public Locker() {
    }

    public Locker(int lockerNumber, int capacity, Plant plant,
                  Department department, Location location,
                  List<Box> boxes) {
        this.lockerNumber = lockerNumber;
        this.capacity = capacity;
        this.plant = plant;
        this.department = department;
        this.location = location;
        this.boxes = boxes;
    }

    public Locker(int lockerNumber, int capacity) {
        this.lockerNumber = lockerNumber;
        this.capacity = capacity;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    public void addBoxes(List<Box> boxes) {
        for(Box b : boxes) {
            b.setLocker(this);
        }
        setBoxes(boxes);
    }

    public void addBox(Box box) {
        if(boxes == null) {
            boxes = new LinkedList<>();
        }
            box.setLocker(this);
            boxes.add(box);
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLockerNumber() {
        return lockerNumber;
    }

    public void setLockerNumber(int lockerNumber) {
        this.lockerNumber = lockerNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    @Override
    public String toString() {
        return plant.toString() + " " + lockerNumber;
    }

    @Override
    public int compareTo(Locker o) {
        return Comparator.comparing(Locker::getPlant)
                .thenComparing(Locker::getLockerNumber)
                .compare(this, o);
    }
}
