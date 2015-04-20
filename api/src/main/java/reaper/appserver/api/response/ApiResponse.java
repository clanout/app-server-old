package reaper.appserver.api.response;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import reaper.appserver.core.framework.response.Response;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse implements Response
{
    private static Gson gson = Converters.registerAll(new GsonBuilder()).create();

    private Map<String, Object> data;

    public ApiResponse()
    {
        data = new HashMap<>();
    }

    @Override
    public void set(String key, Object value)
    {
        if (key != null)
        {
            data.put(key, value);
        }
    }

    @Override
    public String toString()
    {
        return gson.toJson(data);
    }
}
