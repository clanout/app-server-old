package reaper.appserver.persistence.core;

public class RepositoryActionNotAllowed extends RuntimeException
{
    public RepositoryActionNotAllowed()
    {
        super("Repository Error: Invalid Action");
    }
}
