package pl.bratosz.smartlockers.service.exels;

import pl.bratosz.smartlockers.converters.ConvertableEnum;

public enum LoadType implements ConvertableEnum {
    CLOTHES_ROTATION_UPLOAD,
    CLOTHES_ROTATION_UPDATE,
    RELEASED_ROTATIONAL_CLOTHING_UPDATE,
    BASIC_DATA_BASE_UPLOAD,
    DETAILED_DATA_BASE_UPLOAD,
    ;

    @Override
    public String getName() {
        return this.name();
    }
}
