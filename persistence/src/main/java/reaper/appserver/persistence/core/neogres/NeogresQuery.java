package reaper.appserver.persistence.core.neogres;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class NeogresQuery
{
    public static String load(String path)
    {
        URL queryUrl = NeogresQuery.class.getResource("/neogres/" + path);
        try
        {
            URLConnection connection = queryUrl.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder query = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null)
            {
                query.append(line);
                query.append(" ");
            }
            return query.toString();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
