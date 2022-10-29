package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class MeasurementList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany
    @JsonView(Views.Public.class)
    private Set<Employee> employeesToMeasure;

    @OneToMany
    @JsonView(Views.Public.class)
    private Set<Employee> employeesToAssign;

    @OneToMany
    @JsonView(Views.Public.class)
    private Set<Employee> employeesToRelease;

    @OneToOne
    private Client client;

    public MeasurementList() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Employee> getEmployeesToMeasure() {
        if(employeesToMeasure == null) {
            return new LinkedHashSet<>();
        }
        return employeesToMeasure;
    }

    public void setEmployeesToMeasure(Set<Employee> employeesToMeasure) {
        this.employeesToMeasure = employeesToMeasure;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        client.setMeasurementList(this);
        this.client = client;
    }

    public void addEmployee(Employee employee) {
        if(employeesToMeasure == null) {
            employeesToMeasure = new LinkedHashSet<>();
        }
        employeesToMeasure.add(employee);
    }

    public Set<Employee> getEmployeesToAssign() {
        return employeesToAssign;
    }

    public void setEmployeesToAssign(Set<Employee> employeesToAssign) {
        this.employeesToAssign = employeesToAssign;
    }

    public Set<Employee> getEmployeesToRelease() {
        return employeesToRelease;
    }

    public void setEmployeesToRelease(Set<Employee> employeesToRelease) {
        this.employeesToRelease = employeesToRelease;
    }
}
