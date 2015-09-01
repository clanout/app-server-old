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

        User user = userRepository.get("977526725631911");
        userRepository.addFriends(user, Arrays.asList("976303355745864"));
        Set<UserDetails.Friend> friends = userRepository.getUserDetails(user.getId()).getFriends();

        for (UserDetails.Friend friend : friends)
        {
            System.out.println(friend.getId() + " : " + friend.getName());
        }

//        List<Event> events = eventRepository.getVisibleEvents(user, "Bengaluru");
//
//        for(Event event : events)
//        {
//            System.out.println(event.getTitle() + " : " + event.getOrganizerId());
//        }

        postgreDatabaseAdapter.close();
    }

    public static void create()
    {

    }
}
