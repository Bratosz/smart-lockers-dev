package pl.bratosz.smartlockers.converters;

import org.springframework.stereotype.Component;
import pl.bratosz.smartlockers.model.clothes.ClothSize;

@Component
public class ClothSizeConverter extends EnumConverter<ClothSize> {
    public ClothSizeConverter() {
        super(ClothSize.class);
    }
}
