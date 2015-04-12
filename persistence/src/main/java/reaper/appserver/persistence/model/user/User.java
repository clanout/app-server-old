package reaper.appserver.persistence.model.user;

import reaper.appserver.persistence.core.Entity;

import java.util.Calendar;

public class User implements Entity
{
    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String gender;
    private String phone;
    private Calendar registered;
    private UserStatus status;

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

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Calendar getRegistered()
    {
        return registered;
    }

    public void setRegistered(Calendar registered)
    {
        this.registered = registered;
    }

    public UserStatus getStatus()
    {
        return status;
    }

    public void setStatus(UserStatus status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(username);
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
