package reaper.appserver.persistence.core;

public interface DatabaseAdapter
{
    public void init() throws Exception;

    public void close();
}
