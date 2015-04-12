package reaper.appserver.core.app.controller;

import reaper.appserver.core.app.controller.core.BaseController;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.ResponseFactory;

public class EventController extends BaseController
{
    public EventController(Request request, ResponseFactory responseFactory)
    {
        super(request, responseFactory);
    }

    public void summaryAction()
    {
        response.set("events", "This is he events summary list");
    }
}
