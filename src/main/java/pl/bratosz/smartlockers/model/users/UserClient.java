package pl.bratosz.smartlockers.model.users;

import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.model.orders.OrderStatus;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class UserClient extends User {

    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

    @ManyToOne
    private Department department;

    @ManyToOne
    private Plant plant;

    public UserClient(){
    }

    public UserClient(OrderStatus.OrderStage initialStageForOrders,
                      String firstName,
                      String lastName,
                      String login,
                      String password,
                      String email,
                      Client client) {
        super(initialStageForOrders, firstName, lastName, login, password, email);
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}
