package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.date.LocalDateConverter;
import pl.bratosz.smartlockers.model.users.UserClient;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Plant implements Comparable<Plant> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.Public.class)
    private int plantNumber;

    @JsonView(Views.PlantBasicInfo.class)
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL)
    private Set<Location> locations;

    @JsonView(Views.PlantBasicInfo.class)
    @ManyToMany(mappedBy = "plants")
    private Set<Department> departments;

    @JsonView(Views.PlantExtendedInfo.class)
    @ManyToOne(cascade = CascadeType.ALL)
    private Address address;

    @JsonView(Views.PlantExtendedInfo.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @JsonView(Views.PlantExtendedInfo.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "plant", cascade = CascadeType.ALL)
    private Set<Locker> lockers;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL)
    private Set<UserClient> userClients;

    @JsonView(Views.PlantBasicInfo.class)
    private String login;

    private String password;

    private LocalDate lastUpdate;

    private boolean lockersLoaded;

    private boolean clothesLoaded;

    public Plant() {
    }

    public Plant(
            String name, int plantNumber, Address address, Set<Department> departments,
            Client client, Set<Locker> lockers, Set<Location> locations) {
        this.name = name;
        this.plantNumber = plantNumber;
        this.address = address;
        this.departments = departments;
        this.client = client;
        this.lockers = lockers;
        this.locations = locations;
    }

    public Plant(int plantNumber, String name, String login, String password) {
        this.plantNumber = plantNumber;
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public Plant(int plantNumber) {
        this.plantNumber = plantNumber;
        this.name = plantNumber + " GŁÓWNY";
        this.login = "admin";
        this.password = "admin";
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

    public int getPlantNumber() {
        return plantNumber;
    }

    public void setPlantNumber(int plantNumber) {
        this.plantNumber = plantNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Department> getDepartments() {
        if (departments == null) {
            return new HashSet<>();
        } else {
            return departments;
        }
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Locker> getLockers() {
        return lockers;
    }

    public void setLockers(Set<Locker> lockers) {
        this.lockers = lockers;
    }

    public Set<Location> getLocations() {
        if (locations == null)
            return new HashSet<>();
        else
            return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<UserClient> getUserClients() {
        return userClients;
    }

    public void setUserClients(Set<UserClient> userClients) {
        this.userClients = userClients;
    }

    public LocalDate getLastUpdate() {
        if(lastUpdate == null) {
            return LocalDateConverter.getDefaultDate();
        }
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isLockersLoaded() {
        return lockersLoaded;
    }

    public void setLockersLoaded(boolean lockersLoaded) {
        this.lockersLoaded = lockersLoaded;
    }

    public boolean isClothesLoaded() {
        return clothesLoaded;
    }

    public void setClothesLoaded(boolean clothesLoaded) {
        this.clothesLoaded = clothesLoaded;
    }


    @Override
    public String toString() {
        return plantNumber + "";
    }

    @Override
    public int compareTo(Plant o) {
        return Comparator.comparing(Plant::getPlantNumber)
                .compare(this, o);
    }
}
