package reaper.appserver.core.app.service.notification.api.request;

import com.google.gson.annotations.SerializedName;

public class NotificationRegistrationRequest
{
    @SerializedName("user_id")
    private String userId;

    @SerializedName("token")
    private String token;

    public NotificationRegistrationRequest(String userId, String token)
    {
        this.userId = userId;
        this.token = token;
    }
}
