package pl.bratosz.smartlockers.model.users;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.orders.OrderStatus;



import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class UserOurStaff extends User {

    @OneToOne
    @JsonView(Views.Public.class)
    private ManagementList managementList;

    private OurStaffPosition ourStaffPosition;

    public UserOurStaff() {
    }

    public UserOurStaff(OrderStatus.OrderStage initialStageForOrders,
                        String firstName,
                        String lastName,
                        String login,
                        String password,
                        String email,
                        OurStaffPosition ourStaffPosition) {
        super(initialStageForOrders, firstName, lastName, login, password, email);
        this.ourStaffPosition = ourStaffPosition;
    }

    public OurStaffPosition getOurStaffPosition() {
        return ourStaffPosition;
    }

    public void setOurStaffPosition(OurStaffPosition ourStaffPosition) {
        this.ourStaffPosition = ourStaffPosition;
    }

    public ManagementList getManagementList() {
        return managementList;
    }

    public void setManagementList(ManagementList managementList) {
        this.managementList = managementList;
    }
}
