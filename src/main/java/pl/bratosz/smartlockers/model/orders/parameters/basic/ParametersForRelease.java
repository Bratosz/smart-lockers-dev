package pl.bratosz.smartlockers.model.orders.parameters.basic;

import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.users.User;

import java.util.Date;

public interface ParametersForRelease {
    OrderType getOrderType();
    User getUser();
    Date getDate();
    Cloth getClothToRelease();
    Employee getEmployee();

}
