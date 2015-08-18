package reaper.appserver.core.app.service.notification.api;

import com.google.gson.annotations.SerializedName;
import reaper.appserver.core.app.service.notification.Notification;

import java.util.List;
import java.util.Set;

public class MulticastNotification
{
    @SerializedName("to")
    private Set<String> userIds;

    @SerializedName("data")
    private Notification notification;

    public MulticastNotification(Set<String> userIds, Notification notification)
    {
        this.userIds = userIds;
        this.notification = notification;
    }
}
