package reaper.appserver.config;

import java.net.URL;

public enum ConfResource
{
    APP("app.conf"),
    SOURCE("source.conf"),
    DB("db.conf"),
    REPOSITORY("repository.conf"),
    CHAT("chat.conf"),
    RECOMMENDATION("recommendation.conf"),
    NOTIFICATION("notification.conf"),
    SMS("sms.conf");

    private String FILE;

    private ConfResource(String filename)
    {
        this.FILE = filename;
    }

    public URL getResource()
    {
        return ConfResource.class.getResource("/" + FILE);
    }
}
