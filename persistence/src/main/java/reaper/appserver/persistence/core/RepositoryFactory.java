package reaper.appserver.persistence.core;

public class RepositoryFactory
{
    public static <R extends Repository<E>, E extends Entity> R create(Class<E> clazz)
    {
        return null;
    }
}
