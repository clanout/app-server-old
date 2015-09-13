package reaper.appserver.core.app.service.notification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Notification
{
    private Type type;
    private String message;
    private Map<String, String> parameters;

    public enum Type
    {
        EVENT_CREATED,

        EVENT_ADDED,
        EVENT_REMOVED,
        EVENT_UPDATED,
        EVENT_INVITATION,
        FRIEND_RELOCATED,
        BLOCKED,
        UNBLOCKED
    }

    private Notification(Type type)
    {
        this.type = type;
        this.parameters = new HashMap<>();
    }

    public static class Builder
    {
        private Notification notification;

        public Builder(Type type)
        {
            notification = new Notification(type);
            notification.parameters.put("notification_id", UUID.randomUUID().toString());
        }

        public Notification build()
        {
            return notification;
        }

        public Builder message(String message)
        {
            notification.message = message;
            return this;
        }

        public Builder addParameter(String key, String value)
        {
            notification.parameters.put(key, value);
            return this;
        }
    }
}
