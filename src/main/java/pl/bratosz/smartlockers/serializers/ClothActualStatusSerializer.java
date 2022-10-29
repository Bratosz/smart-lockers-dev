package pl.bratosz.smartlockers.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import pl.bratosz.smartlockers.model.clothes.ClothActualStatus;

import java.io.IOException;

public class ClothActualStatusSerializer extends StdSerializer<ClothActualStatus> {
    public ClothActualStatusSerializer() {
        super(ClothActualStatus.class);
    }

    public ClothActualStatusSerializer(Class t) {
        super(t);
    }

    public void serialize(
            ClothActualStatus status,
            JsonGenerator generator,
            SerializerProvider provider)
        throws IOException, JsonProcessingException {
        generator.writeStartObject();
        generator.writeFieldName("originalName");
        generator.writeString(status.name());
        generator.writeFieldName("polishName");
        generator.writeString(status.getName());
        generator.writeFieldName("lifeCycleStatus");
        generator.writeString(status.getLifeCycleStatus().name());
        generator.writeEndObject();
    }
}
