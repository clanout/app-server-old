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
//        System.out.println(OffsetDateTime.now(ZoneOffset.UTC));
        Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();

        PostgreDatabaseAdapter postgreDatabaseAdapter = new PostgreDatabaseAdapter();
        postgreDatabaseAdapter.init();

        UserRepository userRepository = RepositoryFactory.create(User.class);
        EventRepository eventRepository = RepositoryFactory.create(Event.class);

        User gaurav = userRepository.get("976303355745864");

        User aditya = userRepository.getFromUsername("aytida77@gmail.com");

//        eventRepository.createPhoneInvitations("0ca7affe-d3d0-4d34-aeb6-35d2059d32f6", gaurav, Arrays.asList("+917760747507"));
//        eventRepository.createPhoneInvitations("1ca7affe-d3d0-4d34-aeb6-35d2059d32f6", gaurav, Arrays.asList("+917760747507"));
//        System.out.println(gson.toJson(eventRepository.processPendingInvitations(aditya, "+917760747507")));
//
//        eventRepository.processPendingInvitations(user, "+917022014321");

//        eventRepository.createPhoneInvitations("dbdae453-dda6-4480-977e-ea0bb0539ab7", user, Arrays.asList("+917022014321"));

//        System.out.println(gson.toJson(eventRepository.getDetails("542239ca-2323-4a2b-afa9-754f101741cb", user)));

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
