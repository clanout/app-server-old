package reaper.appserver.persistence.model.user;

import reaper.appserver.persistence.core.Entity;

public class User implements Entity
{
    private String id;
    private String username;
    private String firstname;
    private String lastname;

    @Override
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getLastname()
    {
        return lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(id);
        stringBuilder.append(" : ");
        stringBuilder.append(firstname);
        stringBuilder.append(" ");
        stringBuilder.append(lastname);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode()
    {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }

        if (!(o instanceof User))
        {
            return false;
        }
        else
        {
            User other = (User) o;
            if (username.equals(other.username))
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
