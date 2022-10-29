package pl.bratosz.smartlockers.model.users;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.clothes.ClothStatus;
import pl.bratosz.smartlockers.model.orders.OrderStatus;

import javax.persistence.*;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    protected OrderStatus.OrderStage initialStageForOrders;

    protected String firstName;

    protected String lastName;

    protected String login;

    protected String password;

    protected String email;

    @OneToMany(mappedBy = "user")
    protected Set<ClothStatus> grantedClothStatuses;

    @OneToMany(mappedBy = "user")
    protected Set<OrderStatus> grantedOrderStatuses;

    @JsonView(Views.Public.class)
    protected long actualClientId;

    public User() {}

    public User(OrderStatus.OrderStage initialStageForOrders, String firstName, String lastName, String login, String password,
                String email) {
        this.initialStageForOrders = initialStageForOrders;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public OrderStatus.OrderStage getInitialStageForOrders() {
        return initialStageForOrders;
    }

    public void setInitialStageForOrders(OrderStatus.OrderStage initialStageForOrders) {
        this.initialStageForOrders = initialStageForOrders;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<ClothStatus> getGrantedClothStatuses() {
        return grantedClothStatuses;
    }

    public void setGrantedClothStatuses(Set<ClothStatus> grantedClothStatuses) {
        this.grantedClothStatuses = grantedClothStatuses;
    }


    public Set<OrderStatus> getGrantedOrderStatuses() {
        return grantedOrderStatuses;
    }

    public void setGrantedOrderStatuses(Set<OrderStatus> grantedOrderStatuses) {
        this.grantedOrderStatuses = grantedOrderStatuses;
    }

    public long getActualClientId() {
        return actualClientId;
    }

    public void setActualClientId(long actualClientId) {
        this.actualClientId = actualClientId;
    }
}
