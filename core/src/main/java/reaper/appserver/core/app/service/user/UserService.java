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

    public List<UserDetails.Friend> getFriends(User user)
    {
        UserDetails userDetails = userRepository.getUserDetails(user);
        if (userDetails == null)
        {
            throw new ServerError("Unable to fetch user details for user_id = " + user.getId());
        }

        List<UserDetails.Friend> friends = userDetails.getFriends();
        if (friends == null)
        {
            throw new ServerError("Friend list null for user_id = " + user.getId());
        }

        return friends;
    }

    public void toggleBlock(User user, List<String> userIdList)
    {
        userRepository.toggleBlock(user, userIdList);
    }

    public void toggleFavourite(User user, List<String> userIdList)
    {
        userRepository.toggleFavourite(user, userIdList);
    }

    public List<Event> getArchive(User user)
    {
        EventRepository eventRepository = RepositoryFactory.create(Event.class);
        return eventRepository.getArchive(user);
    }
}
