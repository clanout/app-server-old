package reaper.appserver.core.app.service.notification.api.request;

import com.google.gson.annotations.SerializedName;
import reaper.appserver.core.app.service.notification.Notification;

public class BroadcastNotificationRequest
{
    @SerializedName("to")
    private String channelId;

    @SerializedName("data")
    private Notification notification;

    public BroadcastNotificationRequest(String channelId, Notification notification)
    {
        this.channelId = channelId;
        this.notification = notification;
    }
}
