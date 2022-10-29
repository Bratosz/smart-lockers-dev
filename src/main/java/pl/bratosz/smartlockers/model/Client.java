package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.orders.ExchangeStrategy;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.service.pasting.employee.EmployeeToCreate;

import javax.persistence.*;
import java.util.*;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.ClientBasicInfo.class)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Plant> plants;

    @JsonView(Views.ClientBasicInfo.class)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Department> departments;

    @JsonView(Views.ClientBasicInfo.class)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<Location> locations;

    @JsonView(Views.Public.class)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private Set<ClientArticle> articles;

    @JsonView(Views.Public.class)
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<EmployeeToCreate> employeesToCreate;

    @ElementCollection
    @CollectionTable(name = "exchange_strategies",
    joinColumns = {@JoinColumn(name = "client_id", referencedColumnName = "id")})
    @MapKeyEnumerated(EnumType.ORDINAL)
    private Map<OrderType, ExchangeStrategy> exchangeStrategies;

    @JsonView(Views.ClientBasicInfo.class)
    @OneToMany(mappedBy = "client")
    private Set<Position> positions;

    @JsonView(Views.Public.class)
    @OneToOne
    private MeasurementList measurementList;

    public Client() {
    }

    public Client(String name) {
        this.name = name;
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

    public Set<Plant> getPlants() {
        if(plants == null ) {
            return new HashSet<>();
        }
        return plants;
    }

    public void setPlants(Set<Plant> plants) {
        this.plants = plants;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public Set<Department> getDepartments() {
        if(departments == null) {
            return new HashSet<>();
        } else {
            return departments;
        }
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    public Department getDepartmentByName(String departmentName) {
        Optional<Department> departmentOpt = departments.stream().filter(d -> d.getName().equals(departmentName))
                .findFirst();
        return departmentOpt.orElseThrow(NoSuchElementException::new);
    }


    public Location getLocationByName(String locationName) {
        Optional<Location> locationOpt = locations.stream().filter(l -> l.getName().equals(locationName))
                .findFirst();
        return locationOpt.orElseThrow(NoSuchElementException::new);
    }

    public Plant getPlantByNumber(int plantNumber) {
        Optional<Plant> plantOpt = plants.stream().filter(p -> p.getPlantNumber() == plantNumber)
                .findFirst();
        return plantOpt.orElseThrow(NoSuchElementException::new);
    }

    public Set<ClientArticle> getArticles() {
        return articles;
    }

    public void setArticles(Set<ClientArticle> articles) {
        this.articles = articles;
    }

    public void addArticle(ClientArticle article) {
        if(this.articles == null)
            this.articles = new HashSet<>();
        this.articles.add(article);
    }

    public Map<OrderType, ExchangeStrategy> getExchangeStrategies() {
        return exchangeStrategies;
    }

    public void setExchangeStrategies(Map<OrderType, ExchangeStrategy> exchangeStrategies) {
        this.exchangeStrategies = exchangeStrategies;
    }

    public Set<Position> getPositions() {
        return positions;
    }

    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    public MeasurementList getMeasurementList() {
        return measurementList;
    }

    public void setMeasurementList(MeasurementList measurementList) {
        this.measurementList = measurementList;
    }

    public void addMeasurementList(MeasurementList measurementList) {
        measurementList.setClient(this);
        this.setMeasurementList(measurementList);
    }

    public List<EmployeeToCreate> getEmployeesToCreate() {
        if(employeesToCreate == null) {
            employeesToCreate = new LinkedList<>();
        }
        return employeesToCreate;
    }

    public void setEmployeesToCreate(List<EmployeeToCreate> employeesToCreate) {
        this.employeesToCreate = employeesToCreate;
    }
}
