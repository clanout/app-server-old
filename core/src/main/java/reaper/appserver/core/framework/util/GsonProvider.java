package reaper.appserver.core.framework.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

public class GsonProvider
{
    private static Gson gson;

    static
    {
        gson = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new DateTimeDeserializer())
                .registerTypeAdapter(OffsetDateTime.class, new DateTimeSerializer())
                .create();
    }

    public static Gson getGson()
    {
        return gson;
    }

    private static class DateTimeSerializer implements JsonSerializer<OffsetDateTime>
    {
        @Override
        public JsonElement serialize(OffsetDateTime offsetDateTime, Type type, JsonSerializationContext jsonSerializationContext)
        {
            return new JsonPrimitive(offsetDateTime.toString());
        }
    }

    private static class DateTimeDeserializer implements JsonDeserializer<OffsetDateTime>
    {
        @Override
        public OffsetDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
        {
            return OffsetDateTime.parse(jsonElement.getAsString());
        }
    }
}
