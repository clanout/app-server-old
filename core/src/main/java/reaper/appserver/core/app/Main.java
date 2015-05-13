package reaper.appserver.core.app;

import com.google.gson.GsonBuilder;
import reaper.appserver.core.app.service.recommendation.Recommendation;
import reaper.appserver.core.app.service.recommendation.RecommendationService;

import java.util.List;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        RecommendationService service = new RecommendationService();
        List<Recommendation> recommendations = service.getRecommendations("12.9259", "77.6229", "EAT_OUT");
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(recommendations));
    }
}
