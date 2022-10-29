package pl.bratosz.smartlockers.service.pasting.employee;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.*;
import pl.bratosz.smartlockers.service.jgenderize.model.Gender;
import pl.bratosz.smartlockers.utils.SameClient;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Entity
public class EmployeeToCreate implements SameClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private long id;

    @JsonView(Views.Public.class)
    private String personalNumber;

    @JsonView(Views.Public.class)
    private String lastName;

    @JsonView(Views.Public.class)
    private String firstName;

    @JsonView(Views.Public.class)
    private Gender gender;

    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private Position position;

    @JsonView(Views.Public.class)
    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    public EmployeeToCreate() {
    }

    public static EmployeeToCreate createWithDepartmentPositionAndLocation(
            PastedEmployeeEDPL e, Department d, Location l, Position p, Client c, boolean netIsAvailable) {
        EmployeeToCreate newEmployee = new EmployeeToCreate();
        newEmployee.setPersonalNumber(e.getPersonalNumber());
        if(netIsAvailable) {
            newEmployee.setFirstName(e.getFirstName());
            newEmployee.setLastName(e.getLastName());
            newEmployee.setGender(e.getGender());
        } else {
            String[] split = e.getPastedEmployeeName().split("\\s+");
            List<String> names = Arrays.stream(split).collect(Collectors.toList());
            newEmployee.setLastName(names.get(0));
            newEmployee.setFirstName(names.get(1));
            newEmployee.setGender(Gender.NULL);
        }
        newEmployee.setDepartment(d);
        newEmployee.setLocation(l);
        newEmployee.setPosition(p);
        newEmployee.setClient(c);
        return newEmployee;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Client getClient() {
        return client;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }
}
