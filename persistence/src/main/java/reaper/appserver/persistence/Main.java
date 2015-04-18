package reaper.appserver.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.core.neogres.NeogresDatabaseAdapter;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
import reaper.appserver.persistence.model.user.UserRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        NeogresDatabaseAdapter databaseAdapter = new NeogresDatabaseAdapter();
        databaseAdapter.init();

        createEvent();

        databaseAdapter.close();
    }

    public static void createEvent()
    {
        EventRepository eventRepository = RepositoryFactory.create(Event.class);

        Event event = new Event();
        event.setOrganizerId("1168614936");
        event.setType(Event.Type.PUBLIC);
        event.setTitle("Dummy Event");
        event.setCategory("General");
        event.setStartTime(OffsetDateTime.now());
        event.setEndTime(OffsetDateTime.now().plusHours(1));
        event.setFinalized(false);

        Event.Location location = new Event.Location();
        location.setZone("Bangalore");

        event.setLocation(location);

        event.setChatId("12345");

        String eventid = eventRepository.create(event, "Dummy Description");
        System.out.println(eventid);
    }

    public static void load() throws Exception
    {
        UserRepository userRepository = RepositoryFactory.create(User.class);

        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:/dump.sql"));
        String line = "";
        while ((line = bufferedReader.readLine()) != null)
        {
            String tokens[] = line.split(",");

            User user = new User();
            user.setId(tokens[0]);
            user.setUsername(tokens[1]);
            user.setPhone(tokens[2]);
            user.setFirstname(tokens[3]);
            user.setLastname(tokens[4]);
            if (tokens[5].equalsIgnoreCase("f"))
            {
                user.setGender(User.Gender.FEMALE);
            }
            else
            {
                user.setGender(User.Gender.MALE);
            }
            user.setRegistrationTime(OffsetDateTime.parse(tokens[6].replace(" ", "T")));
            user.setStatus(User.Status.ACTIVE);

            String userId = userRepository.create(user);

            System.out.println(userId + "\t" + user);
        }
        bufferedReader.close();
    }

    public static void readDetails()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        UserRepository userRepository = RepositoryFactory.create(User.class);

        User user = userRepository.get("1168614936");
        System.out.println(user);

        UserDetails userDetails = userRepository.getUserDetails(user);
        System.out.println(gson.toJson(userDetails));
    }

    public static void addFriends() throws Exception
    {
        List<String> friends = Arrays.asList("1103675594");//,"1152889224"); //, "1716296053", "1952678247", "1952678247", "2110151006", "2110151006", "2205520504", "2357802924", "2357802924", "2545240997", "2905034189", "2979990076", "3088199575", "3094026607", "3568077948", "3681025577", "4160411515", "4832104744", "5562759834", "5750394386", "5851882983", "6140792440", "6240364405", "6797439046", "6921146402", "7133834467", "7133834467", "7482228748", "8170448043", "8259296468");
        Set<String> friend = new HashSet<>();
        friend.addAll(friends);


        UserRepository userRepository = RepositoryFactory.create(User.class);
        User user = userRepository.get("1168614936");
        System.out.println(user);

        userRepository.toggleBlock(user, friends);
    }
}
