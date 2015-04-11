package reaper.appserver.core.framework.controller;

import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ResourceNotFound;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.response.Response;

public interface Controller
{
    public Response run() throws ResourceNotFound, BadRequest, ServerError;
}
