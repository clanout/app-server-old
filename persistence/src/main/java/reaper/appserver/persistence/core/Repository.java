package reaper.appserver.persistence.core;

public interface Repository<E extends Entity>
{
    public E get(String id);

    public String create(E entity);

    public void update(E entity);

    public void remove(E entity);
}
