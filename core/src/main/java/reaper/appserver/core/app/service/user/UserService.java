package reaper.appserver.core.app.service.user;

import reaper.appserver.core.framework.exceptions.BadRequest;
import reaper.appserver.core.framework.exceptions.ServerError;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserRepository;

import java.time.OffsetDateTime;

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
}
