package reaper.appserver.persistence.model.user;

import java.util.List;

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
    }

    private String id;
    private List<Friend> friends;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<Friend> getFriends()
    {
        return friends;
    }

    public void setFriends(List<Friend> friends)
    {
        this.friends = friends;
    }
}
