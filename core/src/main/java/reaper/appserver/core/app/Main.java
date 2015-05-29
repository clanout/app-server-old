package reaper.appserver.core.app;

import com.google.gson.*;
import com.squareup.okhttp.OkHttpClient;
import reaper.appserver.persistence.model.event.Event;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.POST;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main
{
    private static Gson gson = (new GsonBuilder()).registerTypeAdapter(OffsetDateTime.class, new DateTimeParser()).create();

    public static void main(String[] args) throws Exception
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(new OkHttpClient()))
                .setConverter(new GsonConverter(gson))
                .setEndpoint("http://localhost:8080/").build();

        EventsApi eventsApi = restAdapter.create(EventsApi.class);

        Map<String, String> postData = new HashMap<>();
        postData.put("_URI", "event/summary");
        postData.put("_ME", "9320369679");
        postData.put("zone", "Bengaluru");

        Response response = eventsApi.getVisibleEvents(postData);
        System.out.println(response.getStatus());
//        System.out.println(response.getBody().);
    }

    public static interface EventsApi
    {
        @POST("/event/summary")
        public Response getVisibleEvents(@Body Map<String, String> postData);
    }

    public static class VisibleEventsApiResponse
    {
        public List<Event> events;
    }

    private static class DateTimeParser implements JsonDeserializer<OffsetDateTime>
    {
        @Override
        public OffsetDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
        {
            return OffsetDateTime.parse(jsonElement.getAsString());
        }
    }
}
