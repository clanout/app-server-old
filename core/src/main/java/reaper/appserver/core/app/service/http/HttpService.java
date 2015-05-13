package reaper.appserver.core.app.service.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpService
{
    public String post(String url, String jsonData) throws Exception
    {
        HttpClient client = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(jsonData, ContentType.APPLICATION_JSON));

        HttpResponse response = client.execute(post);

        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode == 200)
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer responseBody = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null)
            {
                responseBody.append(line);
            }

            return responseBody.toString();

        }
        else if (responseCode == 404)
        {
            throw new Exception("HTTP_404");
        }
        else if (responseCode == 400)
        {
            throw new Exception("HTTP_400");
        }
        else
        {
            throw new Exception("HTTP error");
        }
    }

    public String get(String url) throws Exception
    {
        HttpClient client = HttpClientBuilder.create().build();

        HttpGet get = new HttpGet(url);

        HttpResponse response = client.execute(get);

        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode == 200)
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer responseBody = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null)
            {
                responseBody.append(line);
            }

            return responseBody.toString();

        }
        else if (responseCode == 404)
        {
            throw new Exception("HTTP_404");
        }
        else if (responseCode == 400)
        {
            throw new Exception("HTTP_400");
        }
        else
        {
            throw new Exception("HTTP error");
        }
    }
}
