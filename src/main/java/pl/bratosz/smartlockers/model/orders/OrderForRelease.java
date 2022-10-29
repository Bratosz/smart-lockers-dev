package pl.bratosz.smartlockers.model.orders;

import pl.bratosz.smartlockers.model.clothes.Cloth;

public interface OrderForRelease extends BasicOrder {
    Cloth getClothToRelease();

    void setClothToRelease(Cloth cloth);
}
