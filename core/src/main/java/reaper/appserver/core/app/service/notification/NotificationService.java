package reaper.appserver.core.app.service.notification;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import reaper.appserver.core.app.service.event.EventService;
import reaper.appserver.core.app.service.notification.api.ApiManager;
import reaper.appserver.core.app.service.notification.api.NotificationApi;
import reaper.appserver.core.app.service.notification.api.request.MulticastNotificationRequest;
import reaper.appserver.core.app.service.notification.api.request.NotificationPullRequest;
import reaper.appserver.core.app.service.notification.api.request.NotificationRegistrationRequest;
import reaper.appserver.core.app.service.notification.api.response.NotificationPullResponse;
import reaper.appserver.core.app.service.user.UserService;
import reaper.appserver.log.LogUtil;
import reaper.appserver.persistence.model.event.Event;
import reaper.appserver.persistence.model.user.User;
import reaper.appserver.persistence.model.user.UserDetails;
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
    private UserService userService;
    private EventService eventService;

    public NotificationService()
    {
        api = ApiManager.getApi();
        userService = new UserService();
        eventService = new EventService();
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
        Notification notification = new Notification.Builder(Notification.Type.EVENT_ADD)
                .message(user.getFirstname() + " created a new event titled " + event.getTitle())
                .addParameter("event_id", event.getId())
                .build();

        Set<UserDetails.Friend> friends = userService.getFriends(user);
        Set<String> userIds = new HashSet<>(friends.size());
        for (UserDetails.Friend friend : friends)
        {
            userIds.add(friend.getId());
        }

        MulticastNotificationRequest multicastNotificationRequest = new MulticastNotificationRequest(userIds, notification);
        Response response = api.send(multicastNotificationRequest);
        if (response.getStatus() != 200)
        {
            log.error("Notification failed : " + gson.toJson(multicastNotificationRequest));
        }
    }

    public void eventUpdated(User user, Event event)
    {
    }
}
