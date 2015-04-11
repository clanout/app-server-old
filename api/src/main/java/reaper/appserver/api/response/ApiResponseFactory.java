package reaper.appserver.api.response;

import reaper.appserver.core.framework.response.Response;
import reaper.appserver.core.framework.response.ResponseFactory;

public class ApiResponseFactory implements ResponseFactory
{
    @Override
    public Response create()
    {
        return new ApiResponse();
    }
}
