package pl.bratosz.smartlockers.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class DepartmentAlias {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @JsonView(Views.Public.class)
    private String alias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(Views.PrivateView.class)
    private Department department;

    public DepartmentAlias() {
    }

    public DepartmentAlias(String alias) {
        setAlias(alias);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (alias == o) return true;
        if (o == null || alias.getClass() != o.getClass()) return false;
        String that = (String) o;
        return Objects.equals(alias, that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAlias());
    }


}
