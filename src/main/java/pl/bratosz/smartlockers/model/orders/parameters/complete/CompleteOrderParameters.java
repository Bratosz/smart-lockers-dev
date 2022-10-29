package pl.bratosz.smartlockers.model.orders.parameters.complete;

import pl.bratosz.smartlockers.model.Employee;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.orders.ExchangeStrategy;
import pl.bratosz.smartlockers.model.orders.MainOrder;
import pl.bratosz.smartlockers.model.orders.OrderStatus;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.model.orders.parameters.basic.BasicOrderParameters;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForExchangeAndRelease;
import pl.bratosz.smartlockers.model.orders.parameters.basic.ParametersForRelease;
import pl.bratosz.smartlockers.model.users.User;

import static pl.bratosz.smartlockers.model.orders.OrderStatus.*;

public class CompleteOrderParameters extends BasicOrderParameters
        implements
        CompleteForExchangeAndRelease, CompleteForRelease {

    private OrderStatus orderStatus;
    private boolean orderActive;
    private ExchangeStrategy exchangeStrategy;

    public static CompleteOrderParameters createForNewAdditionalCloth(
            Cloth clothForRelease,
            OrderStatus clothOrderStatus,
            MainOrder mainOrder,
            User user) {
        return new CompleteOrderParameters(
                clothForRelease,
                clothForRelease,
                mainOrder.getEmployee(),
                mainOrder.getOrderType(),
                clothOrderStatus,
                mainOrder.getExchangeStrategy(),
                user);
    }

    public static CompleteForExchangeAndRelease createForClothExchangeAndRelease(
            Cloth clothForExchange,
            Cloth clothForRelease,
            Employee employee,
            OrderType orderType,
            OrderStatus orderStatus,
            ExchangeStrategy exchangeStrategy,
            User user) {
        return new CompleteOrderParameters(
                clothForExchange,
                clothForRelease,
                employee,
                orderType,
                orderStatus,
                exchangeStrategy,
                user
        );
    }


    public static CompleteForRelease createForNewArticle(
            Cloth clothForRelease,
            Employee employee,
            OrderStatus orderStatus,
            User user
    ) {
        OrderType orderType = OrderType.NEW_ARTICLE;
        return new CompleteOrderParameters(
                clothForRelease,
                employee,
                orderType,
                orderStatus,
                user
        );
    }

    public static CompleteForRelease createOrderForExistingCloth(
            Cloth cloth,
            Employee employee,
            OrderType orderType,
            OrderStatus orderStatus,
            User user
    ) {
        return new CompleteOrderParameters(
                cloth,
                employee,
                orderType,
                orderStatus,
                user
        );
    }


    private CompleteOrderParameters(Cloth clothForRelease,
                                    Employee employee,
                                    OrderType orderType,
                                    OrderStatus orderStatus,
                                    User user
    ) {
        super(clothForRelease, employee, orderType, user);
        this.orderStatus = orderStatus;
    }


    private CompleteOrderParameters(Cloth clothForExchange,
                                    Cloth clothForRelease,
                                    Employee employee,
                                    OrderType orderType,
                                    OrderStatus orderStatus,
                                    ExchangeStrategy exchangeStrategy,
                                    User user
    ) {
        super(clothForExchange, clothForRelease, employee, orderType, user);
        this.orderStatus = orderStatus;
        this.exchangeStrategy = exchangeStrategy;
    }


    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public ExchangeStrategy getExchangeStrategy() {
        return exchangeStrategy;
    }

    public boolean isOrderActive() {
        return orderActive;
    }

}
