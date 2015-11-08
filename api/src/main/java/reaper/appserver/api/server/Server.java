package reaper.appserver.api.server;


import org.apache.log4j.Logger;
import reaper.appserver.api.request.ApiRequest;
import reaper.appserver.api.request.ApiRequestFactory;
import reaper.appserver.api.response.ApiResponse;
import reaper.appserver.api.response.ApiResponseFactory;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ResourceNotFound;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.server.ApplicationServer;
import reaper.appserver.log.LogUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("{uri: .*}")
public class Server
{
    private static Logger log = LogUtil.getLogger(Server.class);

    @GET
    public Response regularGet()
    {
        return Response.ok("SERVER HEALTHY").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response regularPost(String input)
    {
        try
        {
            ApiRequest apiRequest = (new ApiRequestFactory()).create(input);
            log.info("[REQUEST] " + apiRequest.toString());

            ApiResponseFactory apiResponseFactory = new ApiResponseFactory();

            ApplicationServer applicationServer = new ApplicationServer();

            ApiResponse apiResponse = (ApiResponse) applicationServer.dispatch(apiRequest, apiResponseFactory);
            log.info("[RESPONSE] " + apiResponse.toString());

            return Response.ok(apiResponse.toString(), MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (ResourceNotFound e)
        {
            log.error(e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        catch (BadRequest e)
        {
            log.error(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (ServerError e)
        {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}