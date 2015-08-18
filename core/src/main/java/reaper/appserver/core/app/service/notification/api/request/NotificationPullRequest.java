package reaper.appserver.core.app.service.notification.api.request;

import com.google.gson.annotations.SerializedName;

public class NotificationPullRequest
{
    @SerializedName("user_id")
    private String userId;

    public NotificationPullRequest(String userId)
    {
        this.userId = userId;
    }
}
