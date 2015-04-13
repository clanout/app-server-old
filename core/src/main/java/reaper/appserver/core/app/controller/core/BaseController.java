package reaper.appserver.core.app.controller.core;

import reaper.appserver.core.app.service.user.UserService;
import reaper.appserver.core.framework.controller.ApplicationController;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;

public abstract class BaseController extends ApplicationController
{
    protected UserService userService;

    public BaseController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
        userService = new UserService();
    }

    @Override
    protected boolean preProcess()
    {
        return true;
    }

    @Override
    protected boolean postProcess()
    {
        return true;
    }
}
