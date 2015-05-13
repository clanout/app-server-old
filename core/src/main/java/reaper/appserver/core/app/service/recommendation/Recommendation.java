package reaper.appserver.core.app.service.recommendation;

public class Recommendation
{
    private String name;
    private String latitude;
    private String longitude;
    private String description;
    private String rating;
    private String iconUrl;

    public Recommendation(String name, String latitude, String longitude, String description, String rating, String iconUrl)
    {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.rating = rating;
        this.iconUrl = iconUrl;
    }

    @Override
    public int hashCode()
    {
        return name.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        else
        {
            if (!(o instanceof Recommendation))
            {
                return false;
            }
            else
            {
                Recommendation other = (Recommendation) o;
                if (((Recommendation) o).name.equalsIgnoreCase(name))
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
