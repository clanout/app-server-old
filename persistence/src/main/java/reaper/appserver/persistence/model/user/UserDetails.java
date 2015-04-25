package reaper.appserver.persistence.model.user;

import java.util.Set;

public class UserDetails
{
    public static class Friend
    {
        private String id;
        private String name;
        private boolean isFavourite;
        private boolean isBlocked;

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

        public boolean isFavourite()
        {
            return isFavourite;
        }

        public void setFavourite(boolean isFavourite)
        {
            this.isFavourite = isFavourite;
        }

        public boolean isBlocked()
        {
            return isBlocked;
        }

        public void setBlocked(boolean isBlocked)
        {
            this.isBlocked = isBlocked;
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

            if (!(o instanceof Friend))
            {
                return false;
            }
            else
            {
                Friend other = (Friend) o;
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

    private String id;
    private Set<Friend> friends;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Set<Friend> getFriends()
    {
        return friends;
    }

    public void setFriends(Set<Friend> friends)
    {
        this.friends = friends;
    }
}
