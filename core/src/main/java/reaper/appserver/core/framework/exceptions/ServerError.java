package reaper.appserver.core.framework.exceptions;

public class ServerError extends RuntimeException
{
    public ServerError()
    {
        super("[SERVER ERROR]");
    }

    public ServerError(String message)
    {
        super("[SERVER ERROR] " + message);
    }
}
