package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.utils.SameClient;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Location implements SameClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Client client;

    @JsonView(Views.BasicLocationInfo.class)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Plant plant;

    @ManyToMany
    @JoinTable(
            name = "departments",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    private Set<Department> departments;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private Set<Locker> lockers;

    @JsonView(Views.Public.class)
    private boolean surrogate;

    @JsonView(Views.Public.class)
    private boolean full;

    public Location() {
    }

    public Location(String name, Client client, boolean surrogate, Plant plant) {
        this.name = name;
        this.client = client;
        this.surrogate = surrogate;
        this.plant = plant;
    }

    public Location(String name, Client client, boolean surrogate) {
        this.name = name;
        this.client = client;
        this.surrogate = surrogate;
    }

    public Location(String locationName, Plant plant) {
        this.name = locationName;
        this.plant = plant;
        this.client = plant.getClient();

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public Set<Locker> getLockers() {
        return lockers;
    }

    public void setLockers(Set<Locker> lockers) {
        this.lockers = lockers;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    public boolean isSurrogate() {
        return surrogate;
    }

    public void setSurrogate(boolean surrogate) {
        this.surrogate = surrogate;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }
}
