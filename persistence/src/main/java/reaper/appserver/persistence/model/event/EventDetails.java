package reaper.appserver.persistence.model.event;

import java.util.Set;

public class EventDetails
{
    private String id;
    private String description;
    private Set<Attendee> attendees;
    private Set<Invitee> invitee;

    public String getId()
    {
        return id;
    }

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

    public Set<Attendee> getAttendees()
    {
        return attendees;
    }

    public void setAttendees(Set<Attendee> attendees)
    {
        this.attendees = attendees;
    }

    public Set<Invitee> getInvitee()
    {
        return invitee;
    }

    public void setInvitee(Set<Invitee> invitee)
    {
        this.invitee = invitee;
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

    public static class Attendee
    {
        private String id;
        private String name;
        private Event.RSVP rsvp;
        private String status;
        private boolean isFriend;
        private boolean isInviter;

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

        public Event.RSVP getRsvp()
        {
            return rsvp;
        }

        public void setRsvp(Event.RSVP rsvp)
        {
            this.rsvp = rsvp;
        }

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }

        public boolean isFriend()
        {
            return isFriend;
        }

        public void setIsFriend(boolean isFriend)
        {
            this.isFriend = isFriend;
        }

        public boolean isInviter()
        {
            return isInviter;
        }

        public void setIsInviter(boolean isInviter)
        {
            this.isInviter = isInviter;
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

            if (!(o instanceof Attendee))
            {
                return false;
            }
            else
            {
                Attendee other = (Attendee) o;
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

    public static class Invitee
    {
        private String id;
        private String name;

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

            if (!(o instanceof Invitee))
            {
                return false;
            }
            else
            {
                Invitee other = (Invitee) o;
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
}
