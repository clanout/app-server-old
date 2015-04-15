package reaper.appserver.core.app.controller;

import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;

public class RegisterController extends BaseController
{
    public RegisterController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
    }

    public void mainAction()
    {
        String userId = request.getData("id");
        String username = request.getData("email");
        String firstname = request.getData("first_name");
        String lastname = request.getData("last_name");
        String gender = request.getData("gender");

        userService.create(userId, username, firstname, lastname, gender);

        response.set("user_id", userId);
    }
}
