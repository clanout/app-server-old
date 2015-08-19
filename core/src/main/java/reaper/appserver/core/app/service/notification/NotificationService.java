package reaper.appserver.core.app.service.notification;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import reaper.appserver.core.app.service.event.EventService;
import reaper.appserver.core.app.service.notification.api.ApiManager;
import reaper.appserver.core.app.service.notification.api.NotificationApi;
import reaper.appserver.core.app.service.notification.api.request.BroadcastNotificationRequest;
import reaper.appserver.core.app.service.notification.api.request.MulticastNotificationRequest;
import reaper.appserver.core.app.service.notification.api.request.NotificationPullRequest;
import reaper.appserver.core.app.service.notification.api.request.NotificationRegistrationRequest;
import reaper.appserver.core.app.service.notification.api.response.NotificationPullResponse;
import reaper.appserver.core.app.service.user.UserService;
import reaper.appserver.log.LogUtil;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventDetails;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
import reaper.appserver.persistence.model.user.UserRepository;
import retrofit.client.Response;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationService
{
    private static Logger log = LogUtil.getLogger(NotificationService.class);

    private static Gson gson;

    static
    {
        gson = Converters.registerAll(new GsonBuilder()).create();
    }

    private NotificationApi api;
    private UserRepository userRepository;
    private EventRepository eventRepository;

    public NotificationService()
    {
        api = ApiManager.getApi();
        userRepository = RepositoryFactory.create(User.class);
        eventRepository = RepositoryFactory.create(Event.class);
    }

    public void register(String userId, String token)
    {
        NotificationRegistrationRequest registration = new NotificationRegistrationRequest(userId, token);
        Response response = api.register(registration);
        if (response.getStatus() != 200)
        {
            log.error("Notification Registration Failed for user = " + userId);
        }
    }

    public List<String> pull(String userId)
    {
        NotificationPullRequest request = new NotificationPullRequest(userId);
        NotificationPullResponse response = api.pull(request);
        return response.getNotifications();
    }

    public void eventCreated(User user, Event event)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.EVENT_ADDED)
                    .message(user.getFirstname() + " " + user.getLastname() + " created a new event '" + event.getTitle() + "'")
                    .addParameter("event_id", event.getId())
                    .build();

            Set<UserDetails.Friend> friends = userRepository.getUserDetails(user.getId()).getFriends();
            Set<String> userIds = new HashSet<>(friends.size());
            for (UserDetails.Friend friend : friends)
            {
                userIds.add(friend.getId());
            }

            MulticastNotificationRequest request = new MulticastNotificationRequest(userIds, notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + gson.toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void eventUpdated(User user, Event event)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.EVENT_UPDATED)
                    .message(user.getFirstname() + " " + user.getLastname() + " updated the event '" + event.getTitle() + "'")
                    .addParameter("event_id", event.getId())
                    .build();

            BroadcastNotificationRequest request = new BroadcastNotificationRequest(event.getId(), notification);
            Response response = api.broadcast(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + gson.toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void eventDeleted(User user, Event event)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.EVENT_REMOVED)
                    .message(user.getFirstname() + " " + user.getLastname() + " deleted the event '" + event.getTitle() + "'")
                    .addParameter("event_id", event.getId())
                    .build();

            BroadcastNotificationRequest request = new BroadcastNotificationRequest(event.getId(), notification);
            Response response = api.broadcast(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + gson.toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void rsvpChanged(User user, Event event, Event.RSVP rsvp)
    {
        try
        {
            Notification notification = null;

            if (rsvp == Event.RSVP.YES)
            {
                notification = new Notification.Builder(Notification.Type.EVENT_ADDED)
                        .message(user.getFirstname() + " " + user.getLastname() + " is attending the event '" + event.getTitle() + "'")
                        .addParameter("event_id", event.getId())
                        .build();
            }
            else
            {
                notification = new Notification.Builder(Notification.Type.EVENT_REMOVED)
                        .message(user.getFirstname() + " " + user.getLastname() + " is no longer attending the event '" + event.getTitle() + "'")
                        .addParameter("event_id", event.getId())
                        .build();
            }

            Set<UserDetails.Friend> friends = userRepository.getUserDetails(user.getId()).getFriends();
            Set<String> userIds = new HashSet<>(friends.size());
            for (UserDetails.Friend friend : friends)
            {
                userIds.add(friend.getId());
            }

            MulticastNotificationRequest request = new MulticastNotificationRequest(userIds, notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + gson.toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification Failed [" + e.getMessage() + "]");
        }
    }

    public void eventInvitation(User from, List<String> to, Event event)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.EVENT_INVITATION)
                    .message(from.getFirstname() + " " + from.getLastname() + " invited you to the event '" + event.getTitle() + "'")
                    .addParameter("event_id", event.getId())
                    .addParameter("user_id", from.getId())
                    .build();

            Set<String> userIds = new HashSet<>(to);

            MulticastNotificationRequest request = new MulticastNotificationRequest(userIds, notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + gson.toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void userRelocated(User user, String newZone)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.FRIEND_RELOCATED)
                    .addParameter("user_id", user.getId())
                    .addParameter("name", user.getFirstname() + " " + user.getLastname())
                    .addParameter("zone", newZone)
                    .build();

            Set<UserDetails.Friend> friends = userRepository.getUserDetails(user.getId()).getFriends();
            Set<String> userIds = new HashSet<>(friends.size());
            for (UserDetails.Friend friend : friends)
            {
                userIds.add(friend.getId());
            }

            MulticastNotificationRequest request = new MulticastNotificationRequest(userIds, notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + gson.toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }
}
