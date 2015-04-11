package reaper.appserver.core.framework.request;

public interface Request
{
    public String getController();

    public String getAction();

    public String getData(String key);

    public String getSessionUser();
}