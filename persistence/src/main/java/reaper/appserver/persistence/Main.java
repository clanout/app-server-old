package reaper.appserver.persistence;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.core.postgre.PostgreDatabaseAdapter;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
import reaper.appserver.persistence.model.user.UserRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();

        PostgreDatabaseAdapter postgreDatabaseAdapter = new PostgreDatabaseAdapter();
        postgreDatabaseAdapter.init();

        UserRepository userRepository = RepositoryFactory.create(User.class);
        EventRepository eventRepository = RepositoryFactory.create(Event.class);

        User aditya = userRepository.getFromUsername("aytida77@gmail.com");
        System.out.println(gson.toJson(eventRepository.getVisibleEvents(aditya, "Bengaluru")));

        postgreDatabaseAdapter.close();
    }

    public static Event create(User organizer)
    {
        Event event = new Event();
        event.setTitle("Event Duo");
        event.setType(Event.Type.PUBLIC);
        event.setCategory("GENERAL");
        event.setOrganizerId(organizer.getId());
        event.setIsFinalized(false);

        event.setStartTime(OffsetDateTime.now().plusHours(3));
        event.setEndTime(event.getStartTime().plusHours(2));

        Event.Location location = new Event.Location();
        location.setZone("Bengaluru");
        event.setLocation(location);

        return event;
    }

    public static Event update(Event event)
    {
        Event.Location location = new Event.Location();
        location.setName("Koramangla");
        location.setLatitude(17.25);
        location.setLongitude(72.35);
        location.setZone("Bengaluru");
        event.setLocation(location);
        return event;
    }
}
