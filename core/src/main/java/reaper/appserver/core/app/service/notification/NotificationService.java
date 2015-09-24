package reaper.appserver.core.app.service.notification;

import org.apache.log4j.Logger;
import reaper.appserver.core.app.service.notification.api.ApiManager;
import reaper.appserver.core.app.service.notification.api.NotificationApi;
import reaper.appserver.core.app.service.notification.api.request.BroadcastNotificationRequest;
import reaper.appserver.core.app.service.notification.api.request.MulticastNotificationRequest;
import reaper.appserver.core.app.service.notification.api.request.NotificationPullRequest;
import reaper.appserver.core.app.service.notification.api.request.NotificationRegistrationRequest;
import reaper.appserver.core.app.service.notification.api.response.NotificationPullResponse;
import reaper.appserver.core.framework.util.GsonProvider;
import reaper.appserver.log.LogUtil;
import reaper.appserver.persistence.core.RepositoryFactory;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.event.EventRepository;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
import reaper.appserver.persistence.model.user.UserRepository;
import retrofit.client.Response;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NotificationService
{
    private static Logger log = LogUtil.getLogger(NotificationService.class);

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

        try
        {
            Response response = api.register(registration);
            if (response.getStatus() != 200)
            {
                log.error("Notification Registration Failed for user = " + userId);
            }
        }
        catch (Exception e)
        {

        }
    }

    public List<String> pull(String userId)
    {
        NotificationPullRequest request = new NotificationPullRequest(userId);
        NotificationPullResponse response = api.pull(request);
        return response.getNotifications();
    }

    public void newUser(User user)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.NEW_FRIEND)
                    .addParameter("user_id", user.getId())
                    .addParameter("user_name", user.getFirstname() + " " + user.getLastname())
                    .build();

            Set<String> userIds = userRepository.getUserDetails(user.getId())
                    .getFriends()
                    .stream()
                    .map(UserDetails.Friend::getId)
                    .collect(Collectors.toSet());

            MulticastNotificationRequest request = new MulticastNotificationRequest(userIds, notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
            }
            else
            {
                log.info("Notification sent for new user : " + GsonProvider.getGson().toJson(notification));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void eventCreated(User user, Event event)
    {
        if (event.getType() == Event.Type.INVITE_ONLY)
        {
            return;
        }

        try
        {
            Notification notification = new Notification.Builder(Notification.Type.EVENT_CREATED)
                    .addParameter("event_id", event.getId())
                    .addParameter("event_name", event.getTitle())
                    .addParameter("user_name", user.getFirstname() + " " + user.getLastname())
                    .build();

            Set<String> userIds = userRepository.getUserDetailsLocal(user.getId())
                    .getFriends()
                    .stream()
                    .map(UserDetails.Friend::getId)
                    .collect(Collectors.toSet());

            MulticastNotificationRequest request = new MulticastNotificationRequest(userIds, notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void eventUpdated(User user, Event event, boolean isLocationUpdated, boolean isTimeUpdated)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.EVENT_UPDATED)
                    .addParameter("event_id", event.getId())
                    .addParameter("event_name", event.getTitle())
                    .addParameter("user_id", user.getId())
                    .addParameter("user_name", user.getFirstname() + " " + user.getLastname())
                    .addParameter("is_location_updated", String.valueOf(isLocationUpdated))
                    .addParameter("is_time_updated", String.valueOf(isTimeUpdated))
                    .build();

            BroadcastNotificationRequest request = new BroadcastNotificationRequest(event.getId(), notification);
            Response response = api.broadcast(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
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
                    .addParameter("event_id", event.getId())
                    .addParameter("event_name", event.getTitle())
                    .addParameter("user_id", user.getId())
                    .build();

            BroadcastNotificationRequest request = new BroadcastNotificationRequest(event.getId(), notification);
            Response response = api.broadcast(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void rsvpChanged(User user, Event event, Event.RSVP rsvp, Event.RSVP oldRsvp)
    {
        if (rsvp == Event.RSVP.NO || oldRsvp != Event.RSVP.NO)
        {
            return;
        }

        try
        {
            Notification notification = new Notification.Builder(Notification.Type.RSVP)
                    .addParameter("event_id", event.getId())
                    .addParameter("event_name", event.getTitle())
                    .addParameter("event_type", String.valueOf(event.getType()))
                    .addParameter("user_id", user.getId())
                    .addParameter("user_name", user.getFirstname() + " " + user.getLastname())
                    .build();

            Set<String> userIds = userRepository.getUserDetailsLocal(user.getId())
                    .getFriends()
                    .stream()
                    .map(UserDetails.Friend::getId)
                    .collect(Collectors.toSet());

            MulticastNotificationRequest request = new MulticastNotificationRequest(userIds, notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
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
                    .addParameter("event_id", event.getId())
                    .addParameter("event_name", event.getTitle())
                    .addParameter("user_name", from.getFirstname() + " " + from.getLastname())
                    .build();

            Set<String> userIds = new HashSet<>(to);

            MulticastNotificationRequest request = new MulticastNotificationRequest(userIds, notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
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

            Set<String> userIds = userRepository.getUserDetails(user.getId())
                    .getFriends()
                    .stream()
                    .map(UserDetails.Friend::getId)
                    .collect(Collectors.toSet());

            MulticastNotificationRequest request = new MulticastNotificationRequest(userIds, notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void usersBlocked(User user, List<String> blockedIds)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.BLOCKED)
                    .addParameter("user_id", user.getId())
                    .addParameter("name", user.getFirstname() + " " + user.getLastname())
                    .build();

            MulticastNotificationRequest request = new MulticastNotificationRequest(new HashSet<>(blockedIds), notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void usersUnblocked(User user, List<String> unblockedIds)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.UNBLOCKED)
                    .addParameter("user_id", user.getId())
                    .addParameter("name", user.getFirstname() + " " + user.getLastname())
                    .build();

            MulticastNotificationRequest request = new MulticastNotificationRequest(new HashSet<>(unblockedIds), notification);
            Response response = api.send(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }

    public void chatUpdate(String eventId, String eventName)
    {
        try
        {
            Notification notification = new Notification.Builder(Notification.Type.CHAT)
                    .addParameter("event_id", eventId)
                    .addParameter("event_name", eventName)
                    .build();

            BroadcastNotificationRequest request = new BroadcastNotificationRequest(eventId, notification);
            Response response = api.broadcast(request);
            if (response.getStatus() != 200)
            {
                log.error("Notification failed : " + GsonProvider.getGson().toJson(request));
            }
        }
        catch (Exception e)
        {
            log.error("Notification failed [" + e.getMessage() + "]");
        }
    }
}
