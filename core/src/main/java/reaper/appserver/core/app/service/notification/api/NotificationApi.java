package reaper.appserver.core.app.service.notification.api;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface NotificationApi
{
    @POST("/send")
    Response send(@Body MulticastNotification notification);
}
