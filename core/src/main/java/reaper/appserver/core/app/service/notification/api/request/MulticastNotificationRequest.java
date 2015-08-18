package reaper.appserver.core.app.service.notification.api.request;

import com.google.gson.annotations.SerializedName;
import reaper.appserver.core.app.service.notification.Notification;

import java.util.Set;

public class MulticastNotificationRequest
{
    @SerializedName("to")
    private Set<String> userIds;

    @SerializedName("data")
    private Notification notification;

    public MulticastNotificationRequest(Set<String> userIds, Notification notification)
    {
        this.userIds = userIds;
        this.notification = notification;
    }
}
