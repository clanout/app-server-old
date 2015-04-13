package reaper.appserver.core.framework.exceptions;

public class BadRequest extends RuntimeException
{
    public BadRequest()
    {
        super("[BAD REQUEST]");
    }

    public BadRequest(String message)
    {
        super("[BAD REQUEST] " + message);
    }
}
