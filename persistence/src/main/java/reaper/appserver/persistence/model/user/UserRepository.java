package reaper.appserver.persistence.model.user;

import reaper.appserver.persistence.core.Repository;

import java.util.List;

public interface UserRepository extends Repository<User>
{
    public User getFromUsername(String username);

    public UserDetails getUserDetails(User user);

    public void toggleBlock(User user, List<String> userIds);

    public void toggleFavourite(User user, List<String> userIds);

    public void addFriends(User user, List<String> userIds);
}
