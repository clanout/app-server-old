package reaper.appserver.persistence.core;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

public class GsonProvider
{
    private static Gson gson;
    private static GsonBuilder gsonBuilder;

    static
    {
        gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new DateTimeDeserializer())
                .registerTypeAdapter(OffsetDateTime.class, new DateTimeSerializer());

        gson = gsonBuilder.create();
    }

    public static Gson getGson()
    {
        return gson;
    }

    public static GsonBuilder getGsonBuilder()
    {
        return gsonBuilder;
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
