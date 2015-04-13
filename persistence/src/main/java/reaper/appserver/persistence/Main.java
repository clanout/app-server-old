package reaper.appserver.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.core.postgre.PostgreDatabaseAdapter;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserRepository;

import java.time.OffsetDateTime;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        PostgreDatabaseAdapter adapter = new PostgreDatabaseAdapter();
        adapter.init();

        read();

        adapter.close();

//        OffsetDateTime offsetDateTime = OffsetDateTime.now();
//        System.out.println(offsetDateTime);
//        Timestamp timestamp = Timestamp.from(offsetDateTime.toInstant());
//        System.out.println(timestamp);
//
//        Instant instant = Instant.ofEpochMilli(timestamp.getTime());
//        System.out.println(timestamp.toLocalDateTime());
//        System.out.println(OffsetDateTime);
//        OffsetDateTime offsetDateTime1 = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
//        System.out.println(offsetDateTime1);

    }

    public static void read()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        UserRepository userRepository = RepositoryFactory.create(User.class);

        User user = userRepository.getFromUsername("aytida77@gmail.com");
        System.out.println(gson.toJson(user));
    }

    public static void delete()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        UserRepository userRepository = RepositoryFactory.create(User.class);

        User user = userRepository.get("8fd638f7-c1f8-4a82-9b12-cfb5b8112c69");
        System.out.println(user);

        userRepository.remove(user);

        System.out.println(user);
    }

    public static void create()
    {
        UserRepository userRepository = RepositoryFactory.create(User.class);

        User user = new User();
        user.setId(String.valueOf(System.currentTimeMillis()));
        user.setUsername("aytida77@gmail.com");
        user.setPhone("+91-7760747507");
        user.setFirstname("Aditya");
        user.setLastname("Prasad");
        user.setGender(User.Gender.MALE);
        user.setStatus(User.Status.ACTIVE);

        OffsetDateTime time = OffsetDateTime.now();
        user.setRegistrationTime(time);

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(Date.from(Instant.now()));
//        user.setRegistrationTime(calendar);

        String userId = userRepository.create(user);

        System.out.println(userId);
        System.out.println(user);
    }
}
