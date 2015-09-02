package reaper.appserver.persistence.model.user;

import reaper.appserver.persistence.core.Repository;

import java.util.List;

 public interface UserRepository extends Repository<User>
{
     User getFromUsername(String username);

     UserDetails getUserDetails(String id);

     UserDetails getUserDetailsLocal(String id, String zone);

     void block(User user, List<String> userIds);

     void unblock(User user, List<String> userIds);

     void favourite(User user, List<String> userIds);

     void unfavourite(User user, List<String> userIds);

     void addFriends(User user, List<String> userIds);

     UserDetails getRegisteredContacts(User user, List<String> contacts);

     UserDetails getLocalRegisteredContacts(User user, List<String> contacts, String zone);

     void setPhone(User user, String phone);

     boolean updateLocation(User user, String zone);
}
