package pl.bratosz.smartlockers.model.orders.parameters.basic;

import pl.bratosz.smartlockers.model.clothes.Cloth;

public interface ParametersForExchangeAndRelease extends ParametersForRelease {
    Cloth getClothToExchange();
}
