package pl.bratosz.smartlockers.model.users;

import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.orders.OrderStatus;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class UserEmployee extends UserClient {

    @OneToOne(cascade = CascadeType.ALL)
    private Employee employee;

    public UserEmployee(){}

    public UserEmployee(OrderStatus.OrderStage initialStageForOrders,
                        String firstName,
                        String lastName,
                        String login,
                        String password,
                        String email,
                        Client client,
                        Employee employee) {
        super(initialStageForOrders, firstName, lastName, login, password, email, client);
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
