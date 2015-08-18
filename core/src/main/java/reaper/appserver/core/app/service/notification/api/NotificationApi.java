package reaper.appserver.core.app.service.notification.api;

import reaper.appserver.core.app.service.notification.api.request.BroadcastNotificationRequest;
import reaper.appserver.core.app.service.notification.api.request.MulticastNotificationRequest;
import reaper.appserver.core.app.service.notification.api.request.NotificationPullRequest;
import reaper.appserver.core.app.service.notification.api.request.NotificationRegistrationRequest;
import reaper.appserver.core.app.service.notification.api.response.NotificationPullResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface NotificationApi
{
    @POST("/register")
    Response register(@Body NotificationRegistrationRequest request);

    @POST("/pull")
    NotificationPullResponse pull(@Body NotificationPullRequest request);

    @POST("/send")
    Response send(@Body MulticastNotificationRequest request);

    @POST("/broadcast")
    Response broadcast(@Body BroadcastNotificationRequest request);
}
