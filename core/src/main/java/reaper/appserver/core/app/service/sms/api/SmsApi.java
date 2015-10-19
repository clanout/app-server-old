package reaper.appserver.core.app.service.sms.api;

import retrofit.http.GET;
import retrofit.http.Query;

public interface SmsApi
{
    @GET("/?template_name=Beta&response_format=json")
    SmsApiResponse sendBetaInvitation(@Query("sender_id") String senderId, @Query("username") String username,
                                      @Query("password") String password, @Query("destination") String destination,
                                      @Query("templateParameters[A]") String inviteeName,
                                      @Query("templateParameters[B]") String inviterName,
                                      @Query("templateParameters[C]") String clanTitle);
}
