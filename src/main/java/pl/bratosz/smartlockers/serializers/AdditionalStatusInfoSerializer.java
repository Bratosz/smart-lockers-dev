package pl.bratosz.smartlockers.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import pl.bratosz.smartlockers.model.clothes.AdditionalStatusInfo;


import java.io.IOException;

public class AdditionalStatusInfoSerializer extends StdSerializer<AdditionalStatusInfo> {

    public AdditionalStatusInfoSerializer() {
        super(AdditionalStatusInfo.class);
    }

    public AdditionalStatusInfoSerializer(Class t) {
        super(t);
    }

    public void serialize(
            AdditionalStatusInfo additionalInfo,
            JsonGenerator generator,
            SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("enumValue");
        generator.writeString(additionalInfo.name());
        generator.writeFieldName("polishName");
        generator.writeString(additionalInfo.getName());
        generator.writeEndObject();
    }
}

