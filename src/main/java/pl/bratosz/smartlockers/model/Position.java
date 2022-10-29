package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.utils.SameClient;

import javax.persistence.*;
import java.util.*;

@Entity
public class Position implements SameClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private String name;

    @OneToMany(mappedBy = "position")
    private Set<Employee> employees;

    @ManyToOne
    private Client client;

    @ManyToMany(mappedBy = "positions")
    @JsonView(Views.PositionBasicInfo.class)
    private Set<Department> departments;

    @OneToMany(mappedBy = "position")
    @OrderBy("id")
    @JsonView(Views.Public.class)
    private Set<ArticleWithQuantity> articlesWithQuantities;

    @JsonView(Views.Public.class)
    private boolean surrogate;

    public Position() {
    }

    public Position(String name, Client client) {
        setName(name);
        setClient(client);
    }

    public Position(String n, Client c, boolean b) {
        setName(n);
        setClient(c);
        setSurrogate(b);
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

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
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

    public void addDepartment(Department department) {
        if(departments == null) {
            departments = new HashSet<>();
        }
        department.addPosition(this);
        departments.add(department);
    }

    public void addClothTypeWithQuantity(ArticleWithQuantity articleWithQuantity) {
        if(articlesWithQuantities == null) {
            articlesWithQuantities = new LinkedHashSet<>();
        }
        articleWithQuantity.setPosition(this);
        articlesWithQuantities.add(articleWithQuantity);
    }

    public Set<ArticleWithQuantity> getArticlesWithQuantities() {
        return articlesWithQuantities;
    }

    public void setArticlesWithQuantities(HashSet<ArticleWithQuantity> articlesWithQuantities) {
        this.articlesWithQuantities = articlesWithQuantities;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isSurrogate() {
        return surrogate;
    }

    public void setSurrogate(boolean surrogate) {
        this.surrogate = surrogate;
    }
}
