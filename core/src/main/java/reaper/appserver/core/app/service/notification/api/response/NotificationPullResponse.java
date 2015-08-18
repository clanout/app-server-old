package reaper.appserver.core.app.service.notification.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationPullResponse
{
    @SerializedName("notifications")
    private List<String> notifications;

    public List<String> getNotifications()
    {
        return notifications;
    }
}
