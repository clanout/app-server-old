package reaper.appserver.core.app.controller;

import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.app.service.notification.NotificationService;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;

import java.util.List;

public class NotificationController extends BaseController
{
    private NotificationService notificationService;

    public NotificationController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
        notificationService = new NotificationService();
    }

    public void registerAction()
    {
        String token = request.getData("token");

        if(token == null || token.isEmpty())
        {
            throw new BadRequest("token cannot be null");
        }

        notificationService.register(activeUser.getId(), token);
    }

    public void pullAction()
    {
        List<String> notifications = notificationService.pull(activeUser.getId());
        response.set("notification", notifications);
    }
}
