package reaper.appserver.persistence.model.event;

import java.util.List;

public class EventDetails
{
    public static class Attendee
    {
        private String id;
        private String name;
        private boolean isFriend;
        private Event.RSVP rsvp;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public boolean isFriend()
        {
            return isFriend;
        }

        public void setFriend(boolean isFriend)
        {
            this.isFriend = isFriend;
        }

        public Event.RSVP getRsvp()
        {
            return rsvp;
        }

        public void setRsvp(Event.RSVP rsvp)
        {
            this.rsvp = rsvp;
        }
    }

    public static class InviteListUser
    {
        private String id;
        private String name;
    }

    private String id;
    private String description;
    private List<Attendee> attendees;
    private List<InviteListUser> invited;
    private List<InviteListUser> inviters;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<Attendee> getAttendees()
    {
        return attendees;
    }

    public void setAttendees(List<Attendee> attendees)
    {
        this.attendees = attendees;
    }

    public List<InviteListUser> getInvited()
    {
        return invited;
    }

    public void setInvited(List<InviteListUser> invited)
    {
        this.invited = invited;
    }

    public List<InviteListUser> getInviters()
    {
        return inviters;
    }

    public void setInviters(List<InviteListUser> inviters)
    {
        this.inviters = inviters;
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

        if (!(o instanceof EventDetails))
        {
            return false;
        }
        else
        {
            EventDetails other = (EventDetails) o;
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
}
