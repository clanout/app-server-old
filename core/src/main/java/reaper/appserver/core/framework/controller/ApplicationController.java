package reaper.appserver.core.framework.controller;

import org.apache.log4j.Logger;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ResourceNotFound;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.request.Request;
import reaper.appserver.core.framework.response.Response;
import reaper.appserver.core.framework.response.ResponseFactory;
import reaper.appserver.log.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ApplicationController implements Controller
{
    protected Logger log;

    protected Request request;
    protected Response response;

    public ApplicationController(Request request, ResponseFactory responseFactory)
    {
        this.log = LogUtil.getLogger(this.getClass());

        this.request = request;
        this.response = responseFactory.create();
    }

    protected abstract boolean preProcess();

    protected abstract boolean postProcess();

    @Override
    public Response run() throws ResourceNotFound, BadRequest, ServerError
    {
        try
        {
            if (!preProcess())
            {
                throw new ServerError();
            }

            execute();

            if (!postProcess())
            {
                throw new ServerError();
            }
        }
        catch (ResourceNotFound e)
        {
            throw e;
        }
        catch (BadRequest e)
        {
            throw e;
        }
        catch (ServerError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServerError();
        }

        return response;
    }

    protected void execute() throws ResourceNotFound, BadRequest, ServerError
    {
        Method action = getActionInstance();

        if (action != null)
        {
            try
            {
                action.invoke(this);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                throw new ServerError();
            }
        }
        else
        {
            throw new ResourceNotFound();
        }

    }

    private Method getActionInstance()
    {
        Class<? extends Controller> controller = this.getClass();

        /*
         * Select the required Action of the Controller
		 */
        String actionName = request.getAction();
        Method method;
        try
        {
            method = controller.getDeclaredMethod(actionName);
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            throw new ResourceNotFound();
        }

        return method;
    }
}
