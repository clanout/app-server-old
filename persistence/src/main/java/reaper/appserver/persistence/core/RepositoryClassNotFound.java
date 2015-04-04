package reaper.appserver.persistence.core;

public class RepositoryClassNotFound extends RuntimeException
{
    public RepositoryClassNotFound()
    {
        super("Repository Error: Repository not found");
    }
}
