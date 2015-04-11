package reaper.appserver.api.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import reaper.appserver.core.framework.exceptions.BadRequest;

import java.lang.reflect.Type;
import java.util.Map;

public class ApiRequestFactory
{
    private static final String URI_KEY = "_URI";
    private static final String USER_KEY = "_ME";

    public ApiRequest create(String json) throws BadRequest
    {
        try
        {
            Type type = new TypeToken<Map<String, String>>()
            {
            }.getType();
            Map<String, String> requestData = (new Gson()).fromJson(json, type);

            String uri = requestData.remove(URI_KEY);
            String sessionUser = requestData.remove(USER_KEY);

            String controller = RequestProcessor.getController(uri);
            String action = RequestProcessor.getAction(uri);

            ApiRequest apiRequest = new ApiRequest(controller, action, requestData, sessionUser);
            return apiRequest;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BadRequest();
        }
    }
}
