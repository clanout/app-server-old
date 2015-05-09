package reaper.appserver.core.app.service.user;

import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
import reaper.appserver.persistence.model.user.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class UserService
{
    private UserRepository userRepository;

    public UserService()
    {
        userRepository = RepositoryFactory.create(User.class);
    }

    public void create(String userId, String username, String firstname, String lastname, String gender)
    {
        if (userId == null || userId.isEmpty())
        {
            throw new BadRequest("creare User : userId cannot be null/empty");
        }

        if (username == null || username.isEmpty())
        {
            throw new BadRequest("creare User : username cannot be null/empty");
        }

        if (firstname == null || firstname.isEmpty())
        {
            throw new BadRequest("creare User : firstname cannot be null/empty");
        }

        if (lastname == null || lastname.isEmpty())
        {
            throw new BadRequest("creare User : lastname cannot be null/empty");
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
            throw new ServerError("Unable to fetch user details for user_id = " + user.getId());
        }

        Set<UserDetails.Friend> friends = userDetails.getFriends();

        return friends;
    }

    public Set<UserDetails.Friend> getLocalFriends(User user, String zone)
    {
        UserDetails userDetails = userRepository.getUserDetailsLocal(user.getId(), zone);
        if (userDetails == null)
        {
            throw new ServerError("Unable to fetch user details for user_id = " + user.getId());
        }

        Set<UserDetails.Friend> friends = userDetails.getFriends();

        return friends;
    }

    public void toggleBlock(User user, List<String> blockIds, List<String> unblockIds)
    {
        userRepository.unblock(user, unblockIds);
        userRepository.block(user, blockIds);
    }

    public void toggleFavourite(User user, List<String> favouriteIds, List<String> unfavouriteIds)
    {
        userRepository.unfavourite(user, unfavouriteIds);
        userRepository.favourite(user, favouriteIds);
    }

    public List<Event> getArchive(User user)
    {
        EventRepository eventRepository = RepositoryFactory.create(Event.class);
        return eventRepository.getArchive(user);
    }
}
