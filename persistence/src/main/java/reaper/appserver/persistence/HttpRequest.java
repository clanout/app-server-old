package reaper.appserver.persistence;

import com.google.gson.Gson;

import javax.sound.midi.Soundbank;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by Aditya on 05-04-2015.
 */
public class HttpRequest
{
    public String sendRequest(String url, Map<String, String> postData, String context) throws Exception
    {
        if (postData == null)
        {
            throw new Exception();
        }

        // Add session cookie to post data
        String sessionId = context;
        if (sessionId != null)
        {
            postData.put("SESSIONID", sessionId);
        }

        // URL
        URL urlObject = new URL(url);

        HttpURLConnection connection = null;
        try
        {
            // Open Http Connection
            connection = (HttpURLConnection) urlObject.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(10000);

            // Set post data
            String postDataStream = processPostData(postData);
            System.out.println(postDataStream);
            if (postData != null)
            {
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);

                BufferedWriter os = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                os.write(postDataStream);

                os.close();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                // Process Response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder result = new StringBuilder();
                while ((line = in.readLine()) != null)
                {
                    result.append(line);
                }

                return result.toString();
            }
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
        return null;
    }

    private String processPostData(Map<String, String> postData)
    {
        if (postData.size() == 0)
        {
            return null;
        }

        Gson gson = new Gson();
        String postJson = gson.toJson(postData);
        System.out.println(postJson);
        return postJson;
    }

}
