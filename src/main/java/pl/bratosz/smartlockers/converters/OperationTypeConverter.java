package pl.bratosz.smartlockers.converters;

import org.springframework.stereotype.Component;
import pl.bratosz.smartlockers.service.exels.LoadType;

@Component
public class OperationTypeConverter extends EnumConverter<LoadType> {
    public OperationTypeConverter() {
        super(LoadType.class);
    }
}
