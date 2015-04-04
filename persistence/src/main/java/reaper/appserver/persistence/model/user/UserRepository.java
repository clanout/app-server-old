package reaper.appserver.persistence.model.user;

import reaper.appserver.persistence.core.Repository;

public interface UserRepository extends Repository<User>
{
    public User getFromUsername(String username);
}
