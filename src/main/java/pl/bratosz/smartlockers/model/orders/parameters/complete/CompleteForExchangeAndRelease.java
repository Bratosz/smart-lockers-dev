package pl.bratosz.smartlockers.model.orders.parameters.complete;

import pl.bratosz.smartlockers.model.orders.ExchangeStrategy;
import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForExchangeAndRelease;

public interface CompleteForExchangeAndRelease extends ParametersForExchangeAndRelease {
    boolean isOrderActive();
    OrderStatus getOrderStatus();
    ExchangeStrategy getExchangeStrategy();
}
