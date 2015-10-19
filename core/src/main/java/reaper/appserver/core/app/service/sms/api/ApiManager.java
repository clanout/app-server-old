package reaper.appserver.core.app.service.sms.api;

import com.squareup.okhttp.OkHttpClient;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.core.app.service.notification.api.NotificationApi;
import reaper.appserver.core.framework.util.GsonProvider;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class ApiManager
{
    private static RestAdapter restAdapter;

    public static SmsApi getApi()
    {
        if(restAdapter == null)
        {
            restAdapter = new RestAdapter.Builder()
                    .setClient(new OkClient(new OkHttpClient()))
                    .setEndpoint(ConfLoader.getConf(ConfResource.SMS).get("sms.endpoint"))
                    .setConverter(new GsonConverter(GsonProvider.getGson()))
                    .build();
        }
        return restAdapter.create(SmsApi.class);
    }
}
