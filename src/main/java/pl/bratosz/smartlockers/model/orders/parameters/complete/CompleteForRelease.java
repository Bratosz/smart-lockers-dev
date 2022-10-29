package pl.bratosz.smartlockers.model.orders.parameters.complete;

import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForRelease;

public interface CompleteForRelease extends ParametersForRelease {
    boolean isOrderActive();
    OrderStatus getOrderStatus();
}
