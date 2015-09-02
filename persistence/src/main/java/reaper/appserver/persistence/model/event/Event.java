package reaper.appserver.persistence.model.event;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import reaper.appserver.persistence.core.Entity;
import reaper.appserver.persistence.core.GsonProvider;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public class Event implements Entity
{
    private String id;
    private String organizerId;
    private String title;
    private Type type;
    private String category;
    private String chatId;

    private boolean isFinalized;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Location location;

    private int attendeeCount;

    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;

    private RSVP rsvp;
    private int friendCount;
    private int inviterCount;

    @Override
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
        this.chatId = id;
    }

    public String getOrganizerId()
    {
        return organizerId;
    }

    public void setOrganizerId(String organizerId)
    {
        this.organizerId = organizerId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public boolean isFinalized()
    {
        return isFinalized;
    }

    public void setIsFinalized(boolean isFinalized)
    {
        this.isFinalized = isFinalized;
    }

    public OffsetDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime)
    {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime)
    {
        this.endTime = endTime;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public int getAttendeeCount()
    {
        return attendeeCount;
    }

    public void setAttendeeCount(int attendeeCount)
    {
        this.attendeeCount = attendeeCount;
    }

    public String getChatId()
    {
        return chatId;
    }

    public OffsetDateTime getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(OffsetDateTime createTime)
    {
        this.createTime = createTime;
    }

    public OffsetDateTime getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(OffsetDateTime updateTime)
    {
        this.updateTime = updateTime;
    }

    public RSVP getRsvp()
    {
        return rsvp;
    }

    public void setRsvp(RSVP rsvp)
    {
        this.rsvp = rsvp;
    }

    public int getFriendCount()
    {
        return friendCount;
    }

    public void setFriendCount(int friendCount)
    {
        this.friendCount = friendCount;
    }

    public int getInviterCount()
    {
        return inviterCount;
    }

    public void setInviterCount(int inviterCount)
    {
        this.inviterCount = inviterCount;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(id);
        stringBuilder.append(" : ");
        stringBuilder.append(type);
        stringBuilder.append(" : ");
        stringBuilder.append(title);
        stringBuilder.append(" : ");
        stringBuilder.append(startTime.toString());
        stringBuilder.append(" to ");
        stringBuilder.append(endTime.toString());
        stringBuilder.append(" : ");
        stringBuilder.append(location.toString());
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof Event))
        {
            return false;
        }
        else
        {
            Event other = (Event) o;
            if (id.equals(other.id))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public enum Type
    {
        PUBLIC(0),
        INVITE_ONLY(1);

        private int code;

        private Type(int code)
        {
            this.code = code;
        }

        public int getCode()
        {
            return code;
        }

        public static Type fromCode(int code)
        {
            if (code == 0)
            {
                return PUBLIC;
            }
            else
            {
                return INVITE_ONLY;
            }
        }
    }

    public enum RSVP
    {
        YES,
        MAYBE,
        NO
    }

    public static class Location
    {
        private Double longitude;
        private Double latitude;
        private String name;
        private String zone;

        public Double getLongitude()
        {
            return longitude;
        }

        public void setLongitude(double longitude)
        {
            this.longitude = longitude;
        }

        public Double getLatitude()
        {
            return latitude;
        }

        public void setLatitude(double latitude)
        {
            this.latitude = latitude;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getZone()
        {
            return zone;
        }

        public void setZone(String zone)
        {
            this.zone = zone;
        }

        @Override
        public String toString()
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            stringBuilder.append(name);
            stringBuilder.append(" : ");
            stringBuilder.append(zone);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    public static class Serializer
    {
        private static Gson gson;

        static
        {
            GsonBuilder gsonBuilder = GsonProvider.getGsonBuilder()
                    .serializeNulls()
                    .addSerializationExclusionStrategy(new SerializerStrategy());
            gson = gsonBuilder.create();
        }

        private static class SerializerStrategy implements ExclusionStrategy
        {
            private static List<String> excluded = Arrays.asList("rsvp", "friendCount", "inviterCount", "updateTime");

            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes)
            {
                if (excluded.contains(fieldAttributes.getName()))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass)
            {
                return false;
            }
        }

        public static String serialize(Event event)
        {
            return gson.toJson(event);
        }
    }
}
