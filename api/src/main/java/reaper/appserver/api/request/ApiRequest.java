package reaper.appserver.api.request;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import reaper.appserver.core.framework.request.Request;

import java.util.Map;

public class ApiRequest implements Request
{
    private static Gson gson = Converters.registerAll(new GsonBuilder()).create();

    private String controller;
    private String action;
    private Map<String, String> data;
    private String sessionUser;

    public ApiRequest(String controller, String action, Map<String, String> data, String sessionUser)
    {
        this.controller = controller;
        this.action = action;
        this.data = data;
        this.sessionUser = sessionUser;
    }

    @Override
    public String getController()
    {
        return controller;
    }

    @Override
    public String getAction()
    {
        return action;
    }

    @Override
    public String getData(String key)
    {
        return data.get(key);
    }

    @Override
    public String getSessionUser()
    {
        return sessionUser;
    }

    @Override
    public String toString()
    {
        return gson.toJson(this);
    }
}
