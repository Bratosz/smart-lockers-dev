package pl.bratosz.smartlockers.model.orders;

import pl.bratosz.smartlockers.model.clothes.Cloth;

public interface OrderForExchangeAndRelease extends OrderForRelease {
    Cloth getClothToExchange();

    void setClothToExchange(Cloth cloth);
}
