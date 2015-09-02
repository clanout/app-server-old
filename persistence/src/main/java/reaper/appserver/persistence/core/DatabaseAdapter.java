package reaper.appserver.persistence.core;

public interface DatabaseAdapter
{
    void init() throws Exception;

    void close();
}
