package reaper.appserver.core.app.controller;

import com.google.gson.reflect.TypeToken;
import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;
import reaper.appserver.core.framework.util.GsonProvider;

import java.lang.reflect.Type;
import java.util.List;

public class RegisterController extends BaseController
{
    public RegisterController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
    }

    @Override
    protected void preProcess()
    {
    }

    public void mainAction()
    {
        String userId = request.getData("id");
        String username = request.getData("email");
        String firstname = request.getData("first_name");
        String lastname = request.getData("last_name");
        String gender = request.getData("gender");
        List<String> friends;

        try
        {
            String userIdJson = request.getData("friends");
            Type type = new TypeToken<List<String>>()
            {
            }.getType();

            friends = GsonProvider.getGson().fromJson(userIdJson, type);

            if (friends == null)
            {
                throw new NullPointerException();
            }
        }
        catch (Exception e)
        {
            throw new BadRequest("Invalid list<friend_id> to fetch add friends");
        }

        userService.create(userId, username, firstname, lastname, gender, friends);

        response.set("user_id", userId);
    }
}
