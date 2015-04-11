package reaper.appserver.core.framework.server;

import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.core.framework.controller.Controller;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ResourceNotFound;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.Response;
import reaper.appserver.core.framework.response.ResponseFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ApplicationServer
{
    private static final String CONTROLLER_PACKAGE = ConfLoader.getConf(ConfResource.SOURCE).get("source.package.controller") + ".";

    public Response dispatch(Request request, ResponseFactory responseFactory) throws ResourceNotFound, BadRequest, ServerError
    {
        Class<? extends Controller> controllerClass = getControllerClass(request);
        if (controllerClass == null)
        {
            throw new ResourceNotFound();
        }

        Controller controller = getControllerInstance(controllerClass, request, responseFactory);

        Response response = controller.run();

        return response;
    }

    private Class<? extends Controller> getControllerClass(Request request)
    {
        String controllerName = CONTROLLER_PACKAGE + request.getController();

        Class<? extends Controller> controller;
        try
        {
            controller = (Class<? extends Controller>) Class.forName(controllerName);
        }
        catch (ClassNotFoundException e)
        {
            controller = null;
        }
        return controller;
    }

    private Controller getControllerInstance(Class<? extends Controller> controller, Request request,
                                             ResponseFactory responseFactory)
    {

		/*
         * Load the constructor that takes Request object as the only parameter
		 */
        Constructor<?> constructor = null;
        try
        {
            constructor = controller.getDeclaredConstructor(Request.class, ResponseFactory.class);
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            // Not possible; AbstractController has this constructor
        }

		/*
         * Create an instance of the required Controller
		 */
        Controller controllerInstance = null;
        try
        {
            controllerInstance = controller.cast(constructor.newInstance(request, responseFactory));
        }
        catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e)
        {
            // Not Possible
        }

        return controllerInstance;
    }
}
