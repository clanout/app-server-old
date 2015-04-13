package reaper.appserver.core.framework.exceptions;

public class ResourceNotFound extends RuntimeException
{
    public ResourceNotFound()
    {
        super("[RESOURCE NOT FOUND]");
    }

    public ResourceNotFound(String resourceName)
    {
        super("[RESOURCE NOT FOUND] " + resourceName);
    }
}
