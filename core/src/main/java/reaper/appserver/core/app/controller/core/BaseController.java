package reaper.appserver.core.app.controller.core;

import reaper.appserver.core.app.service.user.UserService;
import reaper.appserver.core.framework.controller.ApplicationController;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;
import reaper.appserver.persistence.model.user.User;

public abstract class BaseController extends ApplicationController
{
    protected UserService userService;
    protected User activeUser;

    public BaseController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
        userService = new UserService();
    }

    @Override
    protected void preProcess()
    {
        activeUser = userService.get(request.getSessionUser());
        if (activeUser == null)
        {
            throw new ServerError("Invalid Session User");
        }
    }

    @Override
    protected void postProcess()
    {

    }
}
