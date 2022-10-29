package pl.bratosz.smartlockers.converters;

import org.springframework.stereotype.Component;
import pl.bratosz.smartlockers.model.Box;

@Component
public class BoxStatusConverter extends EnumConverter<Box.BoxStatus> {
    public BoxStatusConverter() {
        super(Box.BoxStatus.class);
    }
}
