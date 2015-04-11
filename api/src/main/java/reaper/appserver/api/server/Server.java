package reaper.appserver.api.server;


import reaper.appserver.api.request.ApiRequest;
import reaper.appserver.api.request.ApiRequestFactory;
import reaper.appserver.api.response.ApiResponse;
import reaper.appserver.api.response.ApiResponseFactory;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ResourceNotFound;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.server.ApplicationServer;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("{uri: .*}")
public class Server
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String regularGet(@Context UriInfo uriInfo)
    {
        return "HelloWorld GET";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response regularPost(String input)
    {
        try
        {
            ApiRequest apiRequest = (new ApiRequestFactory()).create(input);

            ApiResponseFactory apiResponseFactory = new ApiResponseFactory();

            ApplicationServer applicationServer = new ApplicationServer();

            ApiResponse apiResponse = (ApiResponse) applicationServer.dispatch(apiRequest, apiResponseFactory);

            return Response.ok(apiResponse.toString(), MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (ResourceNotFound e)
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        catch (BadRequest e)
        {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (ServerError e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}