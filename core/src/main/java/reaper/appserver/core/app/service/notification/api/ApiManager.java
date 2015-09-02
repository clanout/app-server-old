package reaper.appserver.core.app.service.notification.api;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import reaper.appserver.config.ConfLoader;
import reaper.appserver.config.ConfResource;
import reaper.appserver.core.framework.util.GsonProvider;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class ApiManager
{
    private static RestAdapter restAdapter;

    public static NotificationApi getApi()
    {
        if(restAdapter == null)
        {
            restAdapter = new RestAdapter.Builder()
                    .setClient(new OkClient(new OkHttpClient()))
                    .setEndpoint(ConfLoader.getConf(ConfResource.NOTIFICATION).get("notification.server.endpoint"))
                    .setConverter(new GsonConverter(GsonProvider.getGson()))
                    .build();
        }
        return restAdapter.create(NotificationApi.class);
    }
}
