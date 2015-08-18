package reaper.appserver.core.app.controller;

import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;

public class NotificationController extends BaseController
{
    public NotificationController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
    }

    public void registerAction()
    {
        String userId = request.getData("user_id");
        String token = request.getData("token");

        response.set("message", "Hello, World!");
    }
}
