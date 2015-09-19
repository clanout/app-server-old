package reaper.appserver.core.app.service.user;

import org.apache.log4j.Logger;
import reaper.appserver.core.app.service.chat.ChatService;
import reaper.appserver.core.app.service.notification.NotificationService;
import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.core.framework.util.GsonProvider;
import reaper.appserver.log.LogUtil;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
import reaper.appserver.persistence.model.user.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class UserService
{
    private static Logger LOG = LogUtil.getLogger(UserService.class);

    private UserRepository userRepository;
    private NotificationService notificationService;
    private ChatService chatService;

    public UserService()
    {
        userRepository = RepositoryFactory.create(User.class);
        notificationService = new NotificationService();
        chatService = new ChatService();
    }

    public void create(String userId, String username, String firstname, String lastname, String gender, List<String> friends)
    {
        if (userId == null || userId.isEmpty())
        {
            throw new BadRequest("create User : userId cannot be null/empty");
        }

        if (username == null || username.isEmpty())
        {
            throw new BadRequest("create User : username cannot be null/empty");
        }

        if (firstname == null || firstname.isEmpty())
        {
            throw new BadRequest("create User : firstname cannot be null/empty");
        }

        if (lastname == null || lastname.isEmpty())
        {
            throw new BadRequest("create User : lastname cannot be null/empty");
        }

        User.Gender userGender = null;
        if (gender == null || gender.isEmpty())
        {
            userGender = User.Gender.NOT_SPECIFIED;
        }
        else if (gender.equalsIgnoreCase("female"))
        {
            userGender = User.Gender.FEMALE;
        }
        else if (gender.equalsIgnoreCase("male"))
        {
            userGender = User.Gender.MALE;
        }
        else
        {
            userGender = User.Gender.OTHER;
        }

        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setGender(userGender);
        user.setRegistrationTime(OffsetDateTime.now());
        user.setStatus(User.Status.ACTIVE);

        if (userRepository.create(user) == null)
        {
            throw new ServerError();
        }

        LOG.info("[NEW USER] " + GsonProvider.getGson().toJson(user));

        addFriends(user, friends);
        LOG.info("[NEW USER - FRIENDS] " + GsonProvider.getGson().toJson(friends));

        notificationService.newUser(user);

        chatService.createUser(user);
    }

    public User get(String userId)
    {
        return userRepository.get(userId);
    }

    public Set<UserDetails.Friend> getFriends(User user)
    {
        UserDetails userDetails = userRepository.getUserDetails(user.getId());
        if (userDetails == null)
        {
            throw new ServerError("Unable to fetch friends for user_id = " + user.getId());
        }

        Set<UserDetails.Friend> friends = userDetails.getFriends();

        return friends;
    }

    public Set<UserDetails.Friend> getLocalFriends(User user, String zone)
    {
        UserDetails userDetails = userRepository.getUserDetailsLocal(user.getId());
        if (userDetails == null)
        {
            throw new ServerError("Unable to local friends for user_id = " + user.getId());
        }

        Set<UserDetails.Friend> friends = userDetails.getFriends();

        return friends;
    }

    public void toggleBlock(User user, List<String> blockIds, List<String> unblockIds)
    {
        userRepository.unblock(user, unblockIds);
        userRepository.block(user, blockIds);

        notificationService.usersBlocked(user, blockIds);
        notificationService.usersUnblocked(user, unblockIds);
    }

    public void toggleFavourite(User user, List<String> favouriteIds, List<String> unfavouriteIds)
    {
        userRepository.unfavourite(user, unfavouriteIds);
        userRepository.favourite(user, favouriteIds);
    }

    public Set<UserDetails.Friend> getRegisteredContacts(User user, List<String> contacts)
    {
        UserDetails userDetails = userRepository.getRegisteredContacts(user, contacts);
        if (userDetails == null)
        {
            throw new ServerError("Unable to fetch registered contacts for user_id = " + user.getId());
        }

        Set<UserDetails.Friend> registeredContacts = userDetails.getFriends();

        return registeredContacts;
    }

    public Set<UserDetails.Friend> getLocalRegisteredContacts(User user, List<String> contacts, String zone)
    {
        UserDetails userDetails = userRepository.getLocalRegisteredContacts(user, contacts, zone);
        if (userDetails == null)
        {
            throw new ServerError("Unable to fetch local registered contacts for user_id = " + user.getId());
        }

        Set<UserDetails.Friend> registeredContacts = userDetails.getFriends();

        return registeredContacts;
    }

    public void setPhone(User user, String phone)
    {
        if (phone == null || phone.isEmpty())
        {
            throw new BadRequest("phone number cannot be null/empty");
        }

        userRepository.setPhone(user, phone);
    }

    public void setLocation(User user, String zone)
    {
        if (zone == null || zone.isEmpty())
        {
            throw new BadRequest("user location cannot be null/empty");
        }

        if (userRepository.updateLocation(user, zone))
        {
            notificationService.userRelocated(user, zone);
        }
    }

    public void addFriends(User user, List<String> userIds)
    {
        userRepository.addFriends(user, userIds);
    }
}
