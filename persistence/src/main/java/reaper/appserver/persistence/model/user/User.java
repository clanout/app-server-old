package reaper.appserver.persistence.model.user;

import reaper.appserver.persistence.core.Entity;

import java.util.Calendar;

public class User implements Entity
{
    public static enum Status
    {
        ACTIVE,
        DISABLED,
        FLAGGED
    }

    public static enum Gender
    {
        MALE("M"),
        FEMALE("F"),
        OTHER("O"),
        NOT_SPECIFIED("NS");

        private String code;

        private Gender(String code)
        {
            this.code = code;
        }

        public String getCode()
        {
            return code;
        }

        public static Gender fromCode(String code)
        {
            if (code.equalsIgnoreCase("F"))
            {
                return FEMALE;
            }
            else if (code.equalsIgnoreCase("M"))
            {
                return MALE;
            }
            else if (code.equalsIgnoreCase("O"))
            {
                return OTHER;
            }
            else
            {
                return NOT_SPECIFIED;
            }
        }
    }

    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private Gender gender;
    private String phone;
    private Calendar registrationTime;
    private Status status;

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

    public Gender getGender()
    {
        return gender;
    }

    public void setGender(Gender gender)
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

    public Calendar getRegistrationTime()
    {
        return registrationTime;
    }

    public void setRegistrationTime(Calendar registrationTime)
    {
        this.registrationTime = registrationTime;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
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
