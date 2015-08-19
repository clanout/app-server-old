package reaper.appserver.persistence.model.user;

import reaper.appserver.persistence.core.Repository;

import java.util.List;

public interface UserRepository extends Repository<User>
{
    public User getFromUsername(String username);

    public UserDetails getUserDetails(String id);

    public UserDetails getUserDetailsLocal(String id, String zone);

    public void block(User user, List<String> userIds);

    public void unblock(User user, List<String> userIds);

    public void favourite(User user, List<String> userIds);

    public void unfavourite(User user, List<String> userIds);

    public void addFriends(User user, List<String> userIds);

    public UserDetails getRegisteredContacts(User user, List<String> contacts);

    public UserDetails getLocalRegisteredContacts(User user, List<String> contacts, String zone);

    public void setPhone(User user, String phone);

    public boolean updateLocation(User user, String zone);
}
