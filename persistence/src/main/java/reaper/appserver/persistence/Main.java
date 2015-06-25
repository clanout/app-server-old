package reaper.appserver.persistence;

import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.core.postgre.PostgreDatabaseAdapter;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserRepository;

import java.time.OffsetDateTime;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        PostgreDatabaseAdapter postgreDatabaseAdapter = new PostgreDatabaseAdapter();
        postgreDatabaseAdapter.init();

        UserRepository userRepository = RepositoryFactory.create(User.class);
        EventRepository eventRepository = RepositoryFactory.create(Event.class);

        User user = userRepository.get("9320369679");

        Event event = eventRepository.get("efcc35d5-4dda-4d9e-be4a-f0a295fda7f2", user);
        event.setStartTime(OffsetDateTime.now());
        event.setEndTime(event.getStartTime().plusHours(3));

        eventRepository.update(event, user, "New Description!!");

        postgreDatabaseAdapter.close();
    }

    public static void create()
    {

    }
}
