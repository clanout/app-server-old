package reaper.appserver.persistence.core;

public interface Repository<E extends Entity>
{
    E get(String id);

    String create(E entity);

    void update(E entity);

    void remove(E entity);
}
